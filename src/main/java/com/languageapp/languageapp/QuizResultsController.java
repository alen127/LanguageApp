package com.languageapp.languageapp;

import com.languageapp.entities.MultipleChoiceQuestion;
import com.languageapp.entities.Quiz;
import com.languageapp.entities.QuizTotalStats;
import com.languageapp.threads.DisplayHighestScoringUserThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;


public class QuizResultsController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    @FXML
    private Text absoluteScoreText;
    @FXML
    private Text percentageScoreText;
    @FXML
    private Text comparedToAverageScoreText;
    private Quiz<MultipleChoiceQuestion> completedQuiz;

    private void displayResults() {
        DecimalFormat decimalFormat = new DecimalFormat("##0");
        absoluteScoreText.setText(completedQuiz.getScore() + "/" + completedQuiz.getNumberOfQuestions());
        percentageScoreText.setText(String.valueOf(decimalFormat.format(completedQuiz.getScorePercent())) + '%');

        QuizTotalStats userStats = LanguageApp.getCurrentUser().multipleChoiceQuizStats();
        if (userStats.getAttempts() == 0) {
            comparedToAverageScoreText.setVisible(false);
        } else {
            if (completedQuiz.getScorePercent() > userStats.getAverageScorePercent())
                comparedToAverageScoreText.setText("Well done! Better than your average of " + decimalFormat.format(userStats.getAverageScorePercent()) + "%");
            else if (completedQuiz.getScorePercent() == userStats.getAverageScorePercent()) {
                comparedToAverageScoreText.setText("Consistency is key! Equal to your average of " + decimalFormat.format(userStats.getAverageScorePercent()) + "%");
            } else {
                comparedToAverageScoreText.setText("Unlucky! Worse than your average of " + decimalFormat.format(userStats.getAverageScorePercent()) + "%");
            }
        }
        userStats.incrementAttempts();
        userStats.addToTotalScorePercent(completedQuiz.getScorePercent());
        Platform.runLater(new Thread(new DisplayHighestScoringUserThread()));
    }

    public void setCompletedQuiz(Quiz<MultipleChoiceQuestion> quiz) {
        this.completedQuiz = quiz;
        displayResults();
    }

    public void playAgain() {
        try {
            LanguageApp.changeScene("quiz.fxml", "Quiz", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
