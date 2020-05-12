package Server.Overlay;

import Server.ClientEntity.ClientEntity;
import Server.Overlay.Cell.ButtonCell;
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
    protected ListView<ButtonCell<ClientEntity>> listOfConnectableView;

    public void addClients(ClientEntity clientEntity) {
        ButtonCell<ClientEntity> cell = new ButtonCell<>(clientEntity);
        for (int i = 0; i < listOfConnectableView.getItems().size(); i++) {
            if (!listOfConnectableView.getItems().get(i).getObj().equals(cell.getObj()))
                listOfConnectableView.getItems().add(cell);
        }

        if (listOfConnectableView.getItems().size() == 0) {
            listOfConnectableView.getItems().add(cell);
        }
    }
}
