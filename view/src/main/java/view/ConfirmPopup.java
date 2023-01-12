package view;

import commons.ConfirmAction;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConfirmPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private Label description;

    @FXML
    private Label title;

    private AccountInfoHandler accountInfoHandler;
    private InsertPresenceHandler insertPresenceHandler;
    private final ConfirmAction confirmAction;
    private Worker presenceWorker;

    private WorkerInfoHandler workerInfoHandler;
    private EnableParentalLeaveHandler enableParentalLeaveHandler;
    private String hours;
    private LocalDate date;
    public ConfirmPopup(ConfirmAction confirmAction, AccountInfoHandler accountInfoHandler){
        this.confirmAction = confirmAction;
        this.accountInfoHandler = accountInfoHandler;
    }
    public ConfirmPopup(ConfirmAction confirmAction, Worker worker, LocalDate date, InsertPresenceHandler insertPresenceHandler){
        this.confirmAction = confirmAction;
        this.insertPresenceHandler = insertPresenceHandler;
        this.presenceWorker = worker;
        this.date = date;
    }
    public ConfirmPopup (ConfirmAction confirmAction, WorkerInfoHandler workerInfoHandler){
        this.confirmAction = confirmAction;
        this.workerInfoHandler = workerInfoHandler;
    }
    public ConfirmPopup (ConfirmAction confirmAction, EnableParentalLeaveHandler enableParentalLeaveHandler, int hours){
        this.confirmAction = confirmAction;
        this.enableParentalLeaveHandler = enableParentalLeaveHandler;
        this.hours = String.valueOf(hours);
    }
    @FXML
    public void initialize(){
        title.setText(confirmAction.getTitleString());
        if (confirmAction == ConfirmAction.PRESENCE){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            description.setText(confirmAction.getDescriptionString().replace
                    ("$worker", presenceWorker.getFullName()).replace
                    ("$date", date.format(formatter)));
        } else if (confirmAction == ConfirmAction.ENABLE_PARENTAL_LEAVE) {
            description.setText(confirmAction.getDescriptionString().replace("$hours", hours));
        } else {
            description.setText(confirmAction.getDescriptionString());
        }

    }
    @FXML
    void clickConfirm(ActionEvent event) {
        if (confirmAction == ConfirmAction.LOGOUT){
            accountInfoHandler.clickedConfirmLogout();
        }
        if (confirmAction == ConfirmAction.PRESENCE){
            insertPresenceHandler.clickedConfirm();
        }
        if (confirmAction == ConfirmAction.REMOVE){
            workerInfoHandler.clickedConfirm(confirmAction);
        }
        if (confirmAction == ConfirmAction.PROMOTE){
            workerInfoHandler.clickedConfirm(confirmAction);
        }
        if (confirmAction == ConfirmAction.ENABLE_PARENTAL_LEAVE){
            enableParentalLeaveHandler.clickedConfirm();
        }
    }

}
