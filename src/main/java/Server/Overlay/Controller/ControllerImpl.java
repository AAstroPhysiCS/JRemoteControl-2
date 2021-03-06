package Server.Overlay.Controller;

import Server.ClientEntity.Cell.ClientEntityCell;
import Server.Overlay.MainFrame;
import Tools.GraphicsConfigurator;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
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
        initEvents();

        GraphicsConfigurator.drawColor(desktopCaptureImageView, Color.LIGHT_GRAY);
        GraphicsConfigurator.drawColor(cameraCaptureImageView, Color.LIGHT_GRAY);

        volumeSlider.setMax(100);
        volumeSlider.setMin(0);
    }

    private void initEvents() {
        audioCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> audioCapturePane.setDisable(!newValue));
        desktopCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> desktopCapturePane.setDisable(!newValue));
        cameraCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> cameraCapturePane.setDisable(!newValue));
        textControlButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> textControlPane.setDisable(!newValue));

        listOfConnectableView.setCellFactory(e -> new ClientEntityCell<>(this));
        makeExpandable(cameraCaptureExpandButton, cameraCaptureImageView, cameraCaptureExpandImageView);
        makeExpandable(desktopCaptureExpandButton, desktopCaptureImageView, desktopCaptureExpandImageView);
        makeExpandable(textControlExpandButton, textAreaControl, textAreaControlExpand);
    }

    private void makeExpandable(Button button, Node node, Node expandNode) {
        button.setGraphic(new ImageView(MainFrame.class.getResource("/expandButton.png").toExternalForm()));
        button.setOpacity(0.6d);

        button.setVisible(false);
        node.setOnMouseEntered(mouseEvent -> button.setVisible(true));
        node.setOnMouseExited(mouseEvent -> button.setVisible(false));
        button.setOnMouseEntered(mouseEvent -> button.setVisible(true));
        button.setOnMouseExited(mouseEvent -> button.setVisible(false));

        button.setOnMouseClicked(mouseEvent -> {
            Group group = new Group();
            Scene scene = new Scene(group);

            group.getChildren().add(expandNode);

            Stage stage = new Stage();

            if (expandNode instanceof ImageView i) {
                stage.widthProperty().addListener((observableValue, number, t1) -> i.setFitWidth((Double) t1));
                stage.heightProperty().addListener((observableValue, number, t1) -> i.setFitHeight((Double) t1));
            }

            if (expandNode instanceof TextInputControl i) {
                i.setEditable(false);
                stage.setWidth(600);
                stage.setHeight(500);
                stage.setResizable(false);
                i.setPrefWidth(580);
                i.setPrefHeight(460);
            }
            stage.setScene(scene);
            stage.show();
        });
    }
}
