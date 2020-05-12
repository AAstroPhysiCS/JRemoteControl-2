package Server.Overlay;

import Server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        Controller controller = new Controller();
        loader.setController(controller);
        primaryStage.setScene(new Scene(loader.load(), WIDTH, HEIGHT));
        primaryStage.setTitle("JRemoteControl 2 - Controller");
        primaryStage.setResizable(false);
        primaryStage.show();
        server = new Server(8000);

        controller.getAudioCaptureButton().selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) System.out.println("CHECKED");
        });

        Runtime.getRuntime().addShutdownHook(new Thread(MainFrame::disposeAll));
    }

    public static void disposeAll() {
        server.disposeAll();
        System.exit(0);
    }
}
