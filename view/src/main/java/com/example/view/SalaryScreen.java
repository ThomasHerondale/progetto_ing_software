package com.example.view;

import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class SalaryScreen extends LoggedScreen {

    @FXML
    private Button backButton;

    @FXML
    private Group profileIcon;

    private final ViewSalaryHandler viewSalaryHandler;
    private final AccountInfoHandler accountInfoHandler;

    public SalaryScreen(Worker worker, ViewSalaryHandler handler){
        //mancano ancora delle cose
        //TODO:
        super(worker);
        this.viewSalaryHandler = handler;
        accountInfoHandler = new AccountInfoHandler(worker);
    }
    @Override
    public void initialize(){
        super.initialize();
        //TODO:
    }
    @FXML
    void clickBack(ActionEvent event) {
        viewSalaryHandler.clickedBack();
    }

    @FXML
    void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }

}

