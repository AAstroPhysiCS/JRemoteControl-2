package Client.Features;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DesktopCapture extends Feature {

    private static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private static Robot r;
    private static BufferedImage image;

    public DesktopCapture() {
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startFeature() {
        image = r.createScreenCapture(new Rectangle(screenDimension));
    }

    public static BufferedImage getImage() {
        return image;
    }

    @Override
    public void disposeAll() {
        image.flush();
    }
}
