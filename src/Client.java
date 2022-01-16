import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

// Client class
class Client {
    private static PrintWriter out;
    private static BufferedReader in;
    private static boolean should_receive = true;
    private static Socket socket;
    private static Muehle mill;

    // driver code
    public static void main(String[] args)
    {
        mill = new Muehle();

    // establish a connection by providing host and port
        // number
        try {
            socket = new Socket("localhost", 1234);
            // writing to server
            out = new PrintWriter(
                    socket.getOutputStream(), true);

            // reading from server
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            new Thread(Client::receive_data).start(); // abgekapselt vom main thread
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send_data(String data){
        out.println(data); // sending information to server
        out.flush();
    }

    public static void receive_data() {
        while(should_receive){
            try {
                String data = in.readLine();
                // interpretation //

                if (data != null ) {
                    System.out.println(data);

                    Map data_as_map = string_to_map(data);
                    System.out.println(data_as_map);
                    // input string as map //
                    System.out.println(data_as_map);

                    String method = data_as_map.get("method").toString();
                    String myPlayer = mill.getP1().getColor();

                    switch(method){
                        case "init" -> { // setzt farben in beiden spielen gleich //
                            String player = data_as_map.get("player").toString();
                            mill.setP1(new Player(player, mill.getLabel_p1(), mill.getInfo(), player + ".png", player + "-red.png"));
                            mill.setP2((player.equals("white")) ?
                                    new Player("black", mill.getLabel_p1(), mill.getInfo(), "black.png", "black-red.png")
                                    :
                                    new Player("white", mill.getLabel_p2(), mill.getInfo(), "white.png", "white-red.png")
                            );

                            mill.getFrame().setTitle(player);

                            mill.start_game(mill);
                        }
                        case "turn_on" -> mill.setMy_turn(true);
                        case "turn_off" -> mill.setMy_turn(false);
                        case "set" -> {
                            String player = data_as_map.get("player").toString();
                            Cell cell = Muehle.getCells()[Integer.parseInt(data_as_map.get("cell").toString())];
                            if ((player.equals(myPlayer))) {
                                mill.getP1().setStone(cell, false);
                            } else {
                                mill.getP2().setStone(cell, false);
                            }

//                            System.out.println(mill.getCurrent_player().getStones_to_set());
//                            System.out.println(mill.getNext_player().getStones_to_set());
                        }
                        case "move" -> {
                            String player = data_as_map.get("player").toString();
                            Cell start_cell = Muehle.getCells()[Integer.parseInt(data_as_map.get("start_cell").toString())];
                            Cell end_cell = Muehle.getCells()[Integer.parseInt(data_as_map.get("end_cell").toString())];
                            if (player.equals(myPlayer)){
                                mill.getP1().moveStone(start_cell, end_cell, false);
                            }
                            else {
                                mill.getP2().moveStone(start_cell, end_cell, false);
                            }
                        }
                        case "take" -> {
                            String player = data_as_map.get("player").toString();
                            Cell cell = Muehle.getCells()[Integer.parseInt(data_as_map.get("cell").toString())];
                            if (player.equals(myPlayer)){
                                mill.getP1().takeStone(cell, false);
                                mill.getP2().setStones_in_game(mill.getP2().getStones_in_game() - 1);
                            }
                            else {
                                mill.getP2().takeStone(cell, false);
                                mill.getP1().setStones_in_game(mill.getP1().getStones_in_game() - 1);
                            }
                        }
                        case "win" -> {
                            String player = data_as_map.get("player").toString();
                            if (!player.equals(myPlayer)){
                                mill.winning(player, false);
                            }
                            else {
                                mill.winning((player.equals("white") ? "black" : "white"), false);
                            }
                            mill.getNeustart().setVisible(true);
                        }
                        case "aufgeben" -> {
                            String player = data_as_map.get("player").toString();
                            if (player.equals(myPlayer)){
                                mill.winning(player, true);
                            }
                            else {
                                mill.winning((player.equals("white") ? "black" : "white"), true);
                            }
                            mill.getNeustart().setVisible(true);
                        }
                        case "neustart" -> {
                            mill.close_game();
                            mill.start_game(mill);
                        }
                    }

                } else { // server dead
                    should_receive = false;
                }
            } catch (IOException e) { // could be server dead
                e.printStackTrace();
                should_receive = false;
            }
        }
    }

    public static Map string_to_map(String s){
        Map<String, String> hashMap
                = new HashMap<>();
        String[] parts = s.split(",");
        for (String part : parts) {

            // split the student data by colon to get the
            // name and roll number
            String[] myData = part.split(":");

            String dataKey = myData[0].trim();
            String dataWord = myData[1].trim();

            // Add to map
            hashMap.put(dataKey, dataWord);
        }

        return hashMap;
    }

    public static Muehle getMill() {
        return mill;
    }
}
