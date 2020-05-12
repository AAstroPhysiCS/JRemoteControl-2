package Server.Overlay;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Drawer {

    private Drawer(){}

    public static void drawColor(ImageView imageView, Color color){
        BufferedImage image = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        imageView.setImage(SwingFXUtils.toFXImage(image, null));
    }
}
