package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class EditButtonTableCell<S, T> extends TableCell<S, T> {
    private final Button editButton;

    public EditButtonTableCell(Callback<S, Void> callback) {
        editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            S item = getTableView().getItems().get(getIndex());
            callback.call(item);
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(editButton);
        }
    }
}
