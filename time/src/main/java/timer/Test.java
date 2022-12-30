package timer;

import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Test {

    private static final Timer timer = new Timer("autoExitTimer");
    public static void main(String[] args) {
        timer.scheduleAtFixedRate(new AutoExitTask(), Date.from(Instant.now()), 1000);
    }
}
