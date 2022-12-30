package com.example.view;

import database.DBMSDaemon;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class SalaryScreen extends LoggedScreen {

    @FXML
    private Button backButton;

    private final ViewSalaryHandler viewSalaryHandler;
    private final AccountInfoHandler accountInfoHandler;

    public SalaryScreen(ViewSalaryHandler handler){
        //mancano ancora delle cose
        //TODO:
        this.viewSalaryHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
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

