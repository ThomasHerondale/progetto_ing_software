package timer;

import commons.Period;
import control.SalaryHandler;
import control.ShiftProposalHandler;
import database.DBMSDaemon;
import database.DBMSException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    /**
     * Il timer che si occupa delle uscite automatiche.
     */
    private final Timer autoExitTimer;

    /**
     * La data di inizio di ogni trimestre.
     */
    private static final List<LocalDate> firstDaysOfQuarters = List.of(
            LocalDate.of(2023, 1, 2),
            LocalDate.of(2023, 4, 3),
            LocalDate.of(2023, 7, 3),
            LocalDate.of(2023, 10, 2)
    );

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
        this.autoExitTimer = new Timer(true);
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
        /* Programma il timer delle uscite automatiche ogni mezz'ora */
        var rate = debugMode ? 10_000 : AUTO_EXIT_RATE;
        var currentDate = debugMode ?
                // LocalDate.of(2023, 1, 27)
                firstDaysOfQuarters.get(0).minusWeeks(1)
                :
                LocalDate.now();
        var currentTime = debugMode ?
                LocalTime.of(23, 30)
                :
                LocalTime.now();

        autoExitTimer.scheduleAtFixedRate(new AutoExitTask(debugMode), Date.from(Instant.now()), rate);

        /* Task di debug */
        if (debugMode) {
            autoExitTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.err.println("[DEBUG - AUTO_EXIT - LIVE]");
                }
            }, Date.from(Instant.now()), 5_000);
        }

        /* Controlla la data per proporre una nuova turnazione */
        for (var firstDay : firstDaysOfQuarters) {
            var shiftProposalDay = firstDay.minusWeeks(1);
            if (currentDate.equals(shiftProposalDay))
                newShiftProposal(firstDay);
        }

        /* Controlla la data per calcolare gli stipendi e resettare i contatori*/
        /* Se è il 27 di un mese, e sono passate le 23 */
        if (currentDate.getDayOfMonth() == 27 &&
                currentTime.isAfter(LocalTime.of(23, 0))) {
            if (debugMode)
                System.err.println("[DEBUG - SALARIES - ACTION]...");
            newSalaries(currentDate);
            if (debugMode)
                System.err.println("[DEBUG - SALARIES - ACTION]");
            resetCounters();
            if (debugMode)
                System.err.println("[DEBUG - RESET_COUNTERS - ACTION]");
        }
    }

    private void resetCounters() {
        try {
            DBMSDaemon.getInstance().resetCounters();
        } catch (DBMSException e) {
            throw new TimerException(e.getMessage());
        }
    }

    private void newShiftProposal(LocalDate firstDayOfQuarter) {
        if (debugMode)
            System.err.println("[DEBUG - SHIFT_PROPOSAL - ACTION]...");
        try {
            var workers = DBMSDaemon.getInstance().getWorkersList();
            var holidays = DBMSDaemon.getInstance().getRequestedHolidays(firstDayOfQuarter);
            var handler = new ShiftProposalHandler(firstDayOfQuarter, workers, holidays);
            handler.computeNewShiftsProposal();
        } catch (DBMSException e) {
            throw new TimerException(e.getMessage());
        }
        if (debugMode)
            System.err.println("[DEBUG - SHIFT_PROPOSAL - ACTION]");
    }

    private void newSalaries(LocalDate endDate) {
        System.out.println("End: " + endDate);
        var referencePeriod = new Period(
                endDate.minusMonths(1),
                endDate.minusDays(1)
        );
        try {
            SalaryHandler.computeSalaries(referencePeriod);
        } catch (DBMSException e) {
            e.printStackTrace();
            throw new TimerException(e.getMessage());
        }
    }

    /**
     * Elimina la programmazione di tutte le task del manager e ne invalida l'istanza.
     */
    public void cancel() {
        autoExitTimer.cancel();
        autoExitTimer.purge();
        invalidate();
    }

    /**
     * Invalida l'istanza del TimerManager
     */
    private static void invalidate() {
        instance = null;
    }

}
