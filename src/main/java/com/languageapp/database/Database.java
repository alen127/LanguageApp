package com.languageapp.database;

import com.languageapp.entities.DictionaryEntry;
import com.languageapp.entities.Flashcard;
import com.languageapp.entities.MultipleChoiceQuestion;
import com.languageapp.exceptions.DatabaseException;
import com.languageapp.threads.ConnectionThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    public static Connection connectToDatabase() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("dat/database.properties"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password")
        );
    }

    private static Connection getConnection() {
        ConnectionThread connectionThread = new ConnectionThread();
        Thread t = new Thread(connectionThread);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            logger.error("Connection thread was interrupted", e);
        }
        return connectionThread.getConnection();
    }

    public static List<MultipleChoiceQuestion> getQuestions() throws DatabaseException {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM QUIZ");
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String prompt = resultSet.getString("QUESTION");
                String optionA = resultSet.getString("OPTION_1");
                String optionB = resultSet.getString("OPTION_2");
                String optionC = resultSet.getString("OPTION_3");
                String optionD = resultSet.getString("OPTION_4");
                int correctOption = resultSet.getInt("CORRECT_OPTION");
                multipleChoiceQuestions.add(new MultipleChoiceQuestion(id, prompt, new ArrayList<>(List.of(optionA, optionB, optionC, optionD)), correctOption));
            }
            Collections.shuffle(multipleChoiceQuestions);
        } catch (SQLException e) {
            String msg = "An error occurred while getting questions from the database";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        return multipleChoiceQuestions;
    }


    public static void addQuestion(MultipleChoiceQuestion question) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO
                    QUIZ (QUESTION, OPTION_1, OPTION_2, OPTION_3, OPTION_4, CORRECT_OPTION)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """);
            statement.setString(1, question.getText());
            statement.setString(2, question.getPossibleAnswers().get(0));
            statement.setString(3, question.getPossibleAnswers().get(1));
            statement.setString(4, question.getPossibleAnswers().get(2));
            statement.setString(5, question.getPossibleAnswers().get(3));
            statement.setInt(6, question.getCorrectAnswerPosition());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void editQuestion(MultipleChoiceQuestion question) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE QUIZ
                    SET QUESTION       = ?,
                        OPTION_1       = ?,
                        OPTION_2       = ?,
                        OPTION_3       = ?,
                        OPTION_4       = ?,
                        CORRECT_OPTION = ?
                    WHERE ID = ?;
                    """);
            statement.setString(1, question.getText());
            statement.setString(2, question.getPossibleAnswers().get(0));
            statement.setString(3, question.getPossibleAnswers().get(1));
            statement.setString(4, question.getPossibleAnswers().get(2));
            statement.setString(5, question.getPossibleAnswers().get(3));
            statement.setInt(6, question.getCorrectAnswerPosition());
            statement.setInt(7, question.getId().intValue());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void deleteQuestion(MultipleChoiceQuestion question) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE
                    FROM QUIZ
                    WHERE ID = ?;
                    """);
            statement.setInt(1, question.getId().intValue());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static List<Flashcard> getFlashcards() throws DatabaseException {
        List<Flashcard> flashcards = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM FLASHCARDS");
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String frontText = resultSet.getString("FRONT_TEXT");
                String backText = resultSet.getString("BACK_TEXT");
                flashcards.add(new Flashcard(id, frontText, backText));
            }
            Collections.shuffle(flashcards);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
        return flashcards;
    }

    public static List<DictionaryEntry> getDictionaryEntries() throws DatabaseException {
        List<DictionaryEntry> dictionaryEntries = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DICTIONARY");
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String englishWord = resultSet.getString("ENGLISH_WORD");
                String croatianWord = resultSet.getString("CROATIAN_WORD");
                dictionaryEntries.add(new DictionaryEntry(id, englishWord, croatianWord));
            }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
        return dictionaryEntries;
    }

    public static void addDictionaryEntry(DictionaryEntry dictionaryEntry) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO
                    DICTIONARY(ENGLISH_WORD, CROATIAN_WORD)
                    VALUES (?, ?)
                    """);
            statement.setString(1, dictionaryEntry.getText());
            statement.setString(2, dictionaryEntry.getCorrectAnswer());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void editDictionaryEntry(DictionaryEntry dictionaryEntry) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE DICTIONARY
                    SET ENGLISH_WORD  = ?,
                        CROATIAN_WORD = ?
                    WHERE ID = ?;
                    """);
            statement.setString(1, dictionaryEntry.getText());
            statement.setString(2, dictionaryEntry.getCorrectAnswer());
            statement.setInt(3, dictionaryEntry.getId().intValue());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void deleteDictionaryEntry(DictionaryEntry dictionaryEntry) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE
                    FROM DICTIONARY
                    WHERE ID = ?;
                    """);
            statement.setInt(1, dictionaryEntry.getId().intValue());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void addFlashcard(Flashcard flashcard) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO FLASHCARDS (FRONT_TEXT, BACK_TEXT) VALUES (?, ?)");
            statement.setString(1, flashcard.getText());
            statement.setString(2, flashcard.getCorrectAnswer());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void editFlashcard(Flashcard flashcard) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE FLASHCARDS
                    SET FRONT_TEXT  = ?,
                        BACK_TEXT = ?
                    WHERE ID = ?;
                    """);
            statement.setString(1, flashcard.getText());
            statement.setString(2, flashcard.getCorrectAnswer());
            statement.setInt(3, flashcard.getId().intValue());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static void deleteFlashcard(Flashcard flashcard) throws DatabaseException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE
                    FROM FLASHCARDS
                    WHERE ID = ?;
                    """);
            statement.setInt(1, flashcard.getId().intValue());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException(e.getMessage(), e);
        }
    }
}
