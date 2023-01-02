package com.example.view;

import commons.ConfirmAction;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConfirmPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private Label description;

    @FXML
    private Label title;

    private AccountInfoHandler accountInfoHandler;
    private InsertPresenceHandler insertPresenceHandler;
    private final ConfirmAction confirmAction;
    private Worker presenceWorker;
    LocalDate date;
    public ConfirmPopup(ConfirmAction confirmAction, AccountInfoHandler accountInfoHandler){
        this.confirmAction = confirmAction;
        this.accountInfoHandler = accountInfoHandler;
    }
    public ConfirmPopup(ConfirmAction confirmAction, Worker worker, LocalDate date, InsertPresenceHandler insertPresenceHandler){
        this.confirmAction = confirmAction;
        this.insertPresenceHandler = insertPresenceHandler;
        this.presenceWorker = worker;
        this.date = date;
    }
    @FXML
    public void initialize(){
        title.setText(confirmAction.getTitleString());
        if (confirmAction == ConfirmAction.PRESENCE){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            description.setText(confirmAction.getDescriptionString().replace
                    ("$worker", presenceWorker.getFullName()).replace
                    ("$date", date.format(formatter)));
        } else if (confirmAction == ConfirmAction.ENABLE_PARENTAL_LEAVE) {
            //TODO:
        } else {
            description.setText(confirmAction.getDescriptionString());
        }

    }
    @FXML
    void clickConfirm(ActionEvent event) {
        if (confirmAction == ConfirmAction.LOGOUT){
            accountInfoHandler.clickedConfirmLogout();
        }
        if (confirmAction == ConfirmAction.PRESENCE){
            insertPresenceHandler.clickedConfirm();
        }
    }

}
