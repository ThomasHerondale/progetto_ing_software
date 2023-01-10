package com.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SuccessPopup {

    @FXML
    private Button okayButton;

    private final AccountInfoHandler accountInfoHandler;

    public SuccessPopup(AccountInfoHandler accountInfoHandler) {
        this.accountInfoHandler = accountInfoHandler;
    }

    public void setClose(Stage stage) {
        stage.setOnCloseRequest(event -> accountInfoHandler.clickedOkay());
    }

    @FXML
    void clickOkay(ActionEvent event) {
        accountInfoHandler.clickedOkay();
    }
}
