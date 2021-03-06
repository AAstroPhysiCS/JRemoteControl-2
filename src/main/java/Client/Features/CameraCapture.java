package Client.Features;

import Handler.Message;
import Handler.PacketHandler;
import Tools.Network.NetworkInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraCapture extends Feature {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Mat cam_Mat;
    private final VideoCapture videoCapture;
    private final int index;

    public CameraCapture(final int index, PacketHandler packetHandler) {
        super(packetHandler);
        this.index = index;
        videoCapture = new VideoCapture();
        cam_Mat = new Mat();
    }

    private void open() {
        boolean success = videoCapture.open(index);
        if (!success) throw new InternalError("Camera could not be opened!");
    }

    private BufferedImage convertToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        DataBufferByte dataBufferByte = (DataBufferByte) image.getRaster().getDataBuffer();
        byte[] buffer = dataBufferByte.getData();
        mat.get(0, 0, buffer);
        return image;
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
    public void stopFeature() {
        running = false;
    }

    @Override
    public void startFeature() {
        open();
        if (!videoCapture.isOpened())
            throw new NullPointerException("Cam could not be opened!");
        running = true;
        thread.execute(run());
    }

    @Override
    protected Runnable run() {
        return () -> {
            while (running) {
                boolean success = videoCapture.read(cam_Mat);
                if (!success)
                    throw new NullPointerException("Frame can not be processed!");
                Message<byte[]> dataMessage = getImageAsByteArray(convertToBufferedImage(cam_Mat));
                try {
                    byte[] data = objectHandler.writeObjects(dataMessage);
                    byte[] dataWithId = objectHandler.writeModifiedArray(data, NetworkInterface.CommandByte.CAMERA_BYTE);
                    packetHandler.send(dataWithId, dataWithId.length, packetHandler.getAddress(), packetHandler.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void disposeAll() {
        thread.shutdown();
        cam_Mat.release();
        videoCapture.release();
    }
}
