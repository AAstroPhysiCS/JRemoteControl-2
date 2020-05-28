package Server;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Server.ClientEntity.ClientBuilder;
import Server.ClientEntity.ClientEntity;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Tools.Globals.BUFFER_SIZE;
import static Tools.Globals.Sleep;

public class Server extends NetworkInterface {

    private final Thread thread;
    private boolean running;

    private byte[] buffer;

    private final List<Integer> allClientId = new ArrayList<>();    //for checking
    private final List<ClientEntity> allClients = new ArrayList<>();

    private final PacketHandler clientPacketHandler;
    private final ObjectHandler<Message<?>> objectHandler;

    public Server(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket(PORT, address);

        clientPacketHandler = new PacketHandler(socket);
        objectHandler = new ObjectHandler<>();

        thread = new Thread(clientListener(), "Server Thread");
        running = true;
        thread.setDaemon(true);
        thread.start();
    }

    private Runnable clientListener() {
        return () -> {
            while (running) {
                try {
                    if (!socket.isClosed())
                        buffer = clientPacketHandler.receive(BUFFER_SIZE);

                    if (buffer.length == 0 || buffer[0] != CommandByte.START_BYTE) continue;

                    Message<?> currentInfo = objectHandler.readModifiedObjects(buffer);
                    if (currentInfo == null) continue;

                    String infoOuter = (String) currentInfo.get();
                    if (infoOuter == null) continue;

                    int id = Integer.parseInt(infoOuter);
                    if (!allClientId.contains(id)) {
                        allClientId.add(id);

                        String[] info = null;
                        Map<String, String> env = null;

                        System.out.println("----------------------------------");
                        System.out.println("New Client connected!");
                        clientPacketHandler.send(new byte[]{CommandByte.CONFIRMATION_BYTE}, 1, clientPacketHandler.getPacketAddress(), clientPacketHandler.getPacketPort());
                        System.out.println("Confirmation byte send!");

                        buffer = new byte[BUFFER_SIZE];
                        buffer = clientPacketHandler.receive(buffer.length, clientPacketHandler.getPacketAddress(), clientPacketHandler.getPacketPort());
                        currentInfo = objectHandler.readObjects(buffer);
                        if (currentInfo.get() instanceof String[] s) {
                            info = s;
                        }

                        buffer = new byte[BUFFER_SIZE];
                        buffer = clientPacketHandler.receive(buffer.length, clientPacketHandler.getPacketAddress(), clientPacketHandler.getPacketPort());
                        currentInfo = objectHandler.readObjects(buffer);
                        if (currentInfo.get() instanceof Map s) {
                            env = s;
                        }

                        clientPacketHandler.send(new byte[]{CommandByte.INFO_ACHIEVED_BYTE}, 1, clientPacketHandler.getPacketAddress(), clientPacketHandler.getPacketPort());
                        System.out.println("Client info achieved!");

                        assert env != null && info != null;
                        allClients.add(new ClientBuilder.ClientBuilderTemplate()
                                .setId(id)
                                .setInfo(info)
                                .setEnv(env)
                                .address(clientPacketHandler.getPacketAddress())
                                .port(clientPacketHandler.getPacketPort())
                                .build());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Sleep(1000 / 60);
            }
        };
    }


    public List<ClientEntity> getAllClients() {
        return allClients;
    }

    public PacketHandler getPacketHandler() {
        return clientPacketHandler;
    }

    @Override
    public void disposeAll() {
        socket.close();
        running = false;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void resetBuffer() {
        buffer = new byte[BUFFER_SIZE];
    }
}
