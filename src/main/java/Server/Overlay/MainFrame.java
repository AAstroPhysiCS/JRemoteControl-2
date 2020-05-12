package Server.Overlay;

import Server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;

public class MainFrame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final int WIDTH = 800, HEIGHT = 600;
    private static Server server;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Controller controller = new ControllerImpl();
        loader.setController(controller);
        primaryStage.setScene(new Scene(loader.load(), WIDTH, HEIGHT));
        primaryStage.setTitle("JRemoteControl 2 - Controller 💩");
        primaryStage.setResizable(false);
        primaryStage.show();

        server = new Server(8000);

        Runtime.getRuntime().addShutdownHook(new Thread(MainFrame::disposeAll));

        primaryStage.setOnCloseRequest(windowEvent -> System.exit(0));
    }

    public static void disposeAll() {
        server.disposeAll();
        Platform.exit();
    }
}
