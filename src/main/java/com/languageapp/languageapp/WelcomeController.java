package com.languageapp.languageapp;


import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WelcomeController {

    @FXML
    private Text welcomeText;

    public void initialize() {
        welcomeText.setText("Hello " + LanguageApp.getCurrentUser().username() + "!");
    }

}
