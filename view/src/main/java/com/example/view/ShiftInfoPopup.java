package com.example.view;

import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ShiftInfoPopup {
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
    private final Shift shift;
    public ShiftInfoPopup(Shift currentShift) {
        this.shift = currentShift;
    }
    @FXML
    public void initialize(){
        idLabel.setText(shift.getOwner().getId());
        fullNameLabel.setText(shift.getOwner().getFullName());
        rankLabel.setText("Livello " + shift.getRank());
        hoursLabel.setText(String.valueOf(shift.getStartTime()) + " - " + shift.getEndTime() + "(" + shift.getHours() + "h)");
        dateLabel.setText(String.valueOf(shift.getDate()));
        if (shift.isOvertime()){
            overtimeLabel.setOpacity(1.0);
        }
        if (shift.isSubstitution()){
            overtimeLabel.setOpacity(1.0);
        }
    }


    @FXML
    void clickBack(ActionEvent event) {
        NavigationManager.getInstance().closePopup("Shift Info");
    }

    @FXML
    void clickRecordEntry(ActionEvent event) {

    }

}
