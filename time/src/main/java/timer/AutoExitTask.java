package timer;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimerTask;

/**
 * La {@link TimerTask} che si occupa di registrare in automatico le uscite dei turni ogni mezz'ora.
 */
class AutoExitTask extends TimerTask {
    private final String actionLogString;

    public AutoExitTask(boolean debugMode) {
        this.actionLogString = debugMode ? ": [DEBUG - AUTO_EXIT - ACTION]" : "";
    }

    @Override
    public void run() {
        try {
            List<Shift> expiredShifts =
                    DBMSDaemon.getInstance().getExitMissingShifts(LocalDate.now(), LocalTime.now());
            DBMSDaemon.getInstance().recordAutoExit(expiredShifts);
            System.err.println(LocalTime.now() + actionLogString);
        } catch (DBMSException e) {
            System.err.println(LocalTime.now() + ": [DEBUG - AUTO_EXIT - DBMS ERROR]");
        }
    }
}
