import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

// ClientHandler class
class ClientHandler {
    DB_Connector db_con;
    private final Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;
    private boolean should_receive = true;

    // Constructor
    public ClientHandler(Socket socket, DB_Connector db_con)
    {
        this.clientSocket = socket;
        this.db_con = db_con;
    }

    public void run()
    {
        try {
            db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root");
            // database acsess //

            // get the outputstream of client
            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            // get the inputstream of client
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

            new Thread(this::receive_data).start(); // abgekapselt vom main thread
        }
        catch (IOException | SQLException e) {
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

                } else { // client dead
                    should_receive = false;
                    db_con.close_con();
                }
            } catch (IOException | SQLException e) { // could be client dead
                should_receive = false;
                e.printStackTrace();
            }
        }
        try {
            db_con.close_con();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}