package com.languageapp.languageapp;

import com.languageapp.entities.User;
import com.languageapp.exceptions.FileEmptyException;
import com.languageapp.files.UsersDataStore;
import com.languageapp.util.AlertMaker;
import com.languageapp.util.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    private List<User> users;

    public void initialize() {
        try {
            users = UsersDataStore.getUsers();
            LanguageApp.setUsers(users);
        } catch (FileNotFoundException e) {
            AlertMaker.showErrorAlert("File error", "User file not found.", "A user file is required to run this application.");
            logger.error(e.getMessage(), e);
            System.exit(-1);
        } catch (FileEmptyException e) {
            AlertMaker.showErrorAlert("File error", "User file is in the wrong format or empty.", "The format should be the following: 'username:password:role'.");
            logger.error(e.getMessage(), e);
            System.exit(-1);
        } catch (IOException e) {
            AlertMaker.showErrorAlert("File error", "Couldn't access user file.", "The file might be in use by another program.");
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    public void login() {
        String username = usernameTextField.getText();
        String password = PasswordHasher.hashPassword(passwordField.getText());
        boolean loginSuccess = false;
        for (var user : users) {
            if (username.equals(user.username()) && password.equals(user.password())) {
                loginSuccess = true;
                Optional<ButtonType> result = AlertMaker.showInformationAlert("Login attempt", "Successful login.", "Click OK to continue.");
                if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                    LanguageApp.setCurrentUser(user);
                    try {
                        LanguageApp.changeScene("welcome.fxml", "Welcome", 640, 480);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        if (!loginSuccess) {
            AlertMaker.showErrorAlert("Login attempt", "Login failed.", "Incorrect username or password.");
        }
    }
}
