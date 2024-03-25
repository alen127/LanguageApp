package com.languageapp.languageapp;

import com.languageapp.database.Database;
import com.languageapp.entities.DictionaryEntry;
import com.languageapp.exceptions.DatabaseException;
import com.languageapp.files.ChangesDataStore;
import com.languageapp.util.AlertMaker;
import com.languageapp.util.ManageUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ManageDictionaryController {
    private static final Logger logger = LoggerFactory.getLogger(ManageDictionaryController.class);
    @FXML
    private ChoiceBox<Long> idChoiceBox;
    @FXML
    private TextField englishTextField;
    @FXML
    private TextField croatianTextField;
    @FXML
    private TableView<DictionaryEntry> dictionaryTableView;
    @FXML
    private TableColumn<DictionaryEntry, String> idTableColumn;
    @FXML
    private TableColumn<DictionaryEntry, String> englishTableColumn;
    @FXML
    private TableColumn<DictionaryEntry, String> croatianTableColumn;
    private List<DictionaryEntry> dictionaryEntries;

    public void initialize() {
        try {
            dictionaryEntries = Database.getDictionaryEntries();
            dictionaryEntries.sort(Comparator.comparing(DictionaryEntry::getId));
            dictionaryTableView.setItems(FXCollections.observableList(dictionaryEntries));
            idChoiceBox.setItems(FXCollections.observableList(dictionaryEntries.stream().map(DictionaryEntry::getId).toList()));
        } catch (DatabaseException e) {
            logger.error(e.getMessage(), e);
        }
        idTableColumn.setCellValueFactory(dictionaryEntry -> new SimpleStringProperty((dictionaryEntry.getValue().getId().toString())));
        englishTableColumn.setCellValueFactory(dictionaryEntry -> new SimpleStringProperty(dictionaryEntry.getValue().getText()));
        croatianTableColumn.setCellValueFactory(dictionaryEntry -> new SimpleStringProperty(dictionaryEntry.getValue().getCorrectAnswer()));
    }

    public void addDictionaryEntry() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(englishTextField, croatianTextField), List.of("English word", "Croatian word"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Dictionary management", "Please fill out the required fields.", msg);
        else {
            try {
                DictionaryEntry newEntry = new DictionaryEntry(englishTextField.getText(), croatianTextField.getText());
                Database.addDictionaryEntry(newEntry);
                ChangesDataStore.recordChange(null, newEntry);
                AlertMaker.showInformationAlert("Dictionary management", "Dictionary entry successfully added.", "");
            } catch (DatabaseException | IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void editDictionaryEntry() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Dictionary management", "Please fill out the required fields.", msg);
        else {
            if (englishTextField.getText().isBlank() && croatianTextField.getText().isBlank())
                AlertMaker.showErrorAlert("Dictionary management", "Please fill out at least one other field.", "No changes were made.");
            else {
                Optional<ButtonType> confirmButtonResult = AlertMaker.showConfirmationAlert("Dictionary management", "Are you sure?", "Click OK to procceed.");
                if (confirmButtonResult.isPresent() && confirmButtonResult.orElse(null).equals(ButtonType.OK)) {
                    DictionaryEntry oldEntry = dictionaryEntries.stream().filter(dictionaryEntry -> dictionaryEntry.getId().equals(idChoiceBox.getValue())).findAny().orElse(null);
                    String newEnglishWord = oldEntry.getText();
                    String newCroatianWord = oldEntry.getCorrectAnswer();
                    if (!englishTextField.getText().isBlank())
                        newEnglishWord = englishTextField.getText();
                    if (!croatianTextField.getText().isBlank())
                        newCroatianWord = croatianTextField.getText();
                    try {
                        DictionaryEntry newEntry = new DictionaryEntry(oldEntry.getId(), newEnglishWord, newCroatianWord);
                        Database.editDictionaryEntry(newEntry);
                        ChangesDataStore.recordChange(oldEntry, newEntry);
                        AlertMaker.showInformationAlert("Dictionary management", "Dictionary entry succesfully edited.", "");
                    } catch (DatabaseException | IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }
        }
    }

    public void deleteDictionaryEntry() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Dictionary management", "Please fill out the required fields.", msg);
        else {
            Optional<ButtonType> confirmButtonResult = AlertMaker.showConfirmationAlert("Dictionary management", "Are you sure?", "Click OK to procceed.");
            if (confirmButtonResult.isPresent() && confirmButtonResult.orElse(null).equals(ButtonType.OK)) {
                DictionaryEntry entryToBeRemoved = dictionaryEntries.stream().filter(dictionaryEntry -> dictionaryEntry.getId().equals(idChoiceBox.getValue())).findAny().orElse(null);
                try {
                    Database.deleteDictionaryEntry(entryToBeRemoved);
                    ChangesDataStore.recordChange(entryToBeRemoved, null);
                    AlertMaker.showInformationAlert("Dictionary management", "Dictionary entry succesfully deleted.", "");
                } catch (DatabaseException | IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
