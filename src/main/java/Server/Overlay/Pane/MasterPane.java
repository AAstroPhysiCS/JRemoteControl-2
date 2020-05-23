package Server.Overlay.Pane;

import Events.FeatureListener;
import Server.ClientEntity.ClientEntity;
import Events.ChangeEvent;
import Server.FeatureListener.CMDControlListener;
import Server.FeatureListener.CameraCaptureListener;
import Server.FeatureListener.ChatControlListener;
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

    private final ChangeEvent<ClientEntity> selectChangeEvent;
    private ClientEntity selectedClient;

    private final FeatureListener cameraCaptureListener, desktopCaptureListener, cmdControlListener, chatControlListener;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MasterPane(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;

        selectChangeEvent = new ChangeEvent<>();

        cameraCaptureListener = new CameraCaptureListener(controller, server);
        desktopCaptureListener = new DesktopCaptureListener(controller, server);
        cmdControlListener = new CMDControlListener(controller, server);
        chatControlListener = new ChatControlListener(controller, server);

        scheduledExecutorService.scheduleAtFixedRate(updateComponents(), 0, 500, TimeUnit.MILLISECONDS);
    }

    public Runnable updateComponents() {
        return () -> Platform.runLater(() -> {   //to not have exceptions...
            for (ClientEntity e : server.getAllClients()) {
                controller.addClients(e);
                selectChangeEvent.addListener(e.getEventListener());
            }
            selectedClient = selectChangeEvent.onChange();
            if (selectedClient != null) {
                desktopCaptureListener.listen(selectedClient);
                cameraCaptureListener.listen(selectedClient);
                cmdControlListener.listen(selectedClient);
                chatControlListener.listen(selectedClient);
            }
        });
    }

    @Override
    public void disposeAll() {
        scheduledExecutorService.shutdown();
    }
}
