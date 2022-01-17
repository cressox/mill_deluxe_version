import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

// Server class
class Server {
    static DB_Connector db_con;
    static ServerSocket server;
    static int i = 0;
    static int mill_games = 0;
    // needs to be last id of table games in db //
    static int num_of_clients = 0;
    // needs to be last id of table player in db //
    static Logic tmp_lgc;


    public static void main(String[] args)
    {
        server = null;
        db_con = new DB_Connector();

        ClientHandler clients[] = new ClientHandler[2];

        try {
            server = new ServerSocket(1234);
            server.setReuseAddress(true);
            while (true) { // beliebig viele clients
                Socket client = server.accept();
                num_of_clients++;
                System.out.println("New client connected from IP: "
                        + client.getInetAddress()
                        .getHostAddress());

                InetAddress ip = client.getInetAddress();

                db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
                db_con.delete_mill(1);
                db_con.delete_mill(2);
                db_con.close_con(ip);

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(num_of_clients, client, db_con);

                clients[i] = clientSock;
                i++;

                if (clients[0] != null && clients[1] != null){
                    mill_games++;
                    db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
                    db_con.insert_mill(mill_games, clients[0].getId(), clients[1].getId());
                    db_con.close_con(ip);
                    tmp_lgc = new Logic();
                    init_lgc(mill_games, ip);
                    clients[1].setLgc(tmp_lgc);
                    clients[0].setLgc(tmp_lgc);
                    clients[0] = null;
                    clients[1] = null;
                    i = 0;
                }

                clientSock.run();
            }

        }
        catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void init_lgc(int mill_id, InetAddress ip) throws SQLException {
        db_con.open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        // database acsess //

        // current game data //
        Map<String, String> mill = db_con.get_mill(mill_id);
        int id_p1 = Integer.parseInt(mill.get("p1"));
        int id_p2 = Integer.parseInt(mill.get("p2"));
        System.out.println(mill);

        Map<String, String> p1 = db_con.get_player(id_p1);
        String color_p1 = p1.get("color");
        System.out.println(p1);

        Map<String, String> p2 = db_con.get_player(id_p2);
        String color_p2 = p2.get("color");
        System.out.println(p2);

        System.out.println(tmp_lgc.getP1());
        tmp_lgc.init(id_p1, id_p2, color_p1, color_p2);
        System.out.println(tmp_lgc.getP1());

        db_con.close_con(ip);
        // close db_con //
    }
}
