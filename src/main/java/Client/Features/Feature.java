package Client.Features;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Tools.Disposeable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Feature implements Disposeable {

    protected final ExecutorService thread = Executors.newFixedThreadPool(2);
    protected boolean running = false;

    protected final ObjectHandler<Message<?>> objectHandler;
    protected final PacketHandler packetHandler;

    public Feature(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
        objectHandler = new ObjectHandler<>();
    }


    public abstract void stopFeature();

    public abstract void startFeature();

    protected abstract Runnable run();
}
