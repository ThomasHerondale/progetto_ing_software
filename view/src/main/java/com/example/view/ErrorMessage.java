package com.example.view;

import database.DBMSDaemon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ErrorMessage {

    @FXML
    private Label messageLabel;

    @FXML
    private Button okayButton;
    private String description;

    public ErrorMessage(String message){
        this.description = message;
    }
    @FXML
    public void initialize(){
        messageLabel.setText(description);
    }

    @FXML
    void clickOkay(ActionEvent event) {
        NavigationManager.getInstance().closePopup("Error Message");
    }

}

