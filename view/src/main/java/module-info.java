module view {
    requires javafx.controls;
    requires javafx.fxml;
    requires model;
    requires database;
    requires mail;
    requires ssn;
    requires time;
    requires control;


    opens com.example.view to javafx.fxml;
    exports com.example.view;
}