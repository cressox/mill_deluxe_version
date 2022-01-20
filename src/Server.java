import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Map;

// Server class
class Server {
    static DB_Connector db_con;
    static myServerSocket server;
    static int i = 0;
    static int mill_games = 0;
    // needs to be last id of table games in db //
    static int num_of_clients = 0;
    // needs to be last id of table player in db //
    static Logic tmp_lgc;
    static InetAddress ip;

    static ClientHandler clients[] = new ClientHandler[2];

    public static void main(String[] args) throws SQLException, UnknownHostException {
        server = null;
        db_con = new DB_Connector();
        ip = InetAddress.getLocalHost();

        mill_games = db_con.get_count_of_mills(ip);
        num_of_clients = db_con.get_count_of_registered_players(ip);

        System.out.println("active mill games in db: " + mill_games);
        System.out.println("registered players in db: " + num_of_clients);


        ClientHandler user_online[] = new ClientHandler[1000];
        // max count of player online //

        try {
            server = new myServerSocket(1234);
            server.setReuseAddress(true);
            while (true) { // beliebig viele clients
                mySocket client = server.accept();
                num_of_clients++;
                System.out.println("New client connected from IP: "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(-1, client, db_con); // -1 user not logged in
                clientSock.setUser_online(user_online);
                // give every client handler the pointer of list of online user //

                join_game(client, clientSock);
                // direct into game

                clientSock.run();
            }

        }
        catch (IOException e) {
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
        // current game data //
        Map<String, String> mill = db_con.getMillByID(mill_id, ip);
        int id_p1 = Integer.parseInt(mill.get("p1"));
        int id_p2 = Integer.parseInt(mill.get("p2"));
        System.out.println(mill);

        Map<String, String> p1 = db_con.getPlayerByID(id_p1, ip);
        String color_p1 = p1.get("color");
        System.out.println(p1);

        Map<String, String> p2 = db_con.getPlayerByID(id_p2, ip);
        String color_p2 = p2.get("color");
        System.out.println(p2);

//        System.out.println(tmp_lgc.getP1());
        tmp_lgc.init(id_p1, id_p2, color_p1, color_p2);
//        System.out.println(tmp_lgc.getP1());

        // debug the current values //
        System.out.println("active mill games in db: " + mill_games);
        System.out.println("registered players in db: " + num_of_clients);
    }

    static void join_game(mySocket client, ClientHandler ch) throws SQLException {
        clients[i] = ch;
        i++;
        if (clients[0] != null && clients[1] != null){
            db_con.insert_mill(client.getMill_id(), clients[0].getId(), clients[1].getId(), ip);
            tmp_lgc = new Logic(client.getMill_id());
            init_lgc(client.getMill_id(), ip);
            clients[1].setLgc(tmp_lgc);
            clients[0].setLgc(tmp_lgc);
            System.out.println("set logic to two player");
            clients[0].setMill_id(client.getMill_id());
            clients[1].setMill_id(client.getMill_id());

            if (client.getMill_id() == 1){
                tmp_lgc.setTest("bei beiden aktiv!!!");
            }
            clients[0] = null;
            clients[1] = null;
            i = 0;
        }
    }
}
