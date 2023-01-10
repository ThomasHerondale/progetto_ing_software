package com.example.view;

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
    public ErrorMessage(boolean isConnectionLost){
        this.description = "Errore di connessione con il database, riprovare o, se il problema persiste," +
                " riavviare il software.";
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

