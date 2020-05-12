package Features;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class WebcamCapture extends Feature {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static WebcamCapture webcamCaptureInstance;

    public static WebcamCapture getInstance(final int index) {
        if (webcamCaptureInstance == null)
            webcamCaptureInstance = new WebcamCapture("Webcam Capture", index);
        return webcamCaptureInstance;
    }

    private Mat cam_Mat;
    private static VideoCapture videoCapture;

    WebcamCapture(String name, final int index) {
        super(name);
        videoCapture = new VideoCapture();
        videoCapture.open(index);
    }

    private BufferedImage convertToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        DataBufferByte dataBufferByte = (DataBufferByte) image.getRaster().getDataBuffer();
        byte[] buffer = dataBufferByte.getData();
        mat.get(0, 0, buffer);
        return image;
    }

    public BufferedImage getCamAsBufferedImage() {
        return convertToBufferedImage(cam_Mat);
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
        cam_Mat.release();
        videoCapture.release();
    }
}
