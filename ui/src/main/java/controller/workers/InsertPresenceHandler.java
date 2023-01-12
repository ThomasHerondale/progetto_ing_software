package controller.workers;

import commons.ConfirmAction;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import view.workers.ConfirmPopup;
import view.workers.ErrorMessage;
import view.workers.InsertPresencePopup;
import view.navigation.NavigationManager;

import java.time.LocalDate;
import java.util.List;

public class InsertPresenceHandler {

    private Worker worker;
    private LocalDate currentDate;
    public void clickedInsertPresence() {
        currentDate = LocalDate.now();
        List<Worker> abstents;
        try {
            abstents = DBMSDaemon.getInstance().getAbsentWorkersList(currentDate);
            NavigationManager.getInstance().createPopup("Insert Presence",
                    controller -> new InsertPresencePopup(abstents, currentDate, this));
        } catch (DBMSException e){
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
    public void clickedConfirm(Worker worker){
        this.worker = worker;
        NavigationManager.getInstance().createPopup("Confirm",
                controller -> new ConfirmPopup(ConfirmAction.PRESENCE, worker, currentDate, this));
    }
    public void clickedConfirm(){
        try {
            DBMSDaemon.getInstance().recordPresence(worker.getId(), currentDate);
            NavigationManager.getInstance().closePopup("Confirm");
            NavigationManager.getInstance().closePopup("Insert Presence");
        } catch (DBMSException e){
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
}
