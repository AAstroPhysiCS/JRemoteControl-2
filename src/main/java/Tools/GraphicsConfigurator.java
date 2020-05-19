package Tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GraphicsConfigurator {

    private GraphicsConfigurator() {
    }

    public static void drawColor(ImageView imageView, Color color) {
        BufferedImage image = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        imageView.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public static Image resize(BufferedImage image, final int resizeWidth, final int resizeHeight) {
        try {
            return SwingFXUtils.toFXImage(Thumbnails.of(image).size(resizeWidth, resizeHeight).asBufferedImage(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage resizeAsBufferedImage(BufferedImage image, final int resizeWidth, final int resizeHeight) {
        try {
            return Thumbnails.of(image).size(resizeWidth, resizeHeight).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image resize(Image imageFX, final int resizeWidth, final int resizeHeight) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(imageFX, null);
        try {
            bImage = Thumbnails.of(bImage).size(resizeWidth, resizeHeight).asBufferedImage();
            return SwingFXUtils.toFXImage(bImage, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
