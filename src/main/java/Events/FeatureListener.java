package Events;

import Handler.Message;
import Handler.ObjectHandler;
import Handler.PacketHandler;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Disposeable;
import Tools.Ref;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class FeatureListener implements Listener<ClientEntity>, Disposeable {

    protected final Controller controller;
    protected final Server server;

    protected boolean runningFeature;

    protected final ExecutorService thread = Executors.newSingleThreadScheduledExecutor();

    protected final ObjectHandler<Message<?>> objectHandler;
    protected PacketHandler packetHandler;

    private final Ref<ClientEntity> reference = new Ref<>();

    public FeatureListener(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;
        objectHandler = new ObjectHandler<>();
        packetHandler = new PacketHandler(server.getSocket());
        initComponents(reference);
    }

    @Override
    public boolean listen(ClientEntity e) {
        reference.setObj(e);
        return true;
    }

    protected abstract Runnable run(Controller controller);

    protected abstract void initComponents(Ref<ClientEntity> selectedClientSup);
}
