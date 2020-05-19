package Server.Features;

import Events.FeatureListener;
import Handler.Message;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Overlay.GraphicsConfigurator;
import Server.Server;
import Tools.Network.NetworkInterface;
import Tools.Ref;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import static Tools.Network.NetworkInterface.Sleep;

public class CameraCaptureListener extends FeatureListener {

    public CameraCaptureListener(Controller controller, Server server) {
        super(controller, server);
    }

    @Override
    public Runnable run(Controller controller) {
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

    @Override
    public void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.cameraCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            if(newValue && selectedClient != null){
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CAMERA_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(oldValue && selectedClient != null){
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CAMERA_BYTE_STOP}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private BufferedImage toImage(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            return ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
