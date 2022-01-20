import java.io.IOException;
import java.net.*;

public class myServerSocket extends ServerSocket {

    public myServerSocket(int port) throws IOException {
        super(port);
    }

    @Override
    public mySocket accept() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isBound())
            throw new SocketException("Socket is not bound yet");
        mySocket s = new mySocket((SocketImpl) null);
        implAccept(s);
        return s;
    }
}
