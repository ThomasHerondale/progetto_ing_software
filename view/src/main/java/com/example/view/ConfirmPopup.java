package com.example.view;

import commons.ConfirmAction;
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

    private final AccountInfoHandler accountInfoHandler;
    private final ConfirmAction confirmAction;
    public ConfirmPopup(ConfirmAction confirmAction, AccountInfoHandler accountInfoHandler){
        this.confirmAction = confirmAction;
        this.accountInfoHandler = accountInfoHandler;
    }
    @FXML
    public void initialize(){
        title.setText(confirmAction.getTitleString());
        if (confirmAction == ConfirmAction.PRESENCE){
            //TODO:
        } else if (confirmAction == ConfirmAction.ENABLE_PARENTAL_LEAVE) {
            //TODO:
        } else {
            description.setText(confirmAction.getDescriptionString());
        }

    }
    @FXML
    void clickConfirm(ActionEvent event) {
        accountInfoHandler.clickedConfirmLogout();
    }

}
