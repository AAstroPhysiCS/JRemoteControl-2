package Tools.Network;

import Tools.Disposeable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class NetworkInterface implements Disposeable {

    protected static InetAddress address;
    protected final int PORT;

    protected DatagramSocket socket;
    protected DatagramPacket packet;

    protected String hostName = "OneTrueGod";

    //not using enum because i need to eventually send bytes
    public static class CommandByte {
        public static final byte START_BYTE = (byte) 0x01;
        public static final byte CAMERA_BYTE = (byte) 0x02;
        public static final byte CMDCONTROL_BYTE = (byte) 0x03;
        public static final byte CHAT_BYTE = (byte)0x9;
        public static final byte DESKTOPCONTROL_BYTE = (byte) 0x04;
        public static final byte AUDIOCAPTURE_BYTE = (byte) 0x05;
        public static final byte CONFIRMATION_BYTE = (byte) 0x07;
        public static final byte INFO_ACHIEVED_BYTE = (byte) 0x08;
        public static final byte STOP_BYTE = (byte)0x10;
    }

    public NetworkInterface(final int PORT) {
        this.PORT = PORT;
        try {
            address = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disposeAll() {
//        for (Feature allFeatures : supportedFeatures)
//            allFeatures.disposeAll();
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}
