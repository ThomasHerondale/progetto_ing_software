module ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires model;
    requires database;
    requires mail;
    requires ssn;
    requires time;


    exports view.presences;
    opens view.presences to javafx.fxml;
    exports view.workers;
    opens view.workers to javafx.fxml;
    exports view.navigation;
    opens view.navigation to javafx.fxml;
    exports controller.presences;
    opens controller.presences to javafx.fxml;
    exports controller.workers;
    opens controller.workers to javafx.fxml;
}