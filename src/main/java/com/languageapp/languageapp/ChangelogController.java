package com.languageapp.languageapp;

import com.languageapp.entities.Change;
import com.languageapp.files.ChangesDataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class ChangelogController {
    private static final Logger logger = LoggerFactory.getLogger(ChangelogController.class);
    @FXML
    private TableView<Change<?>> changesTableView;
    @FXML
    private TableColumn<Change<?>, String> beforeTableColumn;
    @FXML
    private TableColumn<Change<?>, String> afterTableColumn;
    @FXML
    private TableColumn<Change<?>, String> userTableColumn;
    @FXML
    private TableColumn<Change<?>, String> dateTableColumn;
    @FXML
    private TableColumn<Change<?>, String> typeTableColumn;

    public void initialize() {
        try {
            var changes = ChangesDataStore.getChanges();
            Collections.reverse(changes);
            beforeTableColumn.setCellValueFactory(change -> {
                if (change.getValue().getOldValue() != null)
                    return new SimpleStringProperty(change.getValue().getOldValue().toString());
                else
                    return new SimpleStringProperty("null");
            });
            afterTableColumn.setCellValueFactory(change -> {
                if (change.getValue().getNewValue() != null)
                    return new SimpleStringProperty(change.getValue().getNewValue().toString());
                else
                    return new SimpleStringProperty("null");
            });
            userTableColumn.setCellValueFactory(change -> new SimpleStringProperty(change.getValue().getUser().username()));
            dateTableColumn.setCellValueFactory(change -> new SimpleStringProperty(change.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. - HH:mm"))));
            typeTableColumn.setCellValueFactory(change -> new SimpleStringProperty(change.getValue().getType().toString()));
            changesTableView.setItems(FXCollections.observableList(changes));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
