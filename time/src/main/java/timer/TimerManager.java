package timer;

import java.time.Instant;
import java.util.Date;
import java.util.Timer;

public class TimerManager {
    private final Timer autoExitTimer = new Timer(true);
    private long autoExitRate = 1_800_000;

    public TimerManager() {
        this(false);
    }

    public TimerManager(boolean debugMode) {
        var rate = debugMode ? 10_000 : autoExitRate;
        autoExitTimer.scheduleAtFixedRate(new AutoExitTask(), Date.from(Instant.now()), rate);
    }
}
