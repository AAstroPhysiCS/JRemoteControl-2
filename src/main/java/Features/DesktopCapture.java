package Features;

public class DesktopCapture extends Feature {

    private DesktopCapture(String featureName) {
        super(featureName);
    }

    private static DesktopCapture desktopCaptureInstance;

    public static DesktopCapture getInstance(){
        if(desktopCaptureInstance == null)
            desktopCaptureInstance = new DesktopCapture("Desktop Capture");
        return desktopCaptureInstance;
    }

    @Override
    public void startFeature() {

    }

    @Override
    public void disposeAll() {

    }
}
