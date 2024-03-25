package com.languageapp.languageapp;

import com.languageapp.database.Database;
import com.languageapp.entities.DictionaryEntry;
import com.languageapp.entities.Flashcard;
import com.languageapp.entities.MultipleChoiceQuestion;
import com.languageapp.exceptions.DatabaseException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryController {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);
    @FXML
    private TextField searchField;
    @FXML
    private TableView<DictionaryEntry> dictionaryTableView;
    @FXML
    private TableColumn<DictionaryEntry, String> englishWordColumn;
    @FXML
    private TableColumn<DictionaryEntry, String> croatianWordColumn;

    public void initialize() {
        try {
            List<DictionaryEntry> dictionaryEntries = Database.getDictionaryEntries();
            List<Flashcard> flashcards = Database.getFlashcards();
            List<MultipleChoiceQuestion> multipleChoiceQuestions = Database.getQuestions();
            List<DictionaryEntry> flashcardEntries = flashcards.stream().map(flashcard -> new DictionaryEntry(flashcard.getCorrectAnswer(), flashcard.getText())).toList();
            List<DictionaryEntry> quizEntries = multipleChoiceQuestions.stream().map(multipleChoiceQuestion -> new DictionaryEntry(multipleChoiceQuestion.getCorrectAnswer(), multipleChoiceQuestion.getText())).toList();
            dictionaryEntries.addAll(flashcardEntries);
            dictionaryEntries.addAll(quizEntries);
            Map<String, String> filterDuplicatesMap = new HashMap<>();
            dictionaryEntries.forEach(entry -> filterDuplicatesMap.put(entry.getText(), entry.getCorrectAnswer()));
            List<DictionaryEntry> dictionaryEntriesNoDuplicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : filterDuplicatesMap.entrySet()) {
                dictionaryEntriesNoDuplicates.add(new DictionaryEntry(entry.getKey(), entry.getValue()));
            }
            FilteredList<DictionaryEntry> filteredDictionaryEntries = new FilteredList<>(FXCollections.observableArrayList(dictionaryEntriesNoDuplicates), entry -> true);
            searchField.textProperty().addListener(obs -> {
                String filter = searchField.getText();
                if (filter == null || filter.isEmpty())
                    filteredDictionaryEntries.setPredicate(entry -> true);
                else
                    filteredDictionaryEntries.setPredicate(entry -> entry.getCorrectAnswer().contains(filter) || entry.getText().contains(filter));
            });
            englishWordColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));
            croatianWordColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorrectAnswer()));
            englishWordColumn.setStyle("-fx-alignment: CENTER;");
            croatianWordColumn.setStyle("-fx-alignment: CENTER;");
            dictionaryTableView.setItems(filteredDictionaryEntries);
        } catch (DatabaseException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
