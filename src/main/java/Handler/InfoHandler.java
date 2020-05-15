package Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class InfoHandler extends SocketDataHandler {

    public DatagramPacket packet;

    public InfoHandler(DatagramSocket socket) {
        super(socket);
    }

    public byte[] receive(final int length) throws IOException {
        byte[] buffer = new byte[length];
        packet = new DatagramPacket(buffer, length);
        socket.receive(packet);
        return packet.getData();
    }

    public void send(byte[] buffer, final int length) throws IOException {
        packet = new DatagramPacket(buffer, length);
        socket.send(packet);
    }

    public void send(byte[] buffer, final int length, InetAddress address, final int port) throws IOException {
        packet = new DatagramPacket(buffer, length, address, port);
        socket.send(packet);
    }

    public byte[] receive(final int length, InetAddress address, int port) throws IOException {
        byte[] buffer = new byte[length];
        packet = new DatagramPacket(buffer, length, address, port);
        socket.receive(packet);
        return packet.getData();
    }

    public Object handle() {
        return null;
    }

    public <V> V handleTo(byte[] obj, V to) {
        return null;
    }

    public InetAddress getAddress() {
        return packet.getAddress();
    }

    public int getPort() {
        return packet.getPort();
    }
}
