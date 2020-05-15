package Server;

import Server.ClientEntity.ClientBuilder;
import Server.ClientEntity.ClientEntity;
import Handler.InfoHandler;
import Handler.ObjectSerialization;
import Handler.SocketDataHandler;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server extends NetworkInterface {

    private final Thread thread, threadFeatureHandler;
    private boolean running, runningSocketHandler;

    private final int BUFFER_SIZE = 10000;
    private byte[] buffer;

    private final List<Byte> allClientId = new ArrayList<>();    //for checking
    private final List<ClientEntity> allClients = new ArrayList<>();

    private InfoHandler infoHandler;

    public Server(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket(PORT, address);
        infoHandler = new InfoHandler(socket);

        thread = new Thread(run(), "Server Thread");
        running = true;
        thread.setDaemon(true);
        thread.start();

        threadFeatureHandler = new Thread(featureHandler(), "Feature Handler");
        runningSocketHandler = true;
        threadFeatureHandler.start();
    }

    private Runnable run() {
        return () -> {
            while (running) {
                try {
                    if (!socket.isClosed())
                        buffer = infoHandler.receive(2);

                    if (buffer.length <= 0) continue;

                    if (buffer[0] == CommandByte.START_BYTE) {
                        if (!allClientId.contains(buffer[1])) {
                            allClientId.add(buffer[1]);

                            String[] info = null;
                            Map<String, String> env = null;

                            System.out.println("----------------------------------");
                            System.out.println("New Client connected!");
                            infoHandler.send(new byte[]{CommandByte.CONFIRMATION_BYTE}, 1, infoHandler.getAddress(), infoHandler.getPort());
                            System.out.println("Confirmation byte send!");

                            buffer = new byte[BUFFER_SIZE];
                            buffer = infoHandler.receive(buffer.length, infoHandler.getAddress(), infoHandler.getPort());
                            if (ObjectSerialization.deseralize(buffer) instanceof String[] s) {
                                info = s;
                            }

                            buffer = new byte[BUFFER_SIZE];
                            buffer = infoHandler.receive(buffer.length, infoHandler.getAddress(), infoHandler.getPort());
                            if (ObjectSerialization.deseralize(buffer) instanceof Map s) {
                                env = s;
                            }

                            infoHandler.send(new byte[]{CommandByte.INFO_ACHIEVED_BYTE}, 1, infoHandler.getAddress(), infoHandler.getPort());

                            System.out.println("Client info achieved!");
                            assert env != null && info != null;
                            allClients.add(new ClientBuilder.ClientBuilderTemplate()
                                    .setInfo(info)
                                    .setEnv(env)
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

    private Runnable featureHandler() {
        return () -> {
            while(runningSocketHandler){
//                socketHandler.run();
            }
        };
    }

    public List<ClientEntity> getAllClients() {
        return allClients;
    }

    @Override
    public void disposeAll() {
        socket.close();
        running = false;
        runningSocketHandler = false;
        super.disposeAll();
    }
}
