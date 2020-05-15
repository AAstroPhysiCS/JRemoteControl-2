package Server.Overlay.Pane;

import Handler.Message;
import Handler.ObjectHandler;
import Server.ClientEntity.ClientEntity;
import Server.ClientEntity.ClientEvent;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Disposeable;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterPane implements Disposeable {

    private final Controller controller;
    private final Server server;

    private final ObjectHandler<Message<?>> serverMessageHandler;

    private final ClientEvent changeEvent;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MasterPane(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;
        changeEvent = new ClientEvent(controller);

        serverMessageHandler = server.getServerObjectHandler();

        scheduledExecutorService.scheduleAtFixedRate(updateComponents(), 0, 1000, TimeUnit.MILLISECONDS);
        initComponents();
    }

    private void initComponents(){
        controller.desktopCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("HELLO");
            }
        });
    }

    public Runnable updateComponents() {
        return () -> Platform.runLater(() -> {   //to not have exceptions...
            for (ClientEntity e : server.getAllClients()) {
                controller.addClients(e);
                changeEvent.addListener(e.getEventListener());
            }
            ClientEntity selectedClient = changeEvent.call();
            if (selectedClient != null) {

            }
        });
    }

    @Override
    public void disposeAll() {
        scheduledExecutorService.shutdownNow();
    }
}
