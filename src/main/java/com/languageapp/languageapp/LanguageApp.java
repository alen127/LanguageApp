package com.languageapp.languageapp;

import com.languageapp.entities.User;
import com.languageapp.files.UsersDataStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class LanguageApp extends Application {
    public static final Object connectionLock = new Object();
    private static final Logger logger = LoggerFactory.getLogger(LanguageApp.class);
    private static User currentUser;
    private static List<User> users;
    private static Stage mainStage;

    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> users) {
        LanguageApp.users = users;
    }

    public static void changeScene(String fxmlFile, String title, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LanguageApp.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        mainStage.setTitle(title);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(Stage mainStage) {
        LanguageApp.mainStage = mainStage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        LanguageApp.currentUser = currentUser;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(LanguageApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            try {
                UsersDataStore.writeUsers(users);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}