package timer;

import commons.Period;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class QuarterlyTimer {
    private final Runnable task;
    private Timer current = new Timer();

    private final List<Calendar> firstDayOfQuarters;

    private final int currentQuarter;

    public static QuarterlyTimer schedule(Runnable task) {
        return new QuarterlyTimer(task);
    }

    public void cancelCurrent() {
        current.cancel();
        current.purge();
    }

    private QuarterlyTimer(Runnable task) {
        this.task = task;

        var builder = new Calendar.Builder();
        this.firstDayOfQuarters = List.of(
                builder.setDate(2023, 0, 2).build(),
                builder.setDate(2023, 3, 3).build(),
                builder.setDate(2023, 6, 3).build(),
                builder.setDate(2023, 9, 2).build()
        );
        var f = new SimpleDateFormat("yyyy-MM-dd");
        for (var w : firstDayOfQuarters) {
            System.out.println(f.format(w.getTime()));
        }
        this.currentQuarter = getCurrentQuarter();
        schedule();
    }

    public int getCurrentQuarter() {
        var currentDate = LocalDate.now();
        for (var i = 1; i < firstDayOfQuarters.size(); i++) {
            var startDate = LocalDate.of(
                    firstDayOfQuarters.get(i - 1).get(Calendar.YEAR),
                    firstDayOfQuarters.get(i - 1).get(Calendar.MONTH) + 1,
                    firstDayOfQuarters.get(i - 1).get(Calendar.DAY_OF_MONTH)
            );
            var endDate = LocalDate.of(
                    firstDayOfQuarters.get(i).get(Calendar.YEAR),
                    firstDayOfQuarters.get(i).get(Calendar.MONTH) + 1,
                    firstDayOfQuarters.get(i).get(Calendar.DAY_OF_MONTH)
            );
            if (Period.comprehends(startDate, endDate, currentDate))
                return i;
        }
        return 0;
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
        var runDate = firstDayOfQuarters.get(currentQuarter + 1);
        var f = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("F: " + f.format(firstDayOfQuarters.get(currentQuarter + 1).getTime()));
        System.out.println("Date: " + f.format(runDate.getTime()));
        return runDate.getTime();
    }
}

