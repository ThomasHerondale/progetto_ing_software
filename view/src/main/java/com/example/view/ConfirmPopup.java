package com.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ConfirmPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private Label description;

    @FXML
    private Label title;

    private AccountInfoHandler accountInfoHandler;
    public ConfirmPopup(AccountInfoHandler accountInfoHandler){
        //serve la ConfirmAction enum...
        this.accountInfoHandler = accountInfoHandler;
    }
    @FXML
    public void initialize(){
        //TODO:
    }
    @FXML
    void clickConfirm(ActionEvent event) {
        accountInfoHandler.clickedConfirmLogout();
    }

}
