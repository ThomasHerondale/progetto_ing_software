package timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Questa classe programma l'esecuzione di un task specifico in un dato giorno del mese.
 */
public class MonthlyTimer {
    private final Runnable task;
    private final int dayOfMonth;
    private final int hourOfDay;
    private Timer current = new Timer();

    public static MonthlyTimer schedule(Runnable task, int dayOfMonth, int hourOfDay) {
        return new MonthlyTimer(task, dayOfMonth, hourOfDay);
    }

    public void cancelCurrent() {
        current.cancel();
        current.purge();
    }

    private MonthlyTimer(Runnable task, int dayOfMonth, int hourOfDay) {
        this.task = task;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        schedule();
    }

    private void schedule() {
        cancelCurrent();
        current = new Timer();
        current.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    schedule();
                }
            }
        }, nextDate());
    }

    private Date nextDate() {
        var runDate = Calendar.getInstance();
        runDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        runDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        runDate.set(Calendar.MINUTE, 0);
        runDate.set(Calendar.SECOND, 0);
        runDate.add(Calendar.MONTH, 1);
        return runDate.getTime();
    }
}
