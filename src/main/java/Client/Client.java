package Client;

import Tools.Factory.ObjectSerialization;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Client extends NetworkInterface {

    public static void main(String[] args) throws SocketException {
        Client client = new Client(8000);
        Runtime.getRuntime().addShutdownHook(new Thread(client::disposeAll));
    }

    private final Thread threadListener, threadExecutor;
    private static boolean running;

    private static final int id = ThreadLocalRandom.current().nextInt();
    private static boolean connectedToServer = false;

    public Client(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket();
        threadListener = new Thread(run(), "Client Thread");
        threadExecutor = new Thread(runExecutor(), "Executor Thread");
        threadListener.start();
        running = true;
    }

    private Runnable run() {
        return () -> {
            while (running) {
                try {
                    packet = new DatagramPacket(new byte[]{CommandByte.START_BYTE, (byte) id}, 2);
                    socket.connect(address, PORT);
                    send(packet.getData(), packet.getData().length);
                    socket.disconnect();

                    byte[] dataReceived = receive(1, packet.getAddress(), packet.getPort());
                    if (dataReceived[0] == CommandByte.CONFIRMATION_BYTE) {
                        System.out.println("Client connected to a server!");
                        connectedToServer = true;
                        break;
                    }
                    Thread.sleep(1000 / 60);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            threadExecutor.start();
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable runExecutor() {
        return () -> {

            ClientInfo<String[]> osInfoClient = () -> new String[]
                    {System.getProperty("os.name"),
                    System.getProperty("os.version"),
                    System.getProperty("os.vender"),
                    System.getProperty("os.architecture"),
                    System.getProperty("user.name")};

            ClientInfo<Map<String, String>> osInfoSystem = System::getenv;

            byte[] objInfo = Objects.requireNonNull(ObjectSerialization.serialize(osInfoClient.get()));
            byte[] objSystem = Objects.requireNonNull(ObjectSerialization.serialize(osInfoSystem.get()));

            while (connectedToServer) {
                try {
                    packet = new DatagramPacket(objInfo, objInfo.length);
                    socket.connect(address, PORT);
                    send(objInfo, objInfo.length);

                    packet = new DatagramPacket(objSystem, objSystem.length);
                    send(objSystem, objSystem.length);
                    socket.disconnect();
                    Thread.sleep(1000/60);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void disposeAll() {
        running = false;
        connectedToServer = false;
        super.disposeAll();
        try {
            threadListener.join();
            threadExecutor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socket.disconnect();
        socket.close();
    }
}

