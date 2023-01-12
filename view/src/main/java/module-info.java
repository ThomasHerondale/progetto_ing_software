module view {
    requires javafx.controls;
    requires javafx.fxml;
    requires model;
    requires database;
    requires mail;
    requires ssn;
    requires time;
    requires control;


    opens view to javafx.fxml;
    exports view;
}