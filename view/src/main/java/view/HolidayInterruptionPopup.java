package view;

import commons.Period;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.List;

public class HolidayInterruptionPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private DatePicker endDateCalendar;

    @FXML
    private DatePicker startDateCalendar;

    private List<Period> locksList;
    private HolidayInterruptionHandler holidayInterruptionHandler;
    public HolidayInterruptionPopup(List<Period> locksList, HolidayInterruptionHandler holidayInterruptionHandler) {
        this.locksList = locksList;
        this.holidayInterruptionHandler = holidayInterruptionHandler;

    }
    @FXML
    public void initialize(){
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
        holidayInterruptionHandler.selectedStartDate(startDateCalendar.getValue(), this);
    }
    @FXML
    void clickConfirm(ActionEvent event) {
        holidayInterruptionHandler.clickedConfirm(endDateCalendar.getValue());
    }


    public void lockEndDates(List<Period> computeDateLock) {
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
}
