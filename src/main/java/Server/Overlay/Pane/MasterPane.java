package Server.Overlay.Pane;

import Handler.PacketHandler;
import Handler.Message;
import Handler.ObjectHandler;
import Server.ClientEntity.ClientEntity;
import Server.ClientEntity.ClientEvent;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Disposeable;
import Tools.Network.NetworkInterface;
import javafx.application.Platform;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterPane implements Disposeable {

    private final Controller controller;
    private final Server server;

    private final ObjectHandler<Message<?>> serverObjectHandler;
    private final PacketHandler serverFeaturePacketHandler;

    private final ClientEvent changeEvent;
    private ClientEntity selectedClient;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MasterPane(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;
        changeEvent = new ClientEvent(controller);

        serverObjectHandler = server.getObjectHandler();
        serverFeaturePacketHandler = server.getFeaturePacketHandler();

        scheduledExecutorService.scheduleAtFixedRate(updateComponents(), 0, 1000, TimeUnit.MILLISECONDS);
        initComponents();
    }

    private void initComponents(){
        controller.desktopCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue && selectedClient != null){
                try {
                    serverFeaturePacketHandler.send(new byte[]{NetworkInterface.CommandByte.CAMERA_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Runnable updateComponents() {
        return () -> Platform.runLater(() -> {   //to not have exceptions...
            for (ClientEntity e : server.getAllClients()) {
                controller.addClients(e);
                changeEvent.addListener(e.getEventListener());
            }
            selectedClient = changeEvent.call();
        });
    }

    @Override
    public void disposeAll() {
        scheduledExecutorService.shutdownNow();
    }
}
