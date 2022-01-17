import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

// ClientHandler class
class ClientHandler {
    DB_Connector db_con;
    private final Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;
    private boolean should_receive = true;
    private final InetAddress ip;

    // Constructor
    public ClientHandler(Socket socket, DB_Connector db_con)
    {
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

            db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
            // database acsess //
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
                    db_con.close_con(ip);
                }
            } catch (IOException | SQLException e) { // could be client dead
                should_receive = false;
                e.printStackTrace();
                System.out.println("close CH_CON to client " + ip.getHostAddress());
            }
        }
        try {
            db_con.close_con(ip);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}