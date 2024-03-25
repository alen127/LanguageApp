package com.languageapp.languageapp;

import com.languageapp.database.Database;
import com.languageapp.entities.Flashcard;
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

public class ManageFlashcardsController {
    private static final Logger logger = LoggerFactory.getLogger(ManageFlashcardsController.class);
    List<Flashcard> flashcards;
    @FXML
    private ChoiceBox<Long> idChoiceBox;
    @FXML
    private TextField frontTextField;
    @FXML
    private TextField backTextField;
    @FXML
    private TableView<Flashcard> flashcardTableView;
    @FXML
    private TableColumn<Flashcard, String> idTableColumn;
    @FXML
    private TableColumn<Flashcard, String> frontTableColumn;
    @FXML
    private TableColumn<Flashcard, String> backTableColumn;

    public void initialize() {
        try {
            flashcards = Database.getFlashcards();
            flashcards.sort(Comparator.comparingLong(Flashcard::getId));
            idChoiceBox.setItems(FXCollections.observableList(flashcards.stream().map(Flashcard::getId).toList()));
            flashcardTableView.setItems(FXCollections.observableList(flashcards));
        } catch (DatabaseException e) {
            logger.error(e.getMessage(), e);
        }
        idTableColumn.setCellValueFactory(flashcard -> new SimpleStringProperty(flashcard.getValue().getId().toString()));
        frontTableColumn.setCellValueFactory(flashcard -> new SimpleStringProperty(flashcard.getValue().getText()));
        backTableColumn.setCellValueFactory(flashcard -> new SimpleStringProperty(flashcard.getValue().getCorrectAnswer()));
    }

    public void addFlashcard() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(frontTextField, backTextField), List.of("Front", "Back"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Flashcards management", "Please fill out the required fields.", msg);
        else {
            try {
                Flashcard newFlashcard = new Flashcard(frontTextField.getText(), backTextField.getText());
                Database.addFlashcard(newFlashcard);
                ChangesDataStore.recordChange(null, newFlashcard);
                AlertMaker.showInformationAlert("Flashcards management", "Flashcard successfully added.", "");
            } catch (DatabaseException | IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void editFlashcard() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Flashcards management", "Please fill out the required fields.", msg);
        else {
            if (frontTextField.getText().isBlank() && backTextField.getText().isBlank())
                AlertMaker.showErrorAlert("Flashcards management", "Please fill out at least one other field.", "No changes were made.");
            else {
                Optional<ButtonType> confirmButtonResult = AlertMaker.showConfirmationAlert("Flashcards management", "Are you sure?", "Click OK to procceed.");
                if (confirmButtonResult.isPresent() && confirmButtonResult.orElse(null).equals(ButtonType.OK)) {
                    Flashcard oldFlashcard = flashcards.stream().filter(flashcard -> flashcard.getId().equals(idChoiceBox.getValue())).findAny().orElse(null);
                    String newFront = oldFlashcard.getText();
                    String newBack = oldFlashcard.getCorrectAnswer();


                    if (!frontTextField.getText().isBlank())
                        newFront = frontTextField.getText();
                    if (!backTextField.getText().isBlank())
                        newBack = backTextField.getText();
                    try {
                        Flashcard newFlashcard = new Flashcard(oldFlashcard.getId(), newFront, newBack);
                        Database.editFlashcard(newFlashcard);
                        ChangesDataStore.recordChange(oldFlashcard, newFlashcard);
                        AlertMaker.showInformationAlert("Flashcards management", "Flashcard succesfully edited.", "");
                    } catch (DatabaseException | IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    public void deleteFlashcard() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Flashcards management", "Please fill out the required fields.", msg);
        else {
            Optional<ButtonType> confirmButtonResult = AlertMaker.showConfirmationAlert("Flashcards management", "Are you sure?", "Click OK to procceed.");
            if (confirmButtonResult.isPresent() && confirmButtonResult.orElse(null).equals(ButtonType.OK)) {
                Flashcard flashcardToBeRemoved = flashcards.stream().filter(flashcard -> flashcard.getId().equals(idChoiceBox.getValue())).findAny().orElse(null);
                try {
                    Database.deleteFlashcard(flashcardToBeRemoved);
                    ChangesDataStore.recordChange(flashcardToBeRemoved, null);
                    AlertMaker.showInformationAlert("Flashcards management", "Flashcard succesfully deleted.", "");
                } catch (DatabaseException | IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void searchFlashcards() {
        String front = frontTextField.getText();
        String back = backTextField.getText();
        String id = Optional.ofNullable(idChoiceBox.getValue()).map(Object::toString).orElse("");
        List<Flashcard> filteredFlashcards = flashcards.stream().filter(flashcard -> flashcard.getId().toString().contains(id) && flashcard.getText().contains(front) && flashcard.getCorrectAnswer().contains(back)).toList();
        flashcardTableView.setItems(FXCollections.observableList(filteredFlashcards));
    }
}
