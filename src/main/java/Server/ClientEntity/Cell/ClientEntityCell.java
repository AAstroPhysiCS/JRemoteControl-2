package Server.ClientEntity.Cell;

import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientEntityCell<T extends ClientEntity> extends ListCell<T> {

    private final AnchorPane pane = new AnchorPane();
    private final Button connectButton = new Button("Connect");
    private final Button expandButton = new Button("...");
    private final Label label = new Label();

    private Scene envScene;
    private Stage envStage = new Stage();
    private final TextArea textArea = new TextArea();

    private final Controller controller;

    public ClientEntityCell(Controller controller) {
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

        connectButton.setLayoutX(130);
        connectButton.setLayoutY(25);

        expandButton.setLayoutX(160);
        expandButton.setLayoutY(75);

        String[] modified = item.getEnv().toString().replace("{", "").replace("}", "").split(", ");
        textArea.setText(item.toString() + String.join("\n",modified));

        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);

        Group group = new Group(textArea);
        envScene = new Scene(group);
        if (group.getChildren().size() == 0) {
            group.getChildren().add(textArea);
        }

        envStage.setScene(envScene);

        addListeners();

        setText(null);
        if (pane.getChildren().size() == 0)
            pane.getChildren().addAll(label, connectButton, expandButton);
        setGraphic(pane);
    }

    private void addListeners() {
        connectButton.setOnAction(mouseEvent -> {
            T item = getItem();
            controller.statusLabel.setTextFill(Color.color(74 / 255d, 188 / 255d, 140 / 255d));
            controller.statusLabel.setText("Status: Connected to " + item.getInfo()[item.getInfo().length - 1]);
            controller.controlPane.setDisable(false);
            controller.setFocus(item);
        });
        expandButton.setOnMouseClicked(mouseEvent -> envStage.show());
    }
}
