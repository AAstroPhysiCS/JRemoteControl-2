package Server.Overlay.Controller;

import Server.ClientEntity.Cell.ClientEntityCell;
import Server.Overlay.GraphicsConfigurator;
import Server.Overlay.MainFrame;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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

        GraphicsConfigurator.drawColor(desktopCaptureImageView, Color.LIGHT_GRAY);
        GraphicsConfigurator.drawColor(cameraCaptureImageView, Color.LIGHT_GRAY);

        listOfConnectableView.setCellFactory(e -> new ClientEntityCell<>(this));

        cameraCaptureExpandButton.setVisible(false);
        cameraCaptureExpandButton.setGraphic(new ImageView(MainFrame.class.getResource("/expandButton.png").toExternalForm()));
        cameraCaptureExpandButton.setOpacity(0.8d);

        cameraCaptureImageView.setOnMouseEntered(mouseEvent -> cameraCaptureExpandButton.setVisible(true));
        cameraCaptureImageView.setOnMouseExited(mouseEvent -> cameraCaptureExpandButton.setVisible(false));
        cameraCaptureExpandButton.setOnMouseEntered(mouseEvent -> cameraCaptureExpandButton.setVisible(true));
        cameraCaptureExpandButton.setOnMouseExited(mouseEvent -> cameraCaptureExpandButton.setVisible(false));

        cameraCaptureExpandButton.setOnMouseClicked(mouseEvent -> {
            Group group = new Group();
            Scene scene = new Scene(group);

            group.getChildren().add(cameraCaptureExpandImageView);

            Stage stage = new Stage();
            stage.widthProperty().addListener((observableValue, number, t1) -> cameraCaptureExpandImageView.setFitWidth((Double) t1));
            stage.heightProperty().addListener((observableValue, number, t1) -> cameraCaptureExpandImageView.setFitHeight((Double) t1));
            stage.setScene(scene);
            stage.show();
        });
    }
}
