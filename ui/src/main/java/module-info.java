module ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires model;
    requires database;
    requires mail;
    requires ssn;
    requires time;


    opens view to javafx.fxml;
    exports view;
    exports controller;
    opens controller to javafx.fxml;
}