package timer;

import database.DBMSDaemon;
import database.DBMSException;

import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    /**
     * Il timer che si occupa delle uscite automatiche.
     */
    private final Timer autoExitTimer;
    /**
     * Il timer che si occupa del reset dei contatori dei dipendenti.
     */
    private QuarterlyTimer resetCountersTimer;
    /**
     * Il timer che si occupa del calcolo degli stipendi.
     */
    private QuarterlyTimer newSalariesTimer;
    /**
     * Il timer che si occupa del calcolo della nuova proposta di turnazione.
     */
    private QuarterlyTimer shiftProposalTimer;
    /**
     * L'intervallo tra una registrazione di uscite automatiche e un'altra.
     */
    private static final long AUTO_EXIT_RATE = 1_800_000;
    /**
     * La modalità di funzionamento del timer.
     */
    private boolean debugMode = false;
    /**
     * L'istanza di questo {@link TimerManager}, secondo il pattern <i>Singleton</i>.
     */
    private static TimerManager instance;

    private TimerManager() {
        this.autoExitTimer = new Timer(false);
    }

    /**
     * Ottiene l'unica istanza possibile di {@link TimerManager}.
     * @return l'istanza di questo TimerManager
     */
    public static TimerManager getInstance() {
        if (instance == null)
            instance = new TimerManager();
        return instance;
    }

    /**
     * Imposta la modalità di debug per questo manager. Questa modalità accorcia gli intervalli con cui i
     * vari task vengono eseguiti e stampa su {@link System#err} informazioni utili di debug.
     */
    public void setDebugMode() {
        debugMode = true;
    }

    /**
     * Inizializza il manager avviando tutti task.
     */
    public void initialize() {
        var rate = debugMode ? 10_000 : AUTO_EXIT_RATE;
        autoExitTimer.scheduleAtFixedRate(new AutoExitTask(debugMode), Date.from(Instant.now()), rate);
        this.resetCountersTimer = QuarterlyTimer.schedule(
                () -> {
                    try {
                        DBMSDaemon.getInstance().resetCounters();
                    } catch (DBMSException e) {
                        if (debugMode)
                            System.err.println("[DEBUG - RESET_COUNTERS - DBMS ERROR]");
                    }
                }, 27, 23);

        /* Task di debug */
        if (debugMode) {
            autoExitTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.err.println("[DEBUG - AUTO_EXIT - LIVE]");
                }
            }, Date.from(Instant.now()), 5_000);
        }
    }

    /**
     * Elimina la programmazione di tutte le task del manager e ne invalida l'istanza.
     */
    public void cancel() {
        autoExitTimer.cancel();
        autoExitTimer.purge();
        resetCountersTimer.cancelCurrent();
        invalidate();
    }

    /**
     * Invalida l'istanza del TimerManager
     */
    private static void invalidate() {
        instance = null;
    }

}
