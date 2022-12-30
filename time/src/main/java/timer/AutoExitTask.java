package timer;

import database.DBMSDaemon;
import database.DBMSException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TimerTask;

public class AutoExitTask extends TimerTask {

    @Override
    public void run() {
        try {
            System.out.println(DBMSDaemon.getInstance().getExitMissingShifts(LocalDate.now(), LocalTime.now()));
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
    }
}
