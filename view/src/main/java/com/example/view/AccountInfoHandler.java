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

    public void clickedConfirmLogout() {
        NavigationManager.getInstance().closePopup("Confirm");
        NavigationManager.getInstance().closeAccountInfoScreen();
        NavigationManager.getInstance().createScreen("Login",
                controller -> new LoginScreen());
    }

    public void clickedEditEmail() {
        NavigationManager.getInstance().createPopup("Edit",
                controller -> new EditPopup(EditableProperty.EMAIL, this));
    }

    public void clickedEditIBAN() {
        NavigationManager.getInstance().createPopup("Edit",
                controller -> new EditPopup(EditableProperty.IBAN, this));
    }

    public void clickedEditPhone() {
        NavigationManager.getInstance().createPopup("Edit",
                controller -> new EditPopup(EditableProperty.PHONE, this));
    }
    public void clickedConfirmEdit(String input, EditableProperty property){
        try {
            switch (property){
                case PHONE -> DBMSDaemon.getInstance().changePhone(worker.getId(), input);
                case EMAIL -> DBMSDaemon.getInstance().changeEmail(worker.getId(), input);
                case IBAN -> DBMSDaemon.getInstance().changeIban(worker.getId(), input);
            }
            //come faccio ad aggiornare la entity worker in ogni posto dove esiste?

            NavigationManager.getInstance().closePopup("Edit");
        } catch (DBMSException e){
            //TODO:
        }

    }
}
