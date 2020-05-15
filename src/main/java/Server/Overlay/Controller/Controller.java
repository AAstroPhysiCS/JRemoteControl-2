package Server.Overlay.Controller;

import Server.ClientEntity.ClientEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class Controller implements Initializable {
    @FXML
    public ImageView desktopCaptureImageView, cameraCaptureImageView;

    @FXML
    public Slider volumeSlider;

    @FXML
    public TextArea textAreaControl;

    @FXML
    public CheckBox desktopCaptureButton, cameraCaptureButton, audioCaptureButton, textControlButton;

    @FXML
    public TextField textFieldControl;

    @FXML
    public AnchorPane desktopCapturePane, cameraCapturePane, audioCapturePane, textControlPane, controlPane;

    @FXML
    public ListView<ClientEntity> listOfConnectableView;

    @FXML
    public Label statusLabel;

    public ClientEntity item;
    public final Stage stage;

    public void addClients(ClientEntity clientEntity) {
        if (!listOfConnectableView.getItems().contains(clientEntity)) {
            listOfConnectableView.getItems().add(clientEntity);
        }
    }

    public Controller(Stage stage){
        this.stage = stage;
    }

    public <T extends ClientEntity> void setFocus(T item) {
        this.item = item;
    }

    public Stage getStage() {
        return stage;
    }
}
