package Server.Overlay;

import Server.ClientEntity.ClientEntity;
import Server.Server;
import Tools.Disposeable;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterPane implements Disposeable {

    private final Controller controller;
    private final Server server;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MasterPane(Controller controller, Server server) {
        this.controller = controller;
        this.server = server;

        scheduledExecutorService.scheduleAtFixedRate(updateComponents(), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public Runnable updateComponents() {
        return () -> {
            Platform.runLater(() -> {   //to not have exceptions...
                for (ClientEntity e : server.getAllClients())
                    controller.addClients(e);
            });
        };
    }

    @Override
    public void disposeAll() {
        scheduledExecutorService.shutdown();
    }
}
