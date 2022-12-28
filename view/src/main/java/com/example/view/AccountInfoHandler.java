package com.example.view;

import commons.Counters;
import commons.EditableProperty;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;

import java.util.Map;

public class AccountInfoHandler {
    public void clickedProfile() {
        try {
            Map<String, String> counters = DBMSDaemon.getInstance().getAccountData(Session.getInstance().getWorker().getId());
            int autoExit = Integer.parseInt(counters.get("autoExitCount"));
            int delay = Integer.parseInt(counters.get("delayCount"));
            int holiday = Integer.parseInt(counters.get("holidayCount"));
            int parentalLeave = Integer.parseInt(counters.get("parentalLeaveCount"));
            Counters workerCounters = new Counters(autoExit, delay, holiday, parentalLeave);
            NavigationManager.getInstance().openAccountInfoScreen(workerCounters, this);
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
        Session.invalidate();
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
                case PHONE -> DBMSDaemon.getInstance().changePhone(Session.getInstance().getWorker().getId(), input);
                case EMAIL -> DBMSDaemon.getInstance().changeEmail(Session.getInstance().getWorker().getId(), input);
                case IBAN -> DBMSDaemon.getInstance().changeIban(Session.getInstance().getWorker().getId(), input);
            }
            //aggiorna la entity worker
            Session.getInstance().update(DBMSDaemon.getInstance().getWorkerData(Session.getInstance().getWorker().getId()));
            NavigationManager.getInstance().closePopup("Edit");
            NavigationManager.getInstance().closeAccountInfoScreen();
            clickedProfile();
        } catch (DBMSException e){
            //TODO:
        }

    }
}
