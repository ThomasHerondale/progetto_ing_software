package com.example.view;

import commons.ConfirmAction;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class InsertPresenceHandler {

    private Worker worker;
    private LocalDate currentDate;
    public void clickedInsertPresence() {
        currentDate = LocalDate.now();
        List<Worker> abstents;
        try {
            abstents = DBMSDaemon.getInstance().getAbsentWorkersList(currentDate);
        } catch (DBMSException e){
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createPopup("Insert Presence",
                controller -> new InsertPresencePopup(abstents, currentDate, this));


    }
    public void clickedConfirm(Worker worker){
        this.worker = worker;
        NavigationManager.getInstance().createPopup("Confirm",
                controller -> new ConfirmPopup(ConfirmAction.PRESENCE, worker, currentDate, this));
    }
    public void clickedConfirm(){
        /*
        try {
            DBMSDaemon.getInstance().recordPresence(worker, currentDate);
        } catch (DBMSException e){
            //TODO:
        }
         */
        NavigationManager.getInstance().closePopup("Confirm");
        NavigationManager.getInstance().closePopup("Insert Presence");
    }
}
