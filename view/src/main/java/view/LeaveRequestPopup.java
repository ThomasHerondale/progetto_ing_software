package view;

import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LeaveRequestPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private ComboBox<String> endTimeBox;

    @FXML
    private ComboBox<String> shiftsBox;

    @FXML
    private ComboBox<String> startTimeBox;

    private List<Shift> shiftList;
    private LeaveRequestHandler leaveRequestHandler;
    private Shift shiftSelected;

    public LeaveRequestPopup(List<Shift> shiftList, LeaveRequestHandler leaveRequestHandler){
        this.shiftList = shiftList;
        this.leaveRequestHandler = leaveRequestHandler;
    }
    @FXML
    public void initialize(){
        startTimeBox.setDisable(true);
        endTimeBox.setDisable(true);
        confirmButton.setDisable(true);
        for (Shift shift : shiftList) {
            shiftsBox.getItems().add(shift.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
            "           " + shift.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                    shift.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        shiftsBox.setOnAction(this::onShiftSelected);
        startTimeBox.setOnAction(this::onStartTimeSelected);
        endTimeBox.setOnAction(actionEvent -> confirmButton.setDisable(false));
    }

    private void onStartTimeSelected(ActionEvent actionEvent) {
        shiftsBox.setDisable(true);
        confirmButton.setDisable(true);
        endTimeBox.getItems().clear();
        endTimeBox.setPromptText("Orario di fine");
        if (LocalTime.parse(startTimeBox.getValue()) == shiftSelected.getStartTime()){
            endTimeBox.getItems().addAll(leaveRequestHandler.computeTimeLock
                    (LocalTime.parse(startTimeBox.getValue()).plusHours(1),
                            shiftSelected.getEndTime()));
        } else {
            endTimeBox.getItems().add(shiftSelected.getEndTime().
                    format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        endTimeBox.setDisable(false);
    }

    private void onShiftSelected(ActionEvent actionEvent) {
        shiftSelected = null;
        for (Shift shift : shiftList) {
            if (shiftsBox.getValue().equals(shift.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    "           " + shift.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                    shift.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))){
                shiftSelected = shift;
            }
        }
        startTimeBox.getItems().clear();
        endTimeBox.getItems().clear();
        startTimeBox.getItems().addAll(leaveRequestHandler.computeTimeLock
                (shiftSelected.getStartTime(), shiftSelected.getEndTime()));
        startTimeBox.setDisable(false);
    }

    @FXML
    public void clickConfirm(ActionEvent event) {
        leaveRequestHandler.clickedConfirm(shiftSelected.getDate(), startTimeBox.getValue(), endTimeBox.getValue());
    }

}
