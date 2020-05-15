package Server.Overlay.Controller;

import Server.ClientEntity.Cell.ClientEntityCell;
import Server.Overlay.Drawer;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerImpl extends Controller {

    public ControllerImpl(Stage stage) {
        super(stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        audioCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> audioCapturePane.setDisable(!newValue));
        desktopCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> desktopCapturePane.setDisable(!newValue));
        cameraCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> cameraCapturePane.setDisable(!newValue));
        textControlButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> textControlPane.setDisable(!newValue));

        Drawer.drawColor(desktopCaptureImageView, Color.LIGHT_GRAY);
        Drawer.drawColor(cameraCaptureImageView, Color.LIGHT_GRAY);

        listOfConnectableView.setCellFactory(e -> new ClientEntityCell<>(this));
    }
}
