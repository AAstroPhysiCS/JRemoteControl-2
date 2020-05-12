package Tools.Network;

import Features.*;
import Tools.Disposeable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public abstract class NetworkInterface implements IConnectable, Disposeable {

    protected static InetAddress address;
    protected final int PORT;

    protected DatagramSocket socket;
    protected DatagramPacket packet;

    private static final List<Feature> supportedFeatures = new ArrayList<>();

    //not using enum because i need to eventually send bytes
    public static class CommandByte {
        public static final byte START_BYTE = (byte)0x000001;
        public static final byte CAMERA_BYTE = (byte)0x000002;
        public static final byte CMDCONTROL_BYTE = (byte)0x000003;
        public static final byte DESKTOPCONTROL_BYTE = (byte)0x000004;
        public static final byte AUDIOCAPTURE_BYTE = (byte)0x000005;
        public static final byte CAPTUREOSINFO_BYTE = (byte)0x000006;
        public static final byte CONFIRMATION_BYTE = (byte)0x000007;
    }

    public NetworkInterface(final int PORT) {
        this.PORT = PORT;
        try {
            address = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (supportedFeatures.size() == 0) {
            supportedFeatures.add(CameraCapture.getInstance(0));
            supportedFeatures.add(CMDControl.getInstance());
            supportedFeatures.add(AudioCapture.getInstance());
            supportedFeatures.add(DesktopCapture.getInstance());
        }
    }


    protected byte[] receive(final int length) throws IOException {
        byte[] buffer = new byte[length];
        packet = new DatagramPacket(buffer, length);
        socket.receive(packet);
        return packet.getData();
    }

    protected void send(byte[] buffer, final int length) throws IOException {
        packet = new DatagramPacket(buffer, length);
        socket.send(packet);
    }

    protected void send(byte[] buffer, final int length, InetAddress address, final int port) throws IOException {
        packet = new DatagramPacket(buffer, length, address, port);
        socket.send(packet);
    }

    protected byte[] receive(final int length, InetAddress address, int port) throws IOException {
        byte[] buffer = new byte[length];
        packet = new DatagramPacket(buffer, length, address, port);
        socket.receive(packet);
        return packet.getData();
    }

    @Override
    public void disposeAll() {
        for (Feature allFeatures : supportedFeatures)
            allFeatures.disposeAll();
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public static InetAddress getAddress() {
        return address;
    }

    public int getPORT() {
        return PORT;
    }

    public static List<Feature> getSupportedFeatures() {
        return supportedFeatures;
    }
}
