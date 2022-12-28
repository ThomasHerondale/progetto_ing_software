package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

public class AccountInfoHandler {

    private Worker worker;

    public AccountInfoHandler(Worker worker){
        this.worker = worker;
    }
    public void clickedProfile() {
        //Counters workerCounters = DBMSDaemon.getInstance().getWorkerCounters(worker.getId());
        int workerCounters = 1;
        NavigationManager.getInstance().openAccountInfoScreen(worker, workerCounters, this);

    }

    public void clickedBack() {
        NavigationManager.getInstance().closeAccountInfoScreen();
    }
    public void clickedLogout() {
        NavigationManager.getInstance().createPopup("Confirm",
                //serve la WorkerAction enum...
                controller -> new ConfirmPopup(this));
    }

    public void clickedConfirm() {
        NavigationManager.getInstance().closePopup("Confirm");
        NavigationManager.getInstance().closeAccountInfoScreen();
        NavigationManager.getInstance().createScreen("Login",
                controller -> new LoginScreen());
    }
}
