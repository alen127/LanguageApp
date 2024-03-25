package com.languageapp.languageapp;

import com.languageapp.database.Database;
import com.languageapp.entities.Flashcard;
import com.languageapp.entities.FlashcardGame;
import com.languageapp.entities.MultipleChoiceQuestion;
import com.languageapp.exceptions.DatabaseException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FlashcardsController {
    private static final Logger logger = LoggerFactory.getLogger(FlashcardsController.class);
    private FlashcardGame flashcardGame;
    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane frontPane;
    @FXML
    private BorderPane backPane;
    @FXML
    private Text mainTextFront;
    @FXML
    private Text mainTextBack;
    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button backToWelcomePageButton;
    @FXML
    private Text counterText;
    @FXML
    private Text typeTextFront;
    @FXML
    private Text typeTextBack;
    EventHandler<MouseEvent> handleCardFlipEvent = event -> {
        if (stackPane.getChildren().get(1).getId().equals(backPane.getId())) {
            showFront();
            frontPane.toFront();
        } else {
            showBack();
            backPane.toFront();
        }
    };


    public void initialize() {
        try {
            List<Flashcard> flashcardList = Database.getFlashcards();
            List<MultipleChoiceQuestion> quizQuestions = Database.getQuestions();
            List<Flashcard> quizFlashcards = quizQuestions.stream().map(multipleChoiceQuestion -> new Flashcard(multipleChoiceQuestion.getText(), multipleChoiceQuestion.getCorrectAnswer())).toList();
            flashcardList.addAll(quizFlashcards);
            flashcardGame = new FlashcardGame(flashcardList);
            Collections.shuffle(flashcardGame.getVirtualFlashcards());
            backPane = (BorderPane) stackPane.getChildren().get(0);
            frontPane = (BorderPane) stackPane.getChildren().get(1);
            stackPane.setOnMouseClicked(handleCardFlipEvent);
            typeTextBack.setVisible(false);
            mainTextBack.setVisible(false);
            fillPanes();
        } catch (DatabaseException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void showFront() {
        mainTextFront.setVisible(true);
        typeTextFront.setVisible(true);
        mainTextBack.setVisible(false);
        typeTextBack.setVisible(false);
    }

    private void showBack() {
        mainTextFront.setVisible(false);
        typeTextFront.setVisible(false);
        mainTextBack.setVisible(true);
        typeTextBack.setVisible(true);
    }

    public void nextFlashcard() {
        if (flashcardGame.nextFlashcardExists()) {
            flashcardGame.incrementCurrentQuestionIndex();
            fillPanes();
            showFront();
            frontPane.toFront();
        }
    }

    public void previousFlashcard() {
        if (flashcardGame.previousFlashcardExists()) {
            flashcardGame.decrementCurrentQuestionIndex();
            fillPanes();
            showFront();
            frontPane.toFront();
        }
    }

    private void fillPanes() {
        counterText.setText(flashcardGame.getCurrentQuestionIndex() + 1 + "/" + flashcardGame.getNumberOfVirtualFlashcards());
        mainTextFront.setText(flashcardGame.getCurrentQuestion().getText());
        mainTextBack.setText(flashcardGame.getCurrentQuestion().getCorrectAnswer());
    }

    public void backToWelcomePage() {
        try {
            LanguageApp.changeScene("welcome.fxml", "Welcome", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
