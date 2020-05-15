package Handler;

import java.net.DatagramSocket;

public abstract class SocketDataHandler {

    protected final DatagramSocket socket;

    public SocketDataHandler(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
