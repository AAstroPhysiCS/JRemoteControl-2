package Client.Features;

import Tools.Disposeable;

public abstract class Feature implements Disposeable {

    public Feature() {

    }

    public abstract void startFeature();
}
