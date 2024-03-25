package com.languageapp.languageapp;

import com.languageapp.entities.UserType;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MenuBarController {
    private static final Logger logger = LoggerFactory.getLogger(MenuBarController.class);

    @FXML
    private Menu manageMenu;


    public void initialize() {
        if (LanguageApp.getCurrentUser().type() == UserType.REGULAR) {
            manageMenu.setDisable(true);
        }
    }

    public void playQuiz() {
        try {
            LanguageApp.changeScene("quiz.fxml", "Quiz", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void playFlashcards() {
        try {
            LanguageApp.changeScene("flashcards.fxml", "Flashcards", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void viewDictionary() {
        try {
            LanguageApp.changeScene("dictionary.fxml", "Dictionary", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void viewWelcomeScreen() {
        try {
            LanguageApp.changeScene("welcome.fxml", "Welcome", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void manageUsers() {
        try {
            LanguageApp.changeScene("manageUsers.fxml", "Users", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void manageDictionary() {
        try {
            LanguageApp.changeScene("manageDictionary.fxml", "Dictionary", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void manageQuiz() {
        try {
            LanguageApp.changeScene("manageQuiz.fxml", "Quiz", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void manageFlashcards() {
        try {
            LanguageApp.changeScene("manageFlashcards.fxml", "Flashcards", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }
    }

    public void viewChangelog() {
        try {
            LanguageApp.changeScene("changelog.fxml", "Changelog", 640, 480);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
