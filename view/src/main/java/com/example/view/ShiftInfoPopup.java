package com.example.view;

import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;

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
    private Label ordinaryLabel;

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
        hoursLabel.setText(shift.getStartTime() + " - " + shift.getEndTime() + " (" + shift.getHours() + "h)");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateLabel.setText(shift.getDate().format(formatter));
        if (shift.isOvertime()){
            overtimeLabel.setOpacity(1.0);
            ordinaryLabel.setOpacity(0.3);
        }
        if (shift.isSubstitution()){
            substitutionLabel.setOpacity(1.0);
            ordinaryLabel.setOpacity(0.3);
        }
    }


    @FXML
    void clickBack(ActionEvent event) {
        NavigationManager.getInstance().closePopup("Shift Info");
    }

    @FXML
    void clickRecordEntry(ActionEvent event) {
        //TODO:
    }

}
