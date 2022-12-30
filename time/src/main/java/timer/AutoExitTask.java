package timer;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimerTask;

public class AutoExitTask extends TimerTask {

    @Override
    public void run() {
        try {
            List<Shift> expiredShifts =
                    DBMSDaemon.getInstance().getExitMissingShifts(LocalDate.now(), LocalTime.now());
            for (var shift : expiredShifts) {
            }
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
    }
}
