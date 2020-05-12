package Server.Overlay;

import Server.Overlay.Node.OSInfoNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ImageView desktopCaptureImageView, cameraCaptureImageView;

    @FXML
    private Slider volumeSlider;

    @FXML
    private TextArea cmdTextArea;

    @FXML
    private TextField cmdTextField;

    @FXML
    private CheckBox desktopCaptureButton, cameraCaptureButton, audioCaptureButton, cmdCaptureButton;

    @FXML
    private ListView<OSInfoNode> listOfConnectableView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public CheckBox getAudioCaptureButton() {
        return audioCaptureButton;
    }

    public CheckBox getCameraCaptureButton() {
        return cameraCaptureButton;
    }

    public CheckBox getCmdCaptureButton() {
        return cmdCaptureButton;
    }

    public CheckBox getDesktopCaptureButton() {
        return desktopCaptureButton;
    }

    public ImageView getCameraCaptureImageView() {
        return cameraCaptureImageView;
    }

    public ImageView getDesktopCaptureImageView() {
        return desktopCaptureImageView;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public TextArea getCmdTextArea() {
        return cmdTextArea;
    }

    public TextField getCmdTextField() {
        return cmdTextField;
    }

    public ListView<OSInfoNode> getListOfConnectableView() {
        return listOfConnectableView;
    }
}
