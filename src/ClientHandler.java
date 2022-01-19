import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

// ClientHandler class
class ClientHandler {
    int mill_id = -1;
    int id;
    DB_Connector db_con;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean should_receive = true;
    private final InetAddress ip;

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
            // get the outputstream of client
            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            // get the inputstream of client
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

            System.out.println("new CH_CON for " + ip.getHostAddress());

            new Thread(this::receive_data).start();
            // abgekapselt vom main thread
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
                String data = in.readLine();
                // interpretation //

                if (data != null) {
                    System.out.println(data);
                    // interpret data in class logic //
                    update(data);
                    send_data("logic id: " + lgc.getId() + " Player One: " + lgc.getP1().toString() + " " + lgc.getTest());

                } else { // client dead
                    should_receive = false;
                }
            } catch (IOException | SQLException e) { // could be client dead
                should_receive = false;
                e.printStackTrace();
            }
        }
        try {
            disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void disconnect() throws SQLException {
        if (mill_id!=-1) {
            db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
            Map<String, String> mill = db_con.getMillByID(mill_id, ip);
            System.out.println(mill_id);
            System.out.println(mill);

            if (mill == null || mill.isEmpty()) {
                should_receive = false;
                System.out.println("set should_receive to false");
            } else {
                System.out.println("delete mill");
                db_con.delete_mill(Integer.parseInt(mill.get("id")), true, ip);
            }
            System.out.println("closed CH_CON to client " + ip.getHostAddress());
        }
    }

    // update via db //
    void update(String client_request) throws SQLException {
        // get values from logic //
        // than push to db //


        db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        // database acsess //

        // current game data //
        Map<String, String> mill = db_con.getMillByID(id, ip);

        // if mill doesnt exist anymore (cause other client dyed)
        // than check that and disconnect
        if (mill==null){
            disconnect();

        } else {
            int id_p1 = Integer.parseInt(mill.get("p1"));
            int id_p2 = Integer.parseInt(mill.get("p2"));
//        System.out.println(mill);

            Map<String, String> p1 = db_con.getPlayerByID(id_p1, ip);
//        System.out.println(p1);

            Map<String, String> p2 = db_con.getPlayerByID(id_p2, ip);
//        System.out.println(p2);
        }
    }

    public void setLgc(Logic lgc) {
        this.lgc = lgc;
    }

    public int getId() {
        return id;
    }

    public int getMill_id() {
        return mill_id;
    }

    public void setMill_id(int mill_id) {
        this.mill_id = mill_id;
    }
}