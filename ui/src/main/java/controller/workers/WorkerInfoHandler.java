package controller.workers;

import commons.ConfirmAction;
import commons.WorkerStatus;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import view.navigation.NavigationManager;
import view.workers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class WorkerInfoHandler {

    private Worker viewedWorker;
    public void clickedRemove(Worker viewedWorker) {
        this.viewedWorker = viewedWorker;
        NavigationManager.getInstance().createPopup("Confirm",
                controller -> new ConfirmPopup(ConfirmAction.REMOVE, this));
    }
    public void clickedPromote(Worker viewedWorker){
        this.viewedWorker = viewedWorker;
        NavigationManager.getInstance().createPopup("Confirm",
                controller -> new ConfirmPopup(ConfirmAction.PROMOTE, this));
    }

    public void clickedConfirm(ConfirmAction confirmAction) {
        if (confirmAction == ConfirmAction.REMOVE){
            List<Worker> newWorkersList;
            Map<String, WorkerStatus> newWorkersStatus;
            try {
                DBMSDaemon.getInstance().removeWorker(viewedWorker.getId());
                newWorkersList = DBMSDaemon.getInstance().getWorkersList();
                newWorkersStatus = DBMSDaemon.getInstance().getWorkersStatus(LocalDate.now());
                NavigationManager.getInstance().closePopup("Confirm");
                NavigationManager.getInstance().createScreen("Workers Recap",
                        controller -> new WorkersRecapScreen(newWorkersList, newWorkersStatus, new WorkersRecapHandler()));
            } catch (DBMSException e) {
                e.printStackTrace();
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage(true));
            }
        }
        if (confirmAction == ConfirmAction.PROMOTE){
            Map<String, String> workerInfo;
            try {
                DBMSDaemon.getInstance().promoteWorker(viewedWorker.getId());
                viewedWorker = DBMSDaemon.getInstance().getWorkerData(viewedWorker.getId());
                workerInfo = DBMSDaemon.getInstance().getWorkerInfo(viewedWorker.getId());
                NavigationManager.getInstance().closePopup("Confirm");
                NavigationManager.getInstance().createScreen("Worker Info",
                        controller -> new WorkerInfoScreen(viewedWorker, workerInfo, new WorkersRecapHandler()));
            } catch (DBMSException e) {
                e.printStackTrace();
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage(true));
            }
        }

    }
}
