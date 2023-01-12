package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExitRecordedPopup {

    @FXML
    private Label date_hourLabel;

    @FXML
    private Button okayButton;

    private LocalTime exitTime;
    private LocalDate exitDate;
    private RecordPresenceHandler recordPresenceHandler;

    public ExitRecordedPopup(LocalDate exitDate, LocalTime exitTime,
                             RecordPresenceHandler recordPresenceHandler) {
        this.exitDate = exitDate;
        this.exitTime = exitTime;
        this.recordPresenceHandler = recordPresenceHandler;
    }
    @FXML
    public void initialize(){
        date_hourLabel.setText("Uscita registrata in data " + exitDate.format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ", " +
                exitTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    @FXML
    void clickOkayExit(ActionEvent event) {
        recordPresenceHandler.clickedOkayExit();
    }

}
