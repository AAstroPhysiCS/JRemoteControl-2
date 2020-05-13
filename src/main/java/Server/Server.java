package Server;

import Server.ClientEntity.ClientBuilder;
import Server.ClientEntity.ClientEntity;
import Tools.Factory.ObjectSerialization;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Server extends NetworkInterface {

    private final Thread thread;
    private boolean running;

    private static final int BUFFER_SIZE = 10000;
    private static byte[] buffer;

    private final List<Byte> allClientId = new ArrayList<>();    //for checking
    private final List<ClientEntity> allClients = new ArrayList<>();

    public Server(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket(PORT, address);
        thread = new Thread(run(), "Server Thread");
        running = true;
        thread.start();
    }

    private Runnable run() {
        return () -> {
            while (running) {
                try {
                    buffer = receive(2);

                    if (buffer.length <= 0) continue;

                    if (buffer[0] == CommandByte.START_CLIENT_BYTE) {
                        if (!allClientId.contains(buffer[1])) {
                            allClientId.add(buffer[1]);

                            String[] info = null;
                            Map<String, String> env = null;

                            System.out.println("----------------------------------");
                            System.out.println("New Client connected!");
                            send(new byte[]{CommandByte.CONFIRMATION_BYTE}, 1, packet.getAddress(), packet.getPort());
                            System.out.println("Confirmation byte send!");

                            buffer = new byte[BUFFER_SIZE];
                            buffer = receive(buffer.length, packet.getAddress(), packet.getPort());
                            if (ObjectSerialization.deseralize(buffer) instanceof String[] s) { info = s; }

                            buffer = new byte[BUFFER_SIZE];
                            buffer = receive(buffer.length, packet.getAddress(), packet.getPort());
                            if (ObjectSerialization.deseralize(buffer) instanceof Map s) { env = s;}

                            send(new byte[]{CommandByte.INFO_ACHIEVED_BYTE}, 1, packet.getAddress(), packet.getPort());
                            System.out.println("Client info achieved!");

                            if(env != null && info != null) {
                                allClients.add(new ClientBuilder.ClientBuilderTemplate()
                                        .setId(buffer[1])
                                        .setInfo(info)
                                        .setEnv(env)
                                        .build());
                            }
                        }
                    }
                    sleep(1000 / 60);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    public List<ClientEntity> getAllClients() {
        return allClients;
    }

    @Override
    public void disposeAll() {
        running = false;
        super.disposeAll();
        socket.disconnect();
        socket.close();
    }
}
