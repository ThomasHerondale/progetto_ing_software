package view;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import mail.MailManager;

import java.time.LocalTime;

public class RecordEntryHandler {

    private Shift shiftEntity;
    public void clickedRecordEntry(Shift shiftEntity) {
        this.shiftEntity = shiftEntity;
        NavigationManager.getInstance().createPopup("Remote Record Entry",
                controller -> new RemoteRecordEntryPopup(shiftEntity, this));
    }

    public void clickedConfirm(LocalTime entryTime) {
        try {
            if (entryTime.minusMinutes(11).isBefore(shiftEntity.getStartTime())) {
                DBMSDaemon.getInstance().recordEntrance(Session.getInstance().getWorker().getId(),
                        shiftEntity.getDate(), entryTime);
            } else {
                DBMSDaemon.getInstance().recordDelay(Session.getInstance().getWorker().getId(),
                        shiftEntity.getDate(), entryTime);
                int delayCounter = DBMSDaemon.getInstance().getDelayCounter(Session.getInstance().getWorker().getId());
                if (delayCounter > 5){ //requisito non funzionale
                    MailManager.getInstance().notifyDelayLimitReached(
                            DBMSDaemon.getInstance().getAdminEmail(), Session.getInstance().getWorker().getEmail(),
                            Session.getInstance().getWorker().getFullName());
                }
            }
            NavigationManager.getInstance().closePopup("Remote Record Entry");
            NavigationManager.getInstance().closePopup("Shift Info");
        } catch (DBMSException e){
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
}
