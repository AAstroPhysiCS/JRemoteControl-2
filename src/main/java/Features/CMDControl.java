package Features;

public class CMDControl extends Feature {

    private CMDControl(String featureName) {
        super(featureName);
    }

    private static CMDControl CMDControlInstance;

    public static CMDControl getInstance(){
        if(CMDControlInstance == null)
            CMDControlInstance = new CMDControl("CMD - Control");
        return CMDControlInstance;
    }

    @Override
    public void startFeature() {

    }

    @Override
    public void disposeAll() {

    }
}
