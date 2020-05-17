package Server.Overlay;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GraphicsConfigurator {

    private GraphicsConfigurator(){}

    public static void drawColor(ImageView imageView, Color color){
        BufferedImage image = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        imageView.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public static BufferedImage resize(BufferedImage image, final int resizeWidth, final int resizeHeight){
        try {
            return Thumbnails.of(image).size(resizeWidth, resizeHeight).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
