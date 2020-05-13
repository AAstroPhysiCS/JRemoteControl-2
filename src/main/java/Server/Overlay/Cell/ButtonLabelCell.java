package Server.Overlay.Cell;

import Server.ClientEntity.ClientEntity;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class ButtonLabelCell<T extends ClientEntity> extends ListCell<T> {

    private final AnchorPane pane = new AnchorPane();
    private final Button button = new Button("Connect");
    private final Label label = new Label();

    @Override
    public void updateItem(T item, boolean empty) {
        if(item == null){
            setText(null);
            setGraphic(null);
            return;
        }
        super.updateItem(item, empty);
        pane.setLayoutX(0);
        pane.setLayoutY(0);

        label.setText(item.toString().trim());
        label.setLayoutX(0);
        label.setLayoutY(0);

        button.setLayoutX(120);
        button.setLayoutY(55);

        addListeners();

        if(pane.getChildren().size() == 0)
            pane.getChildren().addAll(label, button);
        setText(null);
        setGraphic(pane);
    }

    private void addListeners(){
        button.setOnMouseClicked(mouseEvent -> {
            T item = getItem();
            System.out.println("CONNECTED! " + item.toString());
        });
    }

    public Button getButton() {
        return button;
    }
}
