package Client.Features;

import Tools.Disposeable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Feature implements Disposeable {

    protected final ExecutorService thread = Executors.newSingleThreadScheduledExecutor();
    protected boolean running = false;

    public Feature() {}

    public abstract void stopFeature();

    public abstract void startFeature();

    protected abstract Runnable run();
}
