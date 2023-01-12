package view.workers;

import controller.workers.RecordEntryHandler;
import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RemoteRecordEntryPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private ComboBox<String> entryTimeBox;

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
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        shiftDate.setText(shiftEntity.getDate().format(formatterDate));
        LocalTime startTime = shiftEntity.getStartTime();
        entryTimeBox.getItems().add(0, String.valueOf(startTime));
        for (int i = 1; i < 7; i++){
            startTime = startTime.plusMinutes(5);
            entryTimeBox.getItems().add(i, String.valueOf(startTime));
        }
    }

    @FXML
    public void clickConfirm(ActionEvent event) {
        if (entryTimeBox.getValue() != null){
            recordEntryHandler.clickedConfirm(LocalTime.parse(entryTimeBox.getValue()));
        }
    }

}
