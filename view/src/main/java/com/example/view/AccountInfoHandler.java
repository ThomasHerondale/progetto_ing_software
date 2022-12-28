package com.example.view;

import commons.Counters;
import commons.EditableProperty;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

import java.util.Map;

public class AccountInfoHandler {

    private Worker worker;

    public AccountInfoHandler(Worker worker){
        this.worker = worker;
    }
    public void clickedProfile() {
        try {
            Map<String, String> counters = DBMSDaemon.getInstance().getAccountData(worker.getId());
            int autoExit = Integer.parseInt(counters.get("autoExitCount"));
            int delay = Integer.parseInt(counters.get("delayCount"));
            int holiday = Integer.parseInt(counters.get("holidayCount"));
            int parentalLeave = Integer.parseInt(counters.get("parentalLeaveCount"));
            Counters workerCounters = new Counters(autoExit, delay, holiday, parentalLeave);
            NavigationManager.getInstance().openAccountInfoScreen(worker, workerCounters, this);
        } catch (DBMSException e) {
            //TODO:
        }


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

    public void clickedEditEmail() {
        NavigationManager.getInstance().createPopup("Edit",
                controller -> new EditPopup(EditableProperty.EMAIL));
    }
}
