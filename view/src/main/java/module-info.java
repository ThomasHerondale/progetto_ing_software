module view {
    requires javafx.controls;
    requires javafx.fxml;
    requires model;
    requires database;
    requires mail;


    opens com.example.view to javafx.fxml;
    exports com.example.view;
}