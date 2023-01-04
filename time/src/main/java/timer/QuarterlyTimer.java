package timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class QuarterlyTimer {
    private final Runnable task;
    private final int dayOfMonth;
    private final int hourOfDay;
    private final int month;
    private Timer current = new Timer();

    public static QuarterlyTimer schedule(Runnable task, int dayOfMonth, int hourOfDay, int month) {
        return new QuarterlyTimer(task, dayOfMonth, hourOfDay, month);
    }

    public void cancelCurrent() {
        current.cancel();
        current.purge();
    }

    private QuarterlyTimer(Runnable task, int dayOfMonth, int hourOfDay, int month) {
        this.task = task;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        this.month = month;
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
        runDate.set(Calendar.MONTH, month);
        runDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        runDate.set(Calendar.MINUTE, 0);
        runDate.set(Calendar.SECOND, 0);
        runDate.add(Calendar.MONTH, 3);
        return runDate.getTime();
    }
}

