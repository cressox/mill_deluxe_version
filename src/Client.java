import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Client class
class Client {
    // CLIENT VARIABLES //
    private static PrintWriter out;
    private static BufferedReader in;
    private static boolean should_receive = true;
    private static Socket socket;
    private static Mill_Interface mill_interface = new Mill_Interface();

    // driver code
    public static void main(String[] args) throws IOException {
        mill_interface.draw();

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
                // interpretation //

                if (data != null) {
                    System.out.println(data);

                } else { // server dead
                    should_receive = false;
                }
            } catch (IOException e) { // could be server dead
                e.printStackTrace();
                should_receive = false;
            }
        }
    }

}
