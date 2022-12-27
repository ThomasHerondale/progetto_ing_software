package com.example.view;

import database.DBMSDaemon;
import entities.Worker;

public class AccountInfoHandler {
    public void clickedProfile(Worker worker) {

        //Counters workerCounters = DBMSDaemon.getInstance().getWorkerCounters(worker.getId());
        int workerCounters = 1;
        NavigationManager.getInstance().createScreen("Info Account",
                controller -> new AccountInfoScreen(worker, workerCounters, this));
    }
}
