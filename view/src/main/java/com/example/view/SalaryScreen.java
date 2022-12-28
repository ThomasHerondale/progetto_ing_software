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

    private ViewSalaryHandler viewSalaryHandler;

    public SalaryScreen(Worker worker, ViewSalaryHandler handler){
        super.setWorker(worker);
        this.viewSalaryHandler = handler;
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

    }

}

