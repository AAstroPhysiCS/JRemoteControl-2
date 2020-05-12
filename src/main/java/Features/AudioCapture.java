package Features;

public class AudioCapture extends Feature {

    private AudioCapture(String featureName) {
        super(featureName);
    }

    private static AudioCapture audioCaptureInstance;

    public static AudioCapture getInstance(){
        if(audioCaptureInstance == null)
            audioCaptureInstance = new AudioCapture("Audio Capture");
        return audioCaptureInstance;
    }

    @Override
    public void startFeature() {

    }

    @Override
    public void disposeAll() {

    }
}
