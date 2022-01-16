import java.io.*;
import java.net.*;

// Server class
class Server {
    public static void main(String[] args)
    {
        ServerSocket server = null;
        int num_of_clients = 0;
        ClientHandler[] clients = new ClientHandler[2];

        try {

            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);

            // getting client request
            while (num_of_clients < 2) { // beliebig viele (brauch nur 2)

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
                        = new ClientHandler(client, num_of_clients);

                clients[num_of_clients] = clientSock;
                num_of_clients += 1;

                // This thread will handle the client
                // separately
                clientSock.run();
            }

            clients[0].setOther_player(clients[1]);
            clients[1].setOther_player(clients[0]);

            clients[0].send_data("method:init, player:white");
            clients[1].send_data("method:init, player:black");

            clients[0].send_data("method:turn_on, player:white");

            System.out.println("clients connected");

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
