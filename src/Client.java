import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

// Client class
class Client {
    // CLIENT VARIABLES //
    private static Login login;

    static {
        try {
            login = new Login();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter out;
    private static BufferedReader in;
    private static boolean should_receive = true;
    private static boolean logged_in = true;
    private static Socket socket;
    private static Mill_Interface mill_interface = new Mill_Interface();
    private static Player player;

    Client() throws UnknownHostException {
    }

    // driver code
    public static void main(String[] args) throws IOException {
//        mill_interface.draw("white");
        login.draw();
        //new Thread(Client::check_if_logged_in).start(); // encapsulated vom main thread check if logged in
    }

    public static void connect_to_server(){
        try {
            socket = new Socket("localhost", 1234);
            // writing to server
            out = new PrintWriter(
                    socket.getOutputStream(), true);

            // reading from server
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            new Thread(Client::receive_data).start(); // abgekapselt vom main thread

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send_data(String data) {
        out.println(data); // sending information to server
        out.flush();
    }

    public static void receive_data() {
        while (should_receive) {
            try {
                String data = in.readLine();
                System.out.println(data);
                // interpretation //

                if (data != null) {
                    if (data.contains("lobby")){ // game dies, back to lobby
                        // winning by quit of the other player
                        login.getLobby().draw(); // paint lobby

                    }else if (data.contains("update")){
                        login.getLobby().update_all();

                    }else if (data.contains("join")){
                        mill_interface.draw("black");

                    }else if (data.contains("start") && !data.contains("re")){
                        mill_interface.draw("white");

                    }else if (data.contains("turnOn")){
                        mill_interface.setMyTurn(true);

                    }else if (data.contains("turnOff")){
                        mill_interface.setMyTurn(false);

                    }else if (data.contains("win")){
                        String color_of_winning_player = data.replace("win", "");
                        mill_interface.winning(color_of_winning_player);

                    }else if (data.contains("restart")){
                        String color_of_requesting_player = data.replace("win", "");
                        mill_interface.restart(color_of_requesting_player);

                    }else {
                        interpret_incomming_data(data);

                    }
                } else { // server dead
                    should_receive = false;
                }
            } catch (IOException | SQLException e) { // could be server dead
                e.printStackTrace();
                should_receive = false;
            }
        }
    }

    // INTERPRET DATA FROM CH //
    public static void interpret_incomming_data(String data){
        System.out.println(data);

        int i = 0;
        for (char ch : data.toCharArray()) {
            mill_interface.set_stone(i, ch);
            i++;
        }
    }

}
