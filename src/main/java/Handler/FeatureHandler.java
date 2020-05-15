package Handler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramSocket;

public class FeatureHandler extends SocketDataHandler {

    public FeatureHandler(DatagramSocket socket) {
        super(socket);
    }

    public Object handle() {
        return ObjectSerialization.deseralize(null);
    }

    public <V> V handleTo(byte[] arr, V to) {
        byte[] actualData = ObjectSerialization.deseralize(arr);
        if(to instanceof BufferedImage){
            try(ByteArrayInputStream bis = new ByteArrayInputStream(actualData)){
                return (V) ImageIO.read(bis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
