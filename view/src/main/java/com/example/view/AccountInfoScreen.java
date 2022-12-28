package com.example.view;

import commons.Counters;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AccountInfoScreen extends LoggedScreen{

    @FXML
    private Label IBANLabel;

    @FXML
    private Label IDLabel;

    @FXML
    private Label autoExitCountLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label delayCountLabel;

    @FXML
    private Button editEmailButton;

    @FXML
    private Button editIBANButton;

    @FXML
    private Button editPhoneButton;

    @FXML
    private Label emailLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label holidayCountLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Label parentalLeaveLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Group profileIcon;

    private final AccountInfoHandler accountInfoHandler;
    private Counters workerCounters;

    public AccountInfoScreen(Worker worker, Counters workerCounters, AccountInfoHandler accountInfoHandler) {
        super(worker);
        this.accountInfoHandler = accountInfoHandler;
        this.workerCounters = workerCounters;

    }
    @Override
    public void initialize(){
        super.initialize();
        IDLabel.setText(super.getWorker().getId());
        fullNameLabel.setText(super.getWorker().getFullName().toUpperCase());
        phoneLabel.setText(super.getWorker().getPhone());
        emailLabel.setText(super.getWorker().getEmail());
        IBANLabel.setText(super.getWorker().getIban());
        autoExitCountLabel.setText(String.valueOf(workerCounters.autoExit()));
        delayCountLabel.setText(String.valueOf(workerCounters.delay()));
        holidayCountLabel.setText(String.valueOf(workerCounters.holiday()));
        parentalLeaveLabel.setText(String.valueOf(workerCounters.parentalLeave()));

    }

    @FXML
    void clickBack(ActionEvent event) {
        accountInfoHandler.clickedBack();
    }

    @FXML
    void clickEditEmail(ActionEvent event) {
        //TODO:
    }

    @FXML
    void clickEditIBAN(ActionEvent event) {
        //TODO:
    }

    @FXML
    void clickEditPhone(ActionEvent event) {
        //TODO:
    }

    @FXML
    void clickLogout(ActionEvent event) {
        accountInfoHandler.clickedLogout();
    }

}

