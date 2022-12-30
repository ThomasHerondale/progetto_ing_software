package timer;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimerTask;

public class AutoExitTask extends TimerTask {

    private final String logString;

    public AutoExitTask(boolean debugMode) {
        this.logString = debugMode ? "[DEBUG - AUTO_EXIT - NOTICE]" : "";
    }

    @Override
    public void run() {
        try {
            List<Shift> expiredShifts =
                    DBMSDaemon.getInstance().getExitMissingShifts(LocalDate.now(), LocalTime.now());
            DBMSDaemon.getInstance().recordAutoExit(expiredShifts);
            System.out.println(logString);
        } catch (DBMSException e) {
            System.err.println();
        }
    }
}
