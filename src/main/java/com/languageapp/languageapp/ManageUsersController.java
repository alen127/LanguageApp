package com.languageapp.languageapp;

import com.languageapp.entities.QuizTotalStats;
import com.languageapp.entities.User;
import com.languageapp.entities.UserType;
import com.languageapp.exceptions.UsernameTakenException;
import com.languageapp.files.ChangesDataStore;
import com.languageapp.files.UsersDataStore;
import com.languageapp.util.AlertMaker;
import com.languageapp.util.ManageUtils;
import com.languageapp.util.PasswordHasher;
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

public class ManageUsersController {
    private static final Logger logger = LoggerFactory.getLogger(ManageQuizController.class);
    @FXML
    private ChoiceBox<Long> idChoiceBox;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ChoiceBox<UserType> userTypeChoiceBox;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, String> idTableColumn;
    @FXML
    private TableColumn<User, String> usernameTableColumn;
    @FXML
    private TableColumn<User, String> passwordTableColumn;
    @FXML
    private TableColumn<User, String> typeTableColumn;

    private List<User> users = LanguageApp.getUsers();

    public void initialize() {
        idTableColumn.setCellValueFactory(user -> new SimpleStringProperty(user.getValue().id().toString()));
        usernameTableColumn.setCellValueFactory(user -> new SimpleStringProperty(user.getValue().username()));
        passwordTableColumn.setCellValueFactory(user -> new SimpleStringProperty(user.getValue().password()));
        typeTableColumn.setCellValueFactory(user -> new SimpleStringProperty(user.getValue().type().toString()));
        userTableView.setItems(FXCollections.observableArrayList(users.stream().sorted(Comparator.comparing(User::id)).toList()));

        idChoiceBox.setItems(FXCollections.observableArrayList(users.stream().map(User::id).sorted(Long::compareTo).toList()));
        userTypeChoiceBox.setItems(FXCollections.observableArrayList(UserType.REGULAR, UserType.ADMIN));
    }


    public void addUser() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(usernameField, passwordField, userTypeChoiceBox), List.of("Username", "Password", "Role"));
        if (msg.isBlank()) {
            Long newID = users.stream().map(User::id).max(Long::compareTo).orElse(0L) + 1;
            String username = usernameField.getText();
            User newUser = new User(newID, username, PasswordHasher.hashPassword(passwordField.getText()), userTypeChoiceBox.getValue(), new QuizTotalStats(-1, -1));
            users.add(newUser);
            try {
                UsersDataStore.writeUsers(users);
                ChangesDataStore.recordChange(null, newUser);
                userTableView.setItems(FXCollections.observableArrayList(users.stream().sorted(Comparator.comparing(User::id)).toList()));
                idChoiceBox.setItems(FXCollections.observableArrayList(users.stream().map(User::id).sorted(Long::compareTo).toList()));
                AlertMaker.showInformationAlert("User management", "User successfully added.", "Added user " + username + ".");
            } catch (IOException e) {
                AlertMaker.showErrorAlert("User management", "Error writing to user file.", "User file might not exist.");
                logger.error(e.getMessage(), e);
            }
        } else
            AlertMaker.showErrorAlert("User management", "Please fill out the required fields.", msg);
    }

    public void editUser() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (msg.isBlank()) {
            if (usernameField.getText().isBlank() && passwordField.getText().isBlank() && userTypeChoiceBox.getValue() == null)
                AlertMaker.showErrorAlert("User management", "Please fill out at least one other field.", "No changes were made.");
            else {
                Optional<ButtonType> confirmButtonResult = AlertMaker.showConfirmationAlert("User management", "Are you sure?", "Click OK to proceed.");
                if (confirmButtonResult.isPresent() && confirmButtonResult.get() == ButtonType.OK) {
                    Long id = idChoiceBox.getValue();
                    User oldUser = users.stream().filter(user -> user.id().equals(id)).findAny().orElse(null);
                    String username = usernameField.getText();
                    String password = PasswordHasher.hashPassword(passwordField.getText());
                    UserType type = userTypeChoiceBox.getValue();


                    if (username.isBlank()) {
                        username = oldUser.username();
                    }
                    if (password.isBlank()) {
                        password = oldUser.password();
                    }
                    if (type == null) {
                        type = oldUser.type();
                    }
                    users.remove(oldUser);
                    User newUser = new User(id, username, password, type, oldUser.multipleChoiceQuizStats());
                    try {
                        UsersDataStore.addUser(newUser);
                        ChangesDataStore.recordChange(oldUser, newUser);
                        userTableView.setItems(FXCollections.observableArrayList(users.stream().sorted(Comparator.comparing(User::id)).toList()));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } catch (UsernameTakenException e) {
                        logger.error(e.getMessage(), e);
                        AlertMaker.showErrorAlert("User management", "Username is already taken.", "Try a different one.");
                    }
                }
            }
        } else
            AlertMaker.showErrorAlert("User management", "Please fill out the required fields.", msg);
    }

    public void deleteUser() {
        String msg = ManageUtils.getRequiredFieldsMessage(List.of(idChoiceBox), List.of("ID"));
        if (!msg.isBlank())
            AlertMaker.showErrorAlert("User management", "Please fill out the required fields.", msg);
        else {
            Optional<ButtonType> confirmButtonResult = AlertMaker.showConfirmationAlert("User management", "Are you sure?", "Click OK to proceed.");
            if (confirmButtonResult.isPresent() && confirmButtonResult.get() == ButtonType.OK) {
                Long id = idChoiceBox.getValue();
                User userToBeDeleted = users.stream().filter(user -> user.id().equals(id)).findAny().orElse(null);
                users.remove(userToBeDeleted);
                try {
                    UsersDataStore.writeUsers(users);
                    ChangesDataStore.recordChange(userToBeDeleted, null);
                    userTableView.setItems(FXCollections.observableArrayList(users.stream().sorted(Comparator.comparing(User::id)).toList()));
                    idChoiceBox.setItems(FXCollections.observableArrayList(users.stream().map(User::id).sorted(Long::compareTo).toList()));
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void searchUsers() {
        String id = Optional.ofNullable(idChoiceBox.getValue()).map(Object::toString).orElse("");
        String username = usernameField.getText();
        String role = Optional.ofNullable(userTypeChoiceBox.getValue()).map(Object::toString).orElse("");
        var filteredUsers = users.stream().filter(user ->
                user.id().toString().contains(id)
                        && user.username().contains(username)
                        && user.type().toString().contains(role)).toList();
        userTableView.setItems(FXCollections.observableArrayList(filteredUsers));
    }
}
