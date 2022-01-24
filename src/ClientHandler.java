import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

// ClientHandler class
class ClientHandler {
    int mill_game_to_join_by_id = -1;
    int id; // same id as in db for the player
    DB_Connector db_con;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean should_receive = true;
    private boolean ready_to_play = false;
    private final InetAddress ip;
    private ClientHandler[] online_users;
    private ClientHandler other_ch;

    // GAME LOGIC //
    private Logic lgc;

    // Constructor
    public ClientHandler(int id, Socket socket, DB_Connector db_con)
    {
        this.id = id;
        this.clientSocket = socket;
        this.db_con = db_con;
        this.ip = clientSocket.getInetAddress();
    }

    public void run()
    {
        try {
            // get the outputted of client
            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            // get the inputted of client
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

            System.out.println("new CH_CON for " + ip.getHostAddress());
            new Thread(this::receive_data).start();
            // encapsulated vom main thread

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send_data(String data){
        out.println(data); // sending information to server
        out.flush();
    }

    public void receive_data() {
        while(should_receive){
            try {
                System.out.println(1);
                String data = in.readLine();
                // interpretation //

                if (data != null) {
                    System.out.println(data);
                    System.out.println(Arrays.toString(online_users));

                    if (data.contains("id")){ // get id from client
                        try { // getting the id from the user who logged in
                            this.id = Integer.parseInt(data.replace("id=", ""));
                        }
                        catch (NumberFormatException e){
                            System.out.println(data + " is not numeric");
                        }

                    } else if (data.contains("millId")){ // get mill id from client
                        try { // getting the id from the user who logged in
                            this.mill_game_to_join_by_id = Integer.parseInt(data.replace("millId=", ""));
                        }
                        catch (NumberFormatException e){
                            System.out.println(data + " is not numeric");
                        }

                    }else if (data.contains("update")){ // get mill id from client
                        update();

                    } else if (data.contains("giveUp")) {
                        init_lgc(mill_game_to_join_by_id, ip);
                        String player_color = data.replace("giveUp", "");
                        send_data("win" + (player_color.equals("white")?"black":"white"));
                        other_ch.send_data("win" + (player_color.equals("white")?"black":"white"));

                    } else if (data.contains("restart")) {
                        String player_color = data.replace("restart", "");
                        send_data("restart" + player_color);
                        other_ch.send_data("restart" + player_color);

                    } else if (ready_to_play){
                        Map<String,String> mill = db_con.getMillByID(mill_game_to_join_by_id, ip);
                        if (mill.isEmpty()){ // game dies
                            ready_to_play = false;
                            send_data("lobby");
                        }

                        else {
                            String tmpStateOfGame = lgc.current_game_state_as_string();
                            boolean isThereAMill = lgc.isMill();
                            System.out.println("current state of isMill: " + lgc.isMill());
                            if (lgc.interpret_client_request(data)) {
                                System.out.println("valid turn current game state: " + lgc.current_game_state_as_string());
                                send_data(lgc.current_game_state_as_string());
                                other_ch.send_data(lgc.current_game_state_as_string());
                                if (lgc.isMill()) {
                                    if ((isThereAMill)&&(!tmpStateOfGame.equals(lgc.current_game_state_as_string()))){
                                        lgc.setMill(false); // only if stone is taken
                                        other_ch.send_data("turnOn");
                                        send_data("turnOff");
                                    }

                                } else {
                                    if ((!tmpStateOfGame.equals(lgc.current_game_state_as_string()))){
                                        other_ch.send_data("turnOn");
                                        send_data("turnOff");
                                    }
                                }
                                System.out.println("current state of isMill: " + lgc.isMill());
                            }

                            if (lgc.isGameOver()){ // game over
                                ready_to_play = false;
                                send_data("win" + lgc.getIsWinner());
                                other_ch.send_data("win" + lgc.getIsWinner());
                            }
                        }
                    }
                    else if (data.contains("join")){
                        join_game();
                        ready_to_play = true;
                        update();

                    } else if (data.contains("start")){
                        start_game();
                        ready_to_play = true;
                        update();
                    }
                } else { // client dead
                    should_receive = false;
                    disconnect();
                }
            } catch (IOException e) { // could be client dead
                should_receive = false;
                try {
                    disconnect();
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                e.printStackTrace();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }

    }

    void disconnect() throws SQLException {
        online_users[id-1] = null; // setting player globally offline
        db_con.deactivate_player(id, ip);
        Map<String, String> mill = db_con.getMillByID(mill_game_to_join_by_id, ip);

        System.out.println(mill_game_to_join_by_id);
        System.out.println(mill);

        if (mill == null || mill.isEmpty()) {
            should_receive = false;
            System.out.println("set should_receive to false");
        } else {
            System.out.println("delete mill");
            db_con.delete_mill(mill_game_to_join_by_id, true, ip);
        }
        System.out.println("closed CH_CON to client " + ip.getHostAddress());
        update();
    }

    void join_game() throws SQLException {
        System.out.println("my mill id " + mill_game_to_join_by_id);
        System.out.println("my player id " + id);

        Map<String, String> tmpMill = db_con.getMillByID(mill_game_to_join_by_id, ip); // mill to join
        System.out.println(tmpMill);

        db_con.change_player_by_raw_input(id, null,
                null, null, -1,-1,"black", null, -1, ip);

        Map<String, String> waitingPlayer = db_con.getPlayerByID(Integer.parseInt(tmpMill.get("p1")), ip); // the waiting player
        System.out.println(waitingPlayer);

        Map<String, String> joiningPlayer = db_con.getPlayerByID(id, ip); // the waiting player
        System.out.println(joiningPlayer);

        for (ClientHandler user : online_users) {
            if (user==null) continue;
            if (user.getId() == Integer.parseInt(waitingPlayer.get("id"))) { // searching the ch of the waiting player
                System.out.println("his mill id " + user.mill_game_to_join_by_id);
                System.out.println("his player id " + user.id);

                // JOINING //
                Map<String, String> updatedMill = db_con.create_game_as_map(tmpMill.get("id"), tmpMill.get("p1"), joiningPlayer.get("id")); // insert joining into waiting game
                db_con.change_mill(mill_game_to_join_by_id, updatedMill, ip); // update mill game in db

                lgc = new Logic(mill_game_to_join_by_id); // connection the same logic
                init_lgc(mill_game_to_join_by_id, ip);
                user.setLgc(lgc);

                this.setOther_ch(user); // linking the ch`s of the users to each other
                user.setOther_ch(this);
                break;
            }
        }

        if ((int)(Math.random()*2) == 0){ // player who start is randomly chosen
            other_ch.send_data("turnOn");
            send_data("turnOff");
        } else {
            other_ch.send_data("turnOff");
            send_data("turnOn");
        }

        send_data("join");
    }

    void start_game() throws SQLException {
        int countMillGames = db_con.get_count_of_mills(ip);
        System.out.println("my player id " + id);

        db_con.change_player_by_raw_input(id, null,
                null, null, -1,-1,"white", null, -1, ip);

        this.mill_game_to_join_by_id = countMillGames+1; // setting mill id

        //Map<String, String> startingPlayer = db_con.getPlayerByID(id, ip); // the waiting player
        db_con.insert_mill(countMillGames+1, id, -1, ip);

        send_data("start");
    }

    void init_lgc(int mill_id, InetAddress ip) throws SQLException {
        // current game data //
        Map<String, String> mill = db_con.getMillByID(mill_id, ip);
        int id_p1 = Integer.parseInt(mill.get("p1"));
        int id_p2 = Integer.parseInt(mill.get("p2"));
        System.out.println(mill);

        Map<String, String> p1 = db_con.getPlayerByID(id_p1, ip);
        String color_p1 = p1.get("color");
        System.out.println(p1);

        Map<String, String> p2 = db_con.getPlayerByID(id_p2, ip);
        String color_p2 = p2.get("color");
        System.out.println(p2);

//        System.out.println(tmp_lgc.getP1());
        lgc.init(id_p1, id_p2);
//        System.out.println(tmp_lgc.getP1());


    }

    // update via db //
    void update() {
        for (ClientHandler ch : online_users){
            if (ch!=null && ch!=this) ch.send_data("update");
        }
    }

    public void setLgc(Logic lgc) {
        this.lgc = lgc;
    }

    public int getId() {
        return id;
    }

    public void setOnline_users(ClientHandler[] online_users) {
        this.online_users = online_users;
    }

    public void setOther_ch(ClientHandler other_ch) {
        this.other_ch = other_ch;
    }
}