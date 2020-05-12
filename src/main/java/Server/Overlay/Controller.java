package Server.Overlay;

import Server.ClientEntity.ClientEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public abstract class Controller implements Initializable {
    @FXML
    protected ImageView desktopCaptureImageView, cameraCaptureImageView;

    @FXML
    protected Slider volumeSlider;

    @FXML
    protected TextArea textAreaControl;

    @FXML
    protected CheckBox desktopCaptureButton, cameraCaptureButton, audioCaptureButton, textControlButton;

    @FXML
    protected TextField textFieldControl;

    @FXML
    protected AnchorPane desktopCapturePane, cameraCapturePane, audioCapturePane, textControlPane, controlPane;

    @FXML
    protected ListView<ClientEntity> listOfConnectableView;
}
