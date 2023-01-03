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
    private final Shift shiftEntity;
    public ShiftInfoPopup(Shift shiftEntity) {
        this.shiftEntity = shiftEntity;
    }
    @FXML
    public void initialize(){
        idLabel.setText(shiftEntity.getOwner().getId());
        fullNameLabel.setText(shiftEntity.getOwner().getFullName());
        rankLabel.setText("Livello " + shiftEntity.getRank());
        if (shiftEntity.getRank() == 'H'){
            rankLabel.setText("Admin");
        }
        hoursLabel.setText(shiftEntity.getStartTime() + " - " + shiftEntity.getEndTime() + " (" + shiftEntity.getHours() + "h)");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateLabel.setText(shiftEntity.getDate().format(formatter));
        if (shiftEntity.isOvertime()){
            overtimeLabel.setOpacity(1.0);
            ordinaryLabel.setOpacity(0.3);
        }
        if (shiftEntity.isSubstitution()){
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
        RecordEntryHandler recordEntryHandler = new RecordEntryHandler();
        recordEntryHandler.clickedRecordEntry(shiftEntity);
    }

}
