package com.example.view;

import commons.HoursRecap;
import commons.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class SalaryScreen extends LoggedScreen {

    @FXML
    private Label IDLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label baseSalary;

    @FXML
    private Label nameLabel;

    @FXML
    private Label overtimeSalary;

    @FXML
    private Label parentalLeaveSalary;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label totSalary;

    private final ViewSalaryHandler viewSalaryHandler;
    private final AccountInfoHandler accountInfoHandler;
    private Map<HoursRecap, Double> salaryData;

    public SalaryScreen(Map<HoursRecap, Double> salaryData, ViewSalaryHandler handler){
        //mancano ancora delle cose
        //TODO:
        this.viewSalaryHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
        this.salaryData = salaryData;
    }
    @Override
    public void initialize(){
        super.initialize();
        IDLabel.setText(Session.getInstance().getWorker().getId());
        nameLabel.setText(Session.getInstance().getWorker().getName());
        surnameLabel.setText(Session.getInstance().getWorker().getSurname());
        baseSalary.setText(String.valueOf(salaryData.entrySet().iterator().next().getKey().ordinaryHours()));
        overtimeSalary.setText(String.valueOf(salaryData.entrySet().iterator().next().getKey().overtimeHours()));
        parentalLeaveSalary.setText(String.valueOf(salaryData.entrySet().iterator().next().getKey().parentalLeaveHours()));
        totSalary.setText(totSalary.getText() + " " + salaryData.entrySet().iterator().next().getValue());
    }
    @FXML
    public void clickBack(ActionEvent event) {
        viewSalaryHandler.clickedBack();
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }

}

