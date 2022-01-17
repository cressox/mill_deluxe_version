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
                    send_data(lgc.getP1().toString());

                } else { // client dead
                    should_receive = false;
                }
            } catch (IOException | SQLException e) { // could be client dead
                should_receive = false;
                e.printStackTrace();
                System.out.println("close CH_CON to client " + ip.getHostAddress());
            }
        }
    }

    // update via db //
    void update(String client_request) throws SQLException {
        db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        // database acsess //

        // current game data //
        Map<String, String> mill = db_con.get_mill(id);
        int id_p1 = Integer.parseInt(mill.get("p1"));
        int id_p2 = Integer.parseInt(mill.get("p2"));
        System.out.println(mill);

        Map<String, String> p1 = db_con.get_player(id_p1);
        System.out.println(p1);

        Map<String, String> p2 = db_con.get_player(id_p2);
        System.out.println(p2);

        db_con.close_con(ip);
        // close db_con //
    }

    public void setLgc(Logic lgc) {
        this.lgc = lgc;
    }

    public int getId() {
        return id;
    }
}