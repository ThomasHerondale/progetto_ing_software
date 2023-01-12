package view.workers;

import commons.Abstention;
import commons.Period;
import controller.workers.AbstentionCommunicationHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;

public class AbstentionCommunicationPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private DatePicker endDateCalendar;

    @FXML
    private DatePicker startDateCalendar;

    @FXML
    private Label titleLabel;

    private Abstention abstention;
    private AbstentionCommunicationHandler abstentionCommunicationHandler;

    List<Period> locksList;

    public AbstentionCommunicationPopup(Abstention abstention, List<Period> locksList,
                                        AbstentionCommunicationHandler abstentionCommunicationHandler) {
        this.abstention = abstention;
        this.abstentionCommunicationHandler = abstentionCommunicationHandler;
        this.locksList = locksList;
    }
    @FXML
    public void initialize(){
        titleLabel.setText("Comunica " + abstention.getStringValue());

        endDateCalendar.setDisable(true);
        confirmButton.setDisable(true);

        startDateCalendar.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                setDisabled(false);
                setMouseTransparent(false);
                for (Period period : locksList) {
                    if (period.comprehends(localDate)) {
                        setDisabled(true);
                        setMouseTransparent(true);
                    }
                }

            }
        });
        startDateCalendar.setOnAction(this::onStartDateSelected);
        endDateCalendar.setOnAction(actionEvent -> confirmButton.setDisable(false));
    }

    private void onStartDateSelected(ActionEvent actionEvent) {
        endDateCalendar.setDisable(false);
        abstentionCommunicationHandler.selectedStartDate(startDateCalendar.getValue(), this);
    }

    public void lockEndDates(List<Period> computeDateLock){
        endDateCalendar.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                setDisabled(false);
                setMouseTransparent(false);
                for (Period period : computeDateLock) {
                    if (period.comprehends(localDate)) {
                        setDisabled(true);
                        setMouseTransparent(true);
                    }
                }
            }
        });
    }

    @FXML
    public void clickConfirm(ActionEvent event) {
        abstentionCommunicationHandler.clickedConfirm(new Period(startDateCalendar.getValue(),
                endDateCalendar.getValue()), abstention);
    }

}

