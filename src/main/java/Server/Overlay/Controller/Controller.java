package Server.Overlay.Controller;

import Server.ClientEntity.ClientEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public abstract class Controller implements Initializable {
    @FXML
    public ImageView desktopCaptureImageView, cameraCaptureImageView;

    public ImageView cameraCaptureExpandImageView = new ImageView();
    public ImageView desktopCaptureExpandImageView = new ImageView();

    @FXML
    public Slider volumeSlider, audioCaptureSlider;

    @FXML
    public TextArea textAreaControl;

    public TextArea textAreaControlExpand = new TextArea();

    @FXML
    public CheckBox desktopCaptureButton, cameraCaptureButton, audioCaptureButton, textControlButton, cmdControlButton, chatControlButton;

    @FXML
    public TextField textFieldControl, audioCapturingTime;

    @FXML
    public AnchorPane desktopCapturePane, cameraCapturePane, audioCapturePane, textControlPane, controlPane;

    @FXML
    public ListView<ClientEntity> listOfConnectableView;

    @FXML
    public Label statusLabel, audioCaptureSliderLabel, audioCapturePathLabel;

    @FXML
    public Button cameraCaptureExpandButton, desktopCaptureExpandButton, textControlExpandButton, audioCapturingConf, audioCaptureStart, audioCaptureSelectFolder;

    public ClientEntity item;

    public final Stage primaryStage;

    public void addClients(ClientEntity clientEntity) {
        if (!listOfConnectableView.getItems().contains(clientEntity)) {
            clientEntity.setController(this);
            listOfConnectableView.getItems().add(clientEntity);
        }
    }

    public Controller(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public <T extends ClientEntity> void setFocus(T item) {
        this.item = item;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
