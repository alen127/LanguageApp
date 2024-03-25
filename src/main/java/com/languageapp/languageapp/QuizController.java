package com.languageapp.languageapp;

import com.languageapp.database.Database;
import com.languageapp.entities.MultipleChoiceQuestion;
import com.languageapp.entities.Quiz;
import com.languageapp.entities.QuizTotalStats;
import com.languageapp.exceptions.DatabaseException;
import com.languageapp.threads.DisplayHighestScoringUserThread;
import com.languageapp.util.AlertMaker;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private QuizTotalStats userStats = LanguageApp.getCurrentUser().multipleChoiceQuizStats();
    @FXML
    private Text prompt;
    @FXML
    private Button buttonA;
    @FXML
    private Button buttonB;
    @FXML
    private Button buttonC;
    @FXML
    private Button buttonD;
    private List<Button> buttons;
    private Quiz<MultipleChoiceQuestion> quiz;
    private Timeline bestUserDisplay;

    EventHandler<ActionEvent> handleAnswerSelectionEvent = event -> {
        buttons.forEach(button -> button.setDisable(true));

        String userAnswer = ((Button) event.getSource()).getText();
        if (quiz.getCurrentQuestion().isCorrectAnswer(userAnswer)) {
            quiz.incrementScore();
        }
        showCorrectAnswer();
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(250));
        pauseTransition.setOnFinished(actionEvent -> {
            buttons.forEach(button -> button.setStyle(null));
            displayNextPage();
        });
        pauseTransition.play();
        quiz.incrementCurrentQuestionIndex();
    };

    public void initialize() {
        buttons = new ArrayList<>(List.of(buttonA, buttonB, buttonC, buttonD));
        try {
            List<MultipleChoiceQuestion> multipleChoiceQuestions = Database.getQuestions();
            quiz = new Quiz<>(multipleChoiceQuestions);
            userStats.init(); // sets attemps and total percent to zero, first time player
            buttons.forEach(button -> button.setOnAction(handleAnswerSelectionEvent));
            displayNextPage();
        } catch (DatabaseException e) {
            AlertMaker.showErrorAlert("Database error", "Error getting data from database.", "Couldn't read quiz questions.");
            logger.error(e.getMessage(), e);
        }
        bestUserDisplay = new Timeline(new KeyFrame(Duration.seconds(2), event -> Platform.runLater(new Thread(new DisplayHighestScoringUserThread()))));
        bestUserDisplay.setCycleCount(Timeline.INDEFINITE);
        bestUserDisplay.play();
    }

    private void showCorrectAnswer() {
        for (var button : buttons) {
            if (button.getText().equals(quiz.getCurrentQuestion().getCorrectAnswer()))
                button.setStyle("-fx-background-color: lightgreen;" + "-fx-opacity: 1;");
            else
                button.setStyle("-fx-background-color: lightcoral;" + "-fx-opacity: 1");
        }
    }

    public void displayNextPage() {
        buttons.forEach(button -> button.setDisable(false));
        if (quiz.getCurrentQuestionIndex() < quiz.getNumberOfQuestions()) {
            displayQuestion();
        } else {
            displayResultsPage();
        }
    }

    private void displayResultsPage() {
        FXMLLoader loader = new FXMLLoader(LanguageApp.class.getResource("quizResults.fxml"));
        Parent root;
        try {
            root = loader.load();
            QuizResultsController quizResultsController = loader.getController();
            quizResultsController.setCompletedQuiz(quiz);

            Scene scene = new Scene(root, 640, 480);
            LanguageApp.getMainStage().setScene(scene);
            LanguageApp.getMainStage().show();
            bestUserDisplay.stop();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    private void displayQuestion() {
        Collections.shuffle(quiz.getCurrentQuestion().getPossibleAnswers());
        prompt.setText(quiz.getCurrentQuestion().getText());
        buttonA.setText(quiz.getCurrentQuestion().getPossibleAnswers().get(0));
        buttonB.setText(quiz.getCurrentQuestion().getPossibleAnswers().get(1));
        buttonC.setText(quiz.getCurrentQuestion().getPossibleAnswers().get(2));
        buttonD.setText(quiz.getCurrentQuestion().getPossibleAnswers().get(3));
    }
}
