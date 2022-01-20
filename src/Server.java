import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

// Server class
class Server {
    static DB_Connector db_con;
    static ServerSocket server;
    static int mill_games = 0;
    // needs to be last id of table games in db //
    static int num_of_clients = 0;
    // needs to be last id of table player in db //
    static Logic tmp_lgc;
    static InetAddress ip;
    static int i = 0;


    public static void main(String[] args) throws SQLException, UnknownHostException {
        server = null;
        db_con = new DB_Connector();
        ip = InetAddress.getLocalHost();



        mill_games = db_con.get_count_of_mills(ip);
        num_of_clients = db_con.get_count_of_registered_players(ip);

        System.out.println("active mill games in db: " + mill_games);
        System.out.println("registered players in db: " + num_of_clients);


        ClientHandler online_users[] = new ClientHandler[1000];
        // max count of player online //

        try {
            server = new ServerSocket(1234);
            server.setReuseAddress(true);
            while (i<1000) { // 1000 clients
                Socket client = server.accept();
                client.getPort();
                System.out.println(client);
                num_of_clients++;
                System.out.println("New client connected from IP: "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(-1, client, db_con); // -1 user not logged in
                online_users[i] = clientSock;
                clientSock.setOnline_users(online_users);
                i++;
                // give every client handler the pointer of list of online user //

                clientSock.run();
                // debug the current values //
                System.out.println("active mill games in db: " + mill_games);
                System.out.println("registered players in db: " + num_of_clients);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
