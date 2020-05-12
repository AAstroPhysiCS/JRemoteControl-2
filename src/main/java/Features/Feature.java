package Features;

import Tools.Disposeable;

public abstract class Feature implements Disposeable {
    private final String featureName;

    protected Feature(String featureName) {
        this.featureName = featureName;
    }

    public abstract void startFeature();

    public String getFeatureName() {
        return featureName;
    }
}
