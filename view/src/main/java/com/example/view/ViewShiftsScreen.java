package com.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class ViewShiftsScreen extends LoggedScreen {

    @FXML
    private ScrollBar horizontalBar;

    @FXML
    private ScrollPane horizontalPane;

    @FXML
    private AnchorPane shiftsPane;

    @FXML
    private ScrollPane verticalAndHorizonatalPane;

    @FXML
    private ScrollBar verticalBar;

    @FXML
    private ScrollPane verticalPane;

    private ShiftHandler shiftHandler;
    private AccountInfoHandler accountInfoHandler;

    public ViewShiftsScreen(ShiftHandler handler){
        this.shiftHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
    }

    @FXML
    public void initialize(){
        super.initialize();
        synchroBar();
    }

    private void synchroBar() {
        verticalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.vvalueProperty());
        verticalBar.valueProperty().bindBidirectional(verticalPane.vvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.hvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(horizontalPane.hvalueProperty());
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }
    @FXML
    public void clickBack(ActionEvent event){
        shiftHandler.clickedBack();
    }

}

