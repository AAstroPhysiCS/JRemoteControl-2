package Server.Overlay.Cell;

import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class ButtonLabelCell<T extends ClientEntity> extends ListCell<T> {

    private final AnchorPane pane = new AnchorPane();
    private final Button connectButton = new Button("Connect");
    private final Label label = new Label();

    private final Controller controller;

    public ButtonLabelCell(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void updateItem(T item, boolean empty) {
        if (item == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        super.updateItem(item, empty);
        pane.setLayoutX(0);
        pane.setLayoutY(0);

        label.setText(item.toString());
        label.setLayoutX(0);
        label.setLayoutY(0);

        connectButton.setLayoutX(120);
        connectButton.setLayoutY(55);

        addListeners();

        if (pane.getChildren().size() == 0)
            pane.getChildren().addAll(label, connectButton);
        setText(null);
        setGraphic(pane);
    }

    private void addListeners() {
        connectButton.setOnAction(mouseEvent -> {
            T item = getItem();
            controller.statusLabel.setTextFill(Color.color(74/255d,188/255d,140/255d));
            controller.statusLabel.setText("Status: Connected to " + item.getInfo()[item.getInfo().length - 1]);
            controller.controlPane.setDisable(false);
            controller.setFocus(item);
        });
    }

    public Button getConnectButton() {
        return connectButton;
    }
}
