package Server.Overlay.Cell;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class ButtonCell<T> extends ListCell<T> {

    private final HBox hBox = new HBox();
    private final Button button = new Button("Connect");
    private final Label label = new Label();

    private final T obj;

    public ButtonCell(T obj){
        this.obj = obj;
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.setText(null);
        label.setText(item.toString());

        HBox.setMargin(button, new Insets(0, 20, 0, 0));
        HBox.setMargin(label, new Insets(0, 50, 0, 0));

        hBox.getChildren().addAll(button, label);
        super.updateItem(item, empty);
        setGraphic(label);
    }

    public T getObj() {
        return obj;
    }
}
