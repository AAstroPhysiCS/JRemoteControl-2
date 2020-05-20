package Client.Features;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Tools.Disposeable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Feature implements Disposeable {

    protected final ExecutorService thread = Executors.newSingleThreadScheduledExecutor();
    protected boolean running = false;

    protected final ObjectHandler<Message<?>> objectHandler;
    protected final PacketHandler packetHandler;

    public Feature(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
        objectHandler = new ObjectHandler<>();
    }

    public byte[] modifyArray(byte[] arr, byte id) {
        byte[] arrNew = new byte[arr.length + 1];
        System.arraycopy(arr, 0, arrNew, 1, arr.length);
        arrNew[0] = id;
        return arrNew;
    }

    public abstract void stopFeature();

    public abstract void startFeature();

    protected abstract Runnable run();
}
