import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// ClientHandler class
class ClientHandler {
    private final Socket clientSocket;
    private final int id; // identification of the client

    private PrintWriter out;
    private BufferedReader in;
    private boolean should_receive = true;

    private ClientHandler other_player;

    // Constructor
    public ClientHandler(Socket socket, int id)
    {
        this.clientSocket = socket;
        this.id = id;
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

            new Thread(this::receive_data).start(); // abgekapselt vom main thread
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
        String last_data = null; // anfragen nicht mehrmls senden

        while(should_receive){
            try {
                String data = in.readLine();
                // interpretation //

                if (data != null) {
                    if (!data.equals(last_data)) {
                        System.out.println(data);
                        last_data = data;
                        if (other_player != null) { // leite daten weiter an anderen client
                            other_player.send_data(data);

                            if (!data.contains("mill")) {
                                this.send_data("method:turn_off");
                                other_player.send_data("method:turn_on");
                            }
                        }
                    }
                } else { // client dead
                    should_receive = false;
                }
            } catch (IOException e) { // could be client dead
                should_receive = false;
                e.printStackTrace();
            }
        }
    }
    public void setOther_player(ClientHandler other_player) {
        this.other_player = other_player;
    }

    public int getId() {
        return id;
    }
}