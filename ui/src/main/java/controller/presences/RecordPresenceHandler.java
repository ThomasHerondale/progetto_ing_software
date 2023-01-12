package controller.presences;

import database.DBMSDaemon;
import database.DBMSException;
import view.presences.DelayNoticePopup;
import view.presences.EntranceRecordedPopup;
import view.presences.ExitRecordedPopup;
import view.workers.ErrorMessage;
import view.navigation.NavigationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class RecordPresenceHandler {
    public void clickedRecordEntrance(String name, String surname, String id) {
        LocalDate entranceDate = LocalDate.now();
        LocalTime entranceTime = LocalTime.now();
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, name, surname)){
                Optional<LocalTime> shiftStartTime = DBMSDaemon.getInstance().getShiftStartTime(id,
                        entranceDate);
                if (shiftStartTime.isPresent()){
                    if (checkTime(shiftStartTime.get())){
                        DBMSDaemon.getInstance().recordEntrance(id, entranceDate, entranceTime);
                        NavigationManager.getInstance().createPopup("Entrance Recorded",
                                controller -> new EntranceRecordedPopup(entranceDate, entranceTime, this));
                    }
                    else {
                        NavigationManager.getInstance().createPopup("Delay Notice",
                                controller -> new DelayNoticePopup(entranceDate, entranceTime, this));
                    }
                }
            } else {
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage("Credenziali errate."));
            }
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }

    private boolean checkTime(LocalTime shiftTime) {
        return Math.abs(LocalTime.now().toSecondOfDay() - shiftTime.toSecondOfDay()) < 600;
    }

    public void clickedRecordExit(String name, String surname, String id) {
        LocalDate exitDate = LocalDate.now();
        LocalTime exitTime = LocalTime.now();
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, name, surname)){
                Optional<LocalTime> shiftEndTime = DBMSDaemon.getInstance().getShiftEndTime(id, exitDate);
                if (shiftEndTime.isPresent()){
                    if (checkTime(shiftEndTime.get())){
                        DBMSDaemon.getInstance().recordExit(id, exitDate, exitTime);
                        NavigationManager.getInstance().createPopup("Exit Recorded",
                                controller -> new ExitRecordedPopup(exitDate, exitTime, this));
                    } else {
                        NavigationManager.getInstance().createPopup("Error Message",
                                controller -> new ErrorMessage("Non è possibile registrare un'uscita."));
                    }
                }

            }
            else {
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage("Credenziali errate."));
            }
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
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
