package com.example.view;

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

    private AccountInfoHandler accountInfoHandler;
    //private Counters workerCounters;

    public AccountInfoScreen(Worker worker, int workerCounters, AccountInfoHandler accountInfoHandler) {
        super.setWorker(worker);
        this.accountInfoHandler = accountInfoHandler;
        //this.workerCounters = workerCounters;

    }
    @Override
    public void initialize(){
        super.initialize();
        IDLabel.setText(super.getWorker().getId());
        fullNameLabel.setText(super.getWorker().getFullName().toUpperCase());
        phoneLabel.setText(super.getWorker().getPhone());
        emailLabel.setText(super.getWorker().getEmail());
        IBANLabel.setText(super.getWorker().getIban());
        /*
        autoExitCountLabel.setText(workerCounters.getAutoExitCount());
        delayCountLabel.setText(workerCounters.getDelayCount());
        holidayCountLabel.setText(workerCounters.getHolidayCount());
        parentalLeaveLabel.setText(workerCounters.getParentalLeave());
        */

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

