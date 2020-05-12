package Client;

import Tools.Factory.ObjectSerialization;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends NetworkInterface {

    public static void main(String[] args) throws SocketException {
        Client client = new Client(8000);
        Runtime.getRuntime().addShutdownHook(new Thread(client::disposeAll));
    }

    private static final ExecutorService threadListener = Executors.newSingleThreadScheduledExecutor();

    private static final Random rand = new Random(LocalDateTime.now().getSecond());
    private static final int id = rand.nextInt();

    public Client(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket();
        threadListener.execute(listener());
    }

    private Runnable listener() {
        return () -> {
            try {
                packet = new DatagramPacket(new byte[]{CommandByte.START_BYTE, (byte) id}, 2);
                while(!socket.isConnected()){
                    socket.connect(address, PORT);
                }
                send(packet.getData(), packet.getData().length);
                socket.disconnect();
                byte[] dataReceived = receive(1, packet.getAddress(), packet.getPort());
                if (dataReceived[0] == CommandByte.CONFIRMATION_BYTE) {
                    System.out.println("Client connected to a server!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            threadListener.execute(infoListener());
        };
    }

    private Runnable infoListener() {
        return () -> {
            ClientInfo<String[]> osInfoClient = () -> new String[]
                    {System.getProperty("os.name"),
                            System.getProperty("os.version"),
                            System.getProperty("os.vendor"),
                            System.getProperty("os.architecture"),
                            System.getProperty("user.name")};

            ClientInfo<Map<String, String>> osInfoSystem = System::getenv;

            byte[] objInfo = Objects.requireNonNull(ObjectSerialization.serialize(osInfoClient.get()));
            byte[] objSystem = Objects.requireNonNull(ObjectSerialization.serialize(osInfoSystem.get()));

            while (true) {
                try {
                    packet = new DatagramPacket(objInfo, objInfo.length);
                    socket.connect(address, PORT);
                    send(objInfo, objInfo.length);

                    packet = new DatagramPacket(objSystem, objSystem.length);
                    send(objSystem, objSystem.length);
                    socket.disconnect();

                    if (receive(1, packet.getAddress(), packet.getPort())[0] == CommandByte.INFO_ACHIEVED_BYTE)
                        break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            threadListener.execute(featureListener());
        };
    }

    private Runnable featureListener() {
        return () -> {
            while (true) {
                sleep(1000 / 60);
            }
        };
    }

    @Override
    public void disposeAll() {
        threadListener.shutdown();
        super.disposeAll();
        socket.disconnect();
        socket.close();
    }
}

