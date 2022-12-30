package com.example.view;

import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ShiftInfoPopup {
    private Shift shift;
    public ShiftInfoPopup(Shift currentShift) {
        this.shift = currentShift;
    }

    @FXML
    private Button backButton;

    @FXML
    private Label dateLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label hoursLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label overtimeLabel;

    @FXML
    private Label rankLabel;

    @FXML
    private Button recordEntryButton;

    @FXML
    private Label substitutionLabel;

    @FXML
    void clickBack(ActionEvent event) {
        NavigationManager.getInstance().closePopup("Shift Info");
    }

    @FXML
    void clickRecordEntry(ActionEvent event) {

    }

}
