package Client.Features;

import Handler.PacketHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DesktopCapture extends Feature {

    private static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private static Robot r;
    private static BufferedImage image;

    private final PacketHandler packetHandler;

    public DesktopCapture(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
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
            image = r.createScreenCapture(new Rectangle(screenDimension));
        };
    }

    @Override
    public void disposeAll() {
        image.flush();
    }
}
