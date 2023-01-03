package com.example.view;

import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class RemoteRecordEntryPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private ComboBox<?> entryTimeBox;

    @FXML
    private Label shiftDate;

    private Shift shiftEntity;
    private RecordEntryHandler recordEntryHandler;

    public RemoteRecordEntryPopup(Shift shiftEntity, RecordEntryHandler recordEntryHandler){
        this.shiftEntity = shiftEntity;
        this.recordEntryHandler = recordEntryHandler;
    }
    @FXML
    public void initialize(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        shiftDate.setText(shiftEntity.getDate().format(formatter));
    }

    @FXML
    void clickConfirm(ActionEvent event) {
        //recordEntryHandler.clickedConfirm();
    }

}
