package Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PacketHandler {

    public DatagramPacket packet;
    public DatagramSocket socket;

    private InetAddress address;
    private int port;

    public PacketHandler(DatagramSocket socket) {
        this.socket = socket;
    }

    public PacketHandler(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    public void send(byte[] buffer, final int length) throws IOException {
        packet = new DatagramPacket(buffer, length);
        socket.send(packet);
    }

    public void send(byte[] buffer, final int length, InetAddress address, final int port) throws IOException {
        packet = new DatagramPacket(buffer, length, address, port);
        socket.send(packet);
    }

    public byte[] receive(final int length) throws IOException {
        byte[] buffer = new byte[length];
        packet = new DatagramPacket(buffer, length);
        socket.receive(packet);
        return packet.getData();
    }

    public byte[] receive(final int length, InetAddress address, int port) throws IOException {
        byte[] buffer = new byte[length];
        packet = new DatagramPacket(buffer, length, address, port);
        socket.receive(packet);
        return packet.getData();
    }

    public InetAddress getPacketAddress() {
        return packet.getAddress();
    }

    public InetAddress getAddress(){ return address; }

    public int getPort(){ return port; }

    public int getPacketPort() {
        return packet.getPort();
    }
}
