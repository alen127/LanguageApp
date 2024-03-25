package com.languageapp.files;

import com.languageapp.entities.Change;
import com.languageapp.languageapp.LanguageApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChangesDataStore {
    private static final String CHANGES_FILE = "dat/changes.ser";
    private static final Logger logger = LoggerFactory.getLogger(ChangesDataStore.class);

    public static ArrayList<Change<?>> getChanges() throws IOException {
        ArrayList<Change<?>> changes = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(CHANGES_FILE)) {
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            changes = (ArrayList<Change<?>>) objectIn.readObject();
            objectIn.close();
        } catch (FileNotFoundException e) {
            logger.info("Changes file not found.", e);
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return changes;
    }

    private static void writeChanges(ArrayList<Change<?>> changes) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(CHANGES_FILE);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(changes);
        fileOut.close();
        objectOut.close();
    }

    public static <T extends Serializable> void recordChange(T oldValue, T newValue) throws IOException {
        ArrayList<Change<?>> changes = getChanges();
        changes.add(new Change<>(oldValue, newValue, LanguageApp.getCurrentUser(), LocalDateTime.now()));
        writeChanges(changes);
    }
}
