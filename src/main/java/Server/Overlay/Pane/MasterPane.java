package Server.Overlay.Pane;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Server.ClientEntity.ClientEntity;
import Server.ClientEntity.ClientEvent;
import Server.Overlay.Controller.Controller;
import Server.Overlay.GraphicsConfigurator;
import Server.Server;
import Tools.Disposeable;
import Tools.Network.NetworkInterface;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Tools.Network.NetworkInterface.Sleep;

public class MasterPane implements Disposeable {

    private final Controller controller;
    private final Server server;

    private final Thread threadFeatureListener;
    private boolean runningFeature;

    private final ObjectHandler<Message<?>> objectHandler;
    private final PacketHandler featurePacketHandler;

    private final ClientEvent changeEvent;
    private ClientEntity selectedClient;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MasterPane(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;
        changeEvent = new ClientEvent(controller);

        objectHandler = new ObjectHandler<>();
        featurePacketHandler = new PacketHandler(server.getSocket());

        threadFeatureListener = new Thread(featureListener(), "Feature Thread");
        runningFeature = true;
        threadFeatureListener.setDaemon(true);
        threadFeatureListener.start();

        scheduledExecutorService.scheduleAtFixedRate(updateComponents(), 0, 1000, TimeUnit.MILLISECONDS);
        initComponents();
    }

    private void initComponents(){
        controller.cameraCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue && selectedClient != null){
                try {
                    featurePacketHandler.send(new byte[]{NetworkInterface.CommandByte.CAMERA_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(oldValue && selectedClient != null){
                try {
                    featurePacketHandler.send(new byte[]{NetworkInterface.CommandByte.CAMERA_BYTE_STOP}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Runnable featureListener() {
        return () -> {
            while (runningFeature) {

                byte[] buffer = server.getBuffer();

                Sleep(1000 / 60);

                if(buffer == null || buffer[0] == 0) continue;

                Message<?> currentInfo = objectHandler.readObjects(buffer);
                BufferedImage image = null;
                if (currentInfo.get() instanceof byte[] s) {
                    image = toImage(s);
                }
                if (image == null) continue;

                controller.cameraCaptureImageView.setPreserveRatio(false);
                final int width = (int)controller.cameraCapturePane.getPrefWidth();
                final int height = (int)controller.cameraCapturePane.getPrefHeight();
                Image imageFX = SwingFXUtils.toFXImage(Objects.requireNonNull(GraphicsConfigurator.resize(image, width, height)), null);
                controller.cameraCaptureImageView.setImage(imageFX);
                controller.cameraCaptureExpandImageView.setImage(imageFX);
            }
        };
    }

    private BufferedImage toImage(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            return ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Runnable updateComponents() {
        return () -> Platform.runLater(() -> {   //to not have exceptions...
            for (ClientEntity e : server.getAllClients()) {
                controller.addClients(e);
                changeEvent.addListener(e.getEventListener());
            }
            selectedClient = changeEvent.call();
        });
    }

    @Override
    public void disposeAll() {
        runningFeature = false;
        scheduledExecutorService.shutdownNow();
    }
}
