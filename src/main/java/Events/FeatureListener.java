package Events;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Ref;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class FeatureListener implements Listener<ClientEntity> {

    protected final Controller controller;
    protected final Server server;

    protected boolean runningFeature;

    protected final ExecutorService thread = Executors.newSingleThreadScheduledExecutor();

    protected final ObjectHandler<Message<?>> objectHandler;
    protected final PacketHandler packetHandler;

    public FeatureListener(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;
        objectHandler = new ObjectHandler<>();
        packetHandler = new PacketHandler(server.getSocket());
    }

    @Override
    public boolean call(Controller controller, ClientEntity e) {
        runningFeature = true;
        thread.execute(run(controller));
        return false;
    }

    protected abstract Runnable run(Controller controller);

    public abstract void initComponents(Ref<ClientEntity> selectedClient);
}
