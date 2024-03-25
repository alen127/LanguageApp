module com.languageapp.languageapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;
    requires org.slf4j;

    opens com.languageapp.languageapp to javafx.fxml;
    exports com.languageapp.languageapp;
    exports com.languageapp.entities;

}