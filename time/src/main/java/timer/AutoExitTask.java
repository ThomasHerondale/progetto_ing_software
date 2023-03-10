package timer;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import mail.MailManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimerTask;

/**
 * La {@link TimerTask} che si occupa di registrare in automatico le uscite dei turni ogni mezz'ora.
 */
class AutoExitTask extends TimerTask {
    private final String actionLogString;
    private final LocalDate currentDate;

    public AutoExitTask(boolean debugMode, LocalDate currentDate) {
        this.actionLogString = debugMode ? ": [DEBUG - AUTO_EXIT - ACTION]" : "";
        this.currentDate = currentDate;
    }

    @Override
    public void run() {
        try {
            List<Shift> expiredShifts =
                    DBMSDaemon.getInstance().getExitMissingShifts(currentDate, LocalTime.now());
            System.out.println("[DEBUG - AUTO_EXIT - INFO] Expired shifts :" + expiredShifts);
            DBMSDaemon.getInstance().recordAutoExit(expiredShifts);
            String adminMail = null;
            for (Shift shift : expiredShifts) {
                MailManager.getInstance().notifyAutoExitRecord(
                        shift.getOwner().getEmail(), shift.getOwner().getFullName()
                );
                if (DBMSDaemon.getInstance().getAutoExitCounter(shift.getOwner().getId()) > 5) {
                    if (adminMail == null)
                        adminMail = DBMSDaemon.getInstance().getAdminEmail();
                    MailManager.getInstance().notifyAutoExitLimitReached(
                            adminMail, shift.getOwner().getEmail(), shift.getOwner().getFullName()
                    );
                }
            }
            System.err.println(LocalTime.now() + actionLogString);
        } catch (DBMSException e) {
            System.err.println(LocalTime.now() + ": [DEBUG - AUTO_EXIT - DBMS ERROR]");
        }
    }
}
