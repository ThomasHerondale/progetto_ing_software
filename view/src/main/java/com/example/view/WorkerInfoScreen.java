package com.example.view;

import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class WorkerInfoScreen extends LoggedScreen{

    @FXML
    private Label IBANLabel;

    @FXML
    private Label IDLabel;

    @FXML
    private Label autoExitCountLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label birthPlaceLabel;

    @FXML
    private Label delayCountLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Button enableParentalLeaveButton;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label holidayCountLabel;


    @FXML
    private Label parentalLeaveLabel;

    @FXML
    private Label phoneLabel;


    @FXML
    private Button promoteButton;

    @FXML
    private Label rankLabel;

    @FXML
    private Button removeButton;

    @FXML
    private Label sexLabel;

    @FXML
    private Label ssnLabel;

    private WorkersRecapHandler workersRecapHandler;

    public WorkerInfoScreen(Worker worker, WorkersRecapHandler workersRecapHandler){
        this.workersRecapHandler = workersRecapHandler;
    }

    @FXML
    public void initialize(){
        super.initialize();
    }


    @FXML
    public void clickBack(ActionEvent event) {
        //TODO:
    }

    @FXML
    public void clickEnableParentalLeave(ActionEvent event) {

    }

    @FXML
    public void clickProfile(MouseEvent event) {
        AccountInfoHandler accountInfoHandler = new AccountInfoHandler();
        accountInfoHandler.clickedProfile();
    }

    @FXML
    public void clickPromote(ActionEvent event) {

    }

    @FXML
    public void clickRemove(ActionEvent event) {

    }

}

