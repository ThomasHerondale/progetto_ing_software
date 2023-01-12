package view;

import commons.ConfirmAction;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

import java.util.Map;

public class EnableParentalLeaveHandler {

    private Worker viewedWorker;
    private int hours;
    public void clickedEnableParentalLeave(Worker viewedWorker) {
        this.viewedWorker = viewedWorker;
        hours = computeHours();
        NavigationManager.getInstance().createPopup("Confirm",
                controller -> new ConfirmPopup(ConfirmAction.ENABLE_PARENTAL_LEAVE, this, hours));
    }

    private int computeHours() {
        return 200;
    }

    public void clickedConfirm(){
        Map<String, String> workerInfo;
        try {
            DBMSDaemon.getInstance().enableParentalLeave(viewedWorker.getId(), hours);
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
