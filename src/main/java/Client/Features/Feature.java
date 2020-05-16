package Client.Features;

import Tools.Disposeable;

public abstract class Feature implements Disposeable {

    protected final Thread thread = new Thread(run(), getClass().getName() + "Feature Thread");

    public Feature() {

    }

    public abstract void startFeature();

    protected abstract Runnable run();
}
