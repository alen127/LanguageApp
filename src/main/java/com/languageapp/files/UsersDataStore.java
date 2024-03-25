package com.languageapp.files;

import com.languageapp.entities.QuizTotalStats;
import com.languageapp.entities.User;
import com.languageapp.entities.UserType;
import com.languageapp.exceptions.FileEmptyException;
import com.languageapp.exceptions.UsernameTakenException;
import com.languageapp.languageapp.LanguageApp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class UsersDataStore {
    private static final String USER_DATA_FILE = "dat/users.txt";


    public static List<User> getUsers() throws IOException {
        Path filePath = Path.of(USER_DATA_FILE);
        validateFile(filePath);
        return Files.readAllLines(filePath).stream()
                .map(line -> line.split(";"))
                .map(data ->
                        new User.Builder(Long.parseLong(data[0]), data[1], data[2], UserType.valueOf(data[3]))
                                .multipleChoiceQuizStats(new QuizTotalStats(Integer.parseInt(data[4]), Float.parseFloat(data[5]))).build()).collect(Collectors.toList());
    }

    public static void writeUsers(List<User> users) throws IOException {
        Path filePath = Path.of(USER_DATA_FILE);
        validateFile(filePath);
        StringBuilder fileString = new StringBuilder();
        for (var user : users) {
            String userString = user.id() + ";" + user.username() + ";" + user.password() + ";" + user.type() + ';' + user.multipleChoiceQuizStats().getAttempts() + ";" + user.multipleChoiceQuizStats().getTotalScorePercent() + '\n';
            fileString.append(userString);
        }
        Files.writeString(filePath, fileString.toString());
    }


    public static void addUser(User user) throws IOException {
        if (LanguageApp.getUsers().stream().anyMatch(u -> u.username().equals(user.username()))) {
            throw new UsernameTakenException("Username " + user.username() + " already exists");
        }
        LanguageApp.getUsers().add(user);
        writeUsers(LanguageApp.getUsers());
    }

    private static void validateFile(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            throw new FileNotFoundException("User file not found");
        }
        if (Files.size(filePath) == 0) {
            throw new FileEmptyException("User file is empty");
        }
    }
}

