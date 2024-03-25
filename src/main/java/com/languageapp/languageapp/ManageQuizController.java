package com.languageapp.languageapp;

import com.languageapp.database.Database;
import com.languageapp.entities.MultipleChoiceQuestion;
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

public class ManageQuizController {
    private static final Logger logger = LoggerFactory.getLogger(ManageQuizController.class);
    @FXML
    private ChoiceBox<Long> idChoiceBox;
    @FXML
    private TextField questionTextField;
    @FXML
    private TextField option1TextField;
    @FXML
    private TextField option2TextField;
    @FXML
    private TextField option3TextField;
    @FXML
    private TextField option4TextField;
    @FXML
    private ChoiceBox<Integer> correctOptionChoiceBox;
    @FXML
    private TableView<MultipleChoiceQuestion> quizTableView;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> idTableColumn;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> questionTableColumn;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> option1TableColumn;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> option2TableColumn;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> option3TableColumn;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> option4TableColumn;
    @FXML
    private TableColumn<MultipleChoiceQuestion, String> correctOptionTableColumn;
    private List<MultipleChoiceQuestion> questions;

    public void initialize() {
        try {
            questions = Database.getQuestions();
            questions.sort(Comparator.comparingLong(MultipleChoiceQuestion::getId));
            idChoiceBox.setItems(FXCollections.observableList(questions.stream().map(MultipleChoiceQuestion::getId).toList()));
            correctOptionChoiceBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4));

            idTableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getId().toString()));
            questionTableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getText()));
            option1TableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getPossibleAnswers().get(0)));
            option2TableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getPossibleAnswers().get(1)));
            option3TableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getPossibleAnswers().get(2)));
            option4TableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getPossibleAnswers().get(3)));
            correctOptionTableColumn.setCellValueFactory(question -> new SimpleStringProperty(question.getValue().getCorrectAnswerPosition().toString()));
            quizTableView.setItems(FXCollections.observableList(questions));
        } catch (DatabaseException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void addQuestion() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(questionTextField, option1TextField, option2TextField, option3TextField, option4TextField, correctOptionChoiceBox),
                List.of("Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct option"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Quiz management", "Please fill out the required fields.", msg);
        else {
            try {
                MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(questionTextField.getText(), List.of(option1TextField.getText(), option2TextField.getText(),
                        option3TextField.getText(), option4TextField.getText()), correctOptionChoiceBox.getValue());
                Database.addQuestion(newQuestion);
                ChangesDataStore.recordChange(null, newQuestion);
                AlertMaker.showInformationAlert("Quiz management", "Question successfully added.", "");
            } catch (DatabaseException | IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void editQuestion() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Quiz management", "Please fill out the required fields.", msg);
        else {
            if (questionTextField.getText().isBlank() && option1TextField.getText().isBlank() && option2TextField.getText().isBlank() && option3TextField.getText().isBlank() && option4TextField.getText().isBlank() && correctOptionChoiceBox.getValue() == null)
                AlertMaker.showErrorAlert("Quiz management", "Please fill out at least one other field.", "No changes were made.");
            else {
                Optional<ButtonType> confirmChangeResult = AlertMaker.showConfirmationAlert("Quiz management", "Are you sure?", "Click OK to proceed.");
                if (confirmChangeResult.isPresent() && confirmChangeResult.orElse(null).equals(ButtonType.OK)) {
                    MultipleChoiceQuestion oldQuestion = questions.stream().filter(question -> question.getId().equals(idChoiceBox.getValue())).findAny().orElse(null);
                    String newQuestionText = oldQuestion.getText();
                    String newOption1 = oldQuestion.getPossibleAnswers().get(0);
                    String newOption2 = oldQuestion.getPossibleAnswers().get(1);
                    String newOption3 = oldQuestion.getPossibleAnswers().get(2);
                    String newOption4 = oldQuestion.getPossibleAnswers().get(3);
                    Integer newCorrectOption = oldQuestion.getCorrectAnswerPosition();


                    if (!questionTextField.getText().isBlank())
                        newQuestionText = questionTextField.getText();
                    if (!option1TextField.getText().isBlank())
                        newOption1 = option1TextField.getText();
                    if (!option2TextField.getText().isBlank())
                        newOption2 = option2TextField.getText();
                    if (!option3TextField.getText().isBlank())
                        newOption3 = option3TextField.getText();
                    if (!option4TextField.getText().isBlank())
                        newOption4 = option4TextField.getText();
                    if (correctOptionChoiceBox.getValue() != null)
                        newCorrectOption = correctOptionChoiceBox.getValue();
                    try {
                        MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(oldQuestion.getId(), newQuestionText, List.of(newOption1, newOption2, newOption3, newOption4), newCorrectOption);
                        Database.editQuestion(newQuestion);
                        ChangesDataStore.recordChange(oldQuestion, newQuestion);
                        AlertMaker.showInformationAlert("Quiz management", "Question successfully added.", "");
                    } catch (DatabaseException | IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    public void deleteQuestion() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("Quiz management", "Please fill out the required fields.", msg);
        else {
            Optional<ButtonType> confirmChangeResult = AlertMaker.showConfirmationAlert("Quiz management", "Are you sure?", "Click OK to proceed.");
            if (confirmChangeResult.isPresent() && confirmChangeResult.orElse(null).equals(ButtonType.OK)) {
                MultipleChoiceQuestion oldQuestion = questions.stream().filter(question -> question.getId().equals(idChoiceBox.getValue())).findAny().orElse(null);
                try {
                    Database.deleteQuestion(oldQuestion);
                    ChangesDataStore.recordChange(oldQuestion, null);
                    AlertMaker.showInformationAlert("Quiz management", "Question successfully deleted.", "");
                } catch (DatabaseException | IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void searchQuestions() {
        String id = Optional.ofNullable(idChoiceBox.getValue()).map(Object::toString).orElse("");
        String questionText = questionTextField.getText();
        String option1 = option1TextField.getText();
        String option2 = option2TextField.getText();
        String option3 = option3TextField.getText();
        String option4 = option4TextField.getText();
        String correctOption = Optional.ofNullable(correctOptionChoiceBox.getValue()).map(Object::toString).orElse("");
        List<MultipleChoiceQuestion> filteredQuestions = questions.stream().filter(question ->
                question.getId().toString().contains(id)
                        && question.getText().contains(questionText)
                        && question.getPossibleAnswers().get(0).contains(option1)
                        && question.getPossibleAnswers().get(1).contains(option2)
                        && question.getPossibleAnswers().get(2).contains(option3)
                        && question.getPossibleAnswers().get(3).contains(option4)
                        && question.getCorrectAnswerPosition().toString().contains(correctOption)
        ).toList();
        quizTableView.setItems(FXCollections.observableList(filteredQuestions));
    }

}
