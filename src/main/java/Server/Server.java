package Server;

import Handler.PacketHandler;
import Handler.Message;
import Handler.ObjectHandler;
import Server.ClientEntity.ClientBuilder;
import Server.ClientEntity.ClientEntity;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server extends NetworkInterface {

    private final Thread thread, threadFeatureListener;
    private boolean running, runningFeature;

    private final int BUFFER_SIZE = 10000;
    private byte[] buffer;

    private final List<Integer> allClientId = new ArrayList<>();    //for checking
    private final List<ClientEntity> allClients = new ArrayList<>();

    private final PacketHandler clientPacketHandler, featurePacketHandler;
    private final ObjectHandler<Message<?>> objectHandler;

    public Server(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket(PORT, address);

        clientPacketHandler = new PacketHandler(socket);
        featurePacketHandler = new PacketHandler(socket);
        objectHandler = new ObjectHandler<>();

        thread = new Thread(clientListener(), "Server Thread");
        threadFeatureListener = new Thread(featureListener(), "Feature Thread");

        running = true;
        runningFeature = true;

        thread.setDaemon(true);
        threadFeatureListener.setDaemon(true);

        thread.start();
        threadFeatureListener.start();
    }

    private Runnable clientListener() {
        return () -> {
            while (running) {
                try {
                    if (!socket.isClosed())
                        buffer = clientPacketHandler.receive(BUFFER_SIZE);

                    if (buffer.length == 0) continue;

                    Message<?> currentInfo = objectHandler.readObjects(buffer);
                    String[] infoOuter = null;
                    if(currentInfo.get() instanceof String[] s){
                        infoOuter = s;
                    }

                    assert infoOuter != null;
                    if (Byte.parseByte(infoOuter[0]) == CommandByte.START_BYTE) {
                        int id = Integer.parseInt(infoOuter[1]);
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
                    }
                    sleep(1000 / 60);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable featureListener() {
        return () -> {
            while(runningFeature){
                
            }
        };
    }

    public ObjectHandler<Message<?>> getObjectHandler() {
        return objectHandler;
    }

    public PacketHandler getFeaturePacketHandler() {
        return featurePacketHandler;
    }

    public List<ClientEntity> getAllClients() {
        return allClients;
    }

    @Override
    public void disposeAll() {
        socket.close();
        running = false;
        runningFeature = false;
        super.disposeAll();
    }
}
