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

public class DesktopCaptureListener extends FeatureListener {

    public DesktopCaptureListener(Controller controller, Server server) {
        super(controller, server);
    }

    @Override
    protected Runnable run(Controller controller) {
        return () -> {
            while (runningFeature) {
                byte[] buffer = server.getBuffer();

                Sleep(1000 / 60);

                if (buffer == null ||  buffer[0] != NetworkInterface.CommandByte.DESKTOPCONTROL_BYTE) continue;

                Message<byte[]> currentInfo = (Message<byte[]>) objectHandler.readModifiedObjects(buffer);
                BufferedImage desktopImage = null;
                if (currentInfo.get() instanceof byte[] s) {
                    desktopImage = toImage(s);
                }
                if (desktopImage == null) continue;

                controller.desktopCaptureImageView.setPreserveRatio(false);
                final int width = (int) controller.desktopCapturePane.getPrefWidth();
                final int height = (int) controller.desktopCapturePane.getPrefHeight();
                controller.desktopCaptureImageView.setImage(GraphicsConfigurator.resize(desktopImage, width, height));

                final int widthExpand = (int) controller.desktopCaptureExpandImageView.getFitWidth();
                final int heightExpand = (int) controller.desktopCaptureExpandImageView.getFitHeight();
                if (widthExpand > 0 && heightExpand > 0)
                    controller.desktopCaptureExpandImageView.setImage(GraphicsConfigurator.resize(desktopImage, widthExpand, heightExpand));
            }
            GraphicsConfigurator.drawColor(controller.desktopCaptureImageView, Color.LIGHT_GRAY);
            if(controller.desktopCaptureExpandImageView.getFitWidth() > 0)
                GraphicsConfigurator.drawColor(controller.desktopCaptureExpandImageView, Color.LIGHT_GRAY);
        };
    }

    @Override
    public void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.desktopCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            if (newValue && selectedClient != null) {
                runningFeature = true;
                thread.execute(run(controller));
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.DESKTOPCONTROL_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oldValue && selectedClient != null) {
                runningFeature = false;
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.DESKTOPCONTROL_BYTE_STOP}, 1, selectedClient.getAddress(), selectedClient.getPort());
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
