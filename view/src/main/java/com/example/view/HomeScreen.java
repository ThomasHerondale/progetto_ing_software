package com.example.view;

import entities.Worker;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class HomeScreen extends LoggedScreen{

    @FXML
    private Group presencesCard;

    @FXML
    private Group profileIcon;

    @FXML
    private Group salaryCard;

    @FXML
    private Group shiftsCard;

    @FXML
    private Group shiftsRecapCard;

    @FXML
    private Group workersCard;

    private AccountInfoHandler accountInfoHandler = new AccountInfoHandler();

    public HomeScreen(Worker worker){
        super.setWorker(worker);
    }
    @Override
    public void initialize(){
        super.initialize();
    }

    @FXML
    public void clickPresences(MouseEvent event) {
        //TODO:
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile(super.getWorker());
    }

    @FXML
    public void clickShiftsRecap(MouseEvent event) {
        //TODO:
    }

    @FXML
    public void clickShowShifts(MouseEvent event) {
        //TODO:
    }

    @FXML
    public void clickViewSalary(MouseEvent event) {
        //TODO:
    }

    @FXML
    public void clickWorkers(MouseEvent event) {
        //TODO:
    }

}

