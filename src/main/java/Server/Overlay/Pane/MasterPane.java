package Server.Overlay.Pane;

import Events.FeatureListener;
import Server.ClientEntity.ClientEntity;
import Server.ClientEntity.ClientEvent;
import Server.FeatureListener.CMDControlListener;
import Server.FeatureListener.CameraCaptureListener;
import Server.FeatureListener.DesktopCaptureListener;
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

    private final ClientEvent changeEvent;
    private ClientEntity selectedClient;

    private final FeatureListener cameraCaptureListener, desktopCaptureListener, cmdControlListener;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MasterPane(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;

        changeEvent = new ClientEvent();

        cameraCaptureListener = new CameraCaptureListener(controller, server);
        desktopCaptureListener = new DesktopCaptureListener(controller, server);
        cmdControlListener = new CMDControlListener(controller, server);

        scheduledExecutorService.scheduleAtFixedRate(updateComponents(), 0, 500, TimeUnit.MILLISECONDS);
    }

    public Runnable updateComponents() {
        return () -> Platform.runLater(() -> {   //to not have exceptions...
            for (ClientEntity e : server.getAllClients()) {
                controller.addClients(e);
                changeEvent.addListener(e.getEventListener());
            }
            selectedClient = changeEvent.call();
            if (selectedClient != null) {
                desktopCaptureListener.call(selectedClient);
                cameraCaptureListener.call(selectedClient);
                cmdControlListener.call(selectedClient);
            }
        });
    }

    @Override
    public void disposeAll() {
        scheduledExecutorService.shutdown();
    }
}
