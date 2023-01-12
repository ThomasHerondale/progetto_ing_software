package view.presences;

import controller.presences.RecordPresenceHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DelayNoticePopup {

    @FXML
    private Label date_hourLabel;

    @FXML
    private Button okayButton;

    private LocalTime entranceTime;
    private LocalDate entranceDate;
    private RecordPresenceHandler recordPresenceHandler;

    public DelayNoticePopup(LocalDate entranceDate, LocalTime entranceTime,
                            RecordPresenceHandler recordPresenceHandler) {
        this.entranceTime = entranceTime;
        this.entranceDate = entranceDate;
        this.recordPresenceHandler = recordPresenceHandler;
    }
    @FXML
    public void initialize(){
        date_hourLabel.setText("(" + entranceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ", " +
                entranceTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ")");
    }

    @FXML
    void clickOkay(ActionEvent event) {
        recordPresenceHandler.clickedOkay();
    }

}
