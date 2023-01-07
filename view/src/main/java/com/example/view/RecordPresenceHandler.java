package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;

import java.time.LocalDate;
import java.time.LocalTime;

public class RecordPresenceHandler {
    public void clickedRecordEntrance(String name, String surname, String id) {
        LocalDate entranceDay = LocalDate.now();
        LocalTime entranceTime = LocalTime.now();
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, name, surname)){
                LocalTime shiftStartTime = DBMSDaemon.getInstance().getShiftStartTime(id,
                        entranceDay, entranceTime);
                if (checkTime(shiftStartTime)){
                    DBMSDaemon.getInstance().recordEntrance(id, entranceDay, entranceTime);
                    NavigationManager.getInstance().createPopup("Entrance Recorded",
                            controller -> new EntranceRecordedPopup(entranceDay, entranceTime, this));
                }
                else {
                    NavigationManager.getInstance().createPopup("Delay Notice",
                            controller -> new DelayNoticePopup(entranceDay, entranceTime, this));
                }
            } else {
                NavigationManager.getInstance().createPopup("Error",
                        controller -> new ErrorMessage("Credenziali errate."));
            }
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
    }

    private boolean checkTime(LocalTime shiftTime) {
        if (Math.abs(LocalTime.now().toSecondOfDay() - shiftTime.toSecondOfDay()) < 600){
                return true;
            }
        return false;
    }

    public void clickedRecordExit(String name, String surname, String id) {
        LocalDate exitDate = LocalDate.now();
        LocalTime exitTime = LocalTime.now();
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, name, surname)){
                LocalTime shiftEndTime = DBMSDaemon.getInstance().getShiftEndTime(id, exitDate, exitTime);
                if (checkTime(shiftEndTime)){
                    DBMSDaemon.getInstance().recordExit(id, exitDate, exitTime);
                    NavigationManager.getInstance().createPopup("Exit Recorded",
                            controller -> new ExitRecordedPopup(exitDate, exitTime, this));
                } else {
                    NavigationManager.getInstance().createPopup("Error",
                            controller -> new ErrorMessage("Non è possibile registrare un'uscita."));
                }
            }
            else {
                NavigationManager.getInstance().createPopup("Error",
                        controller -> new ErrorMessage("Credenziali errate."));
            }
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
    }

    public void clickedOkayEntrance() {
        NavigationManager.getInstance().closePopup("Entrance Recorded");
    }

    public void clickedOkay() {
        NavigationManager.getInstance().closePopup("Delay Notice");
    }

    public void clickedOkayExit() {
        NavigationManager.getInstance().closePopup("Exit Recorded");
    }
}
