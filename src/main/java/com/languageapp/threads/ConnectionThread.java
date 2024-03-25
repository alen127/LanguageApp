package com.languageapp.threads;

import com.languageapp.database.Database;
import com.languageapp.languageapp.LanguageApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionThread.class);

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void run() {
        synchronized (LanguageApp.connectionLock) {
            try {
                connection = Database.connectToDatabase();
            } catch (IOException | SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
