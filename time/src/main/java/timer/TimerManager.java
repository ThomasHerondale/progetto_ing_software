package timer;

import java.time.Instant;
import java.util.Date;
import java.util.Timer;

public class TimerManager {
    private final Timer autoExitTimer;
    private long autoExitRate = 1_800_000;

    private boolean debugMode = false;

    private static TimerManager instance;

    private TimerManager() {
        this.autoExitTimer = new Timer(true);
    }

    public static TimerManager getInstance() {
        if (instance == null)
            instance = new TimerManager();
        return instance;
    }

    public void setDebugMode() {
        debugMode = true;
        autoExitTimer.
    }

    public void initialize() {
        var rate = debugMode ? 10_000 : autoExitRate;
        autoExitTimer.scheduleAtFixedRate(new AutoExitTask(debugMode), Date.from(Instant.now()), rate);
    }

}
