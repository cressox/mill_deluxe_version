import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// Server class
class Server {
    static DB_Connector db_con;
    ArrayList[] onlineUser; // usernames inside
    static ServerSocket server;

    public static void main(String[] args)
    {
        server = null;
        db_con = new DB_Connector();

        int num_of_clients = 0;

        try {

            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);

            // getting client request
            while (true) { // beliebig viele clients

                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected from IP: "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client, db_con);


                // This thread will handle the client
                // separately
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
}
