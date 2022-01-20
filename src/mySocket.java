import java.io.IOException;
import java.net.Socket;
import java.net.SocketImpl;

public class mySocket extends Socket {
    int id;
    int mill_id;

    public mySocket(String host, int port, int id, int mill_id) throws IOException {
        super(host, port);
        this.id = id;
        this.mill_id = mill_id;
    }

    public mySocket(SocketImpl socket) {
    }

    public int getId() {
        return id;
    }

    public int getMill_id() {
        return mill_id;
    }
}
