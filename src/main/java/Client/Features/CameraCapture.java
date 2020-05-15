package Client.Features;

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

    private Mat cam_Mat;
    private static VideoCapture videoCapture;
    private final int index;

    public CameraCapture(final int index) {
        this.index = index;
        videoCapture = new VideoCapture();
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

    public byte[] getImageAsByteArray() {
        BufferedImage image = convertToBufferedImage(cam_Mat);
        try (ByteArrayOutputStream ous = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", ous);
            return ous.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void startFeature() {
        if (!videoCapture.isOpened())
            throw new NullPointerException("Cam not opened!");
        boolean success = videoCapture.read(cam_Mat);
        if (!success)
            throw new NullPointerException("Frame can not be processed!");
    }

    @Override
    public void disposeAll() {
        if (cam_Mat != null)
            cam_Mat.release();
        videoCapture.release();
    }
}
