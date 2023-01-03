package com.example.view;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

public class HomeScreen extends LoggedScreen{

    @FXML
    private Group presencesCard;

    @FXML
    private Group salaryCard;

    @FXML
    private Group shiftsCard;

    @FXML
    private Group shiftsRecapCard;

    @FXML
    private Group workersCard;

    private final AccountInfoHandler accountInfoHandler;
    private final ViewSalaryHandler viewSalaryHandler;
    private final ShiftHandler shiftHandler;
    private final ShowPresencesHandler showPresencesHandler;
    private final WorkersRecapHandler workersRecapHandler;

    public HomeScreen(){
        accountInfoHandler = new AccountInfoHandler();
        viewSalaryHandler = new ViewSalaryHandler();
        shiftHandler = new ShiftHandler();
        showPresencesHandler = new ShowPresencesHandler();
        workersRecapHandler = new WorkersRecapHandler();
    }
    @Override
    public void initialize(){
        super.initialize();
    }

    @FXML
    public void clickPresences(MouseEvent event) {
        showPresencesHandler.clickedPresences();
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }

    @FXML
    public void clickShiftsRecap(MouseEvent event) {
        //TODO:
    }

    @FXML
    public void clickShowShifts(MouseEvent event) {
        shiftHandler.clickedShowShifts();
    }

    @FXML
    public void clickViewSalary(MouseEvent event) {
        viewSalaryHandler.clickedViewSalary();
    }

    @FXML
    public void clickWorkers(MouseEvent event) {
        workersRecapHandler.clickedWorkers();
    }

}

