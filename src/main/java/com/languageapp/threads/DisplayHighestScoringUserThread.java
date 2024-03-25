package com.languageapp.threads;

import com.languageapp.entities.User;
import com.languageapp.exceptions.MultipleHighestScoringUsersException;
import com.languageapp.languageapp.LanguageApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class DisplayHighestScoringUserThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DisplayHighestScoringUserThread.class);


    @Override
    public void run() {
        DecimalFormat format = new DecimalFormat("##0");
        float bestScorePercent = Float.MIN_VALUE;
        User bestUser = LanguageApp.getCurrentUser();
        for (var user : LanguageApp.getUsers()) {
            if (user.multipleChoiceQuizStats().getAverageScorePercent() > bestScorePercent) {
                bestUser = user;
                bestScorePercent = user.multipleChoiceQuizStats().getAverageScorePercent();
            } else if (user.multipleChoiceQuizStats().getAverageScorePercent() == bestScorePercent) {
                MultipleHighestScoringUsersException multipleHighestScoringUsersException = new MultipleHighestScoringUsersException("Multiple users exist with " + bestScorePercent + "% average score.");
                logger.info(multipleHighestScoringUsersException.getMessage(), multipleHighestScoringUsersException);
                throw multipleHighestScoringUsersException;
            }
        }
        LanguageApp.getMainStage().setTitle("Highest scoring user: " + bestUser.username() + " - " + format.format(bestUser.multipleChoiceQuizStats().getAverageScorePercent()) + "%");
    }
}
