package control;

import commons.HoursRecap;
import commons.Period;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import mail.MailManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe di metodi statici si occupa di calcolare gli stipendi degli impiegati.
 */
public class SalaryHandler {
    /**
     * La tariffa oraria dei dipendenti.
     */
    private static final double HOUR_WAGE = 12.5;
    /**
     * Il fattore per cui saranno moltiplicate le tariffe relative allo straordinario.
     */
    private static final double OVERTIME_FACTOR = 1.5;
    /**
     * Il fattore per cui saranno moltiplicate le tariffe relative al congedo parentale.
     */
    private static final double PARENTAL_LEAVE_FACTOR = 0.7;

    /**
     * Le gratifiche sullo stipendio per ogni livello.
     */
    private static final Map<Character, Double> RANK_SALARIES = Map.of(
            'H', 350.0,
            'A', 300.0,
            'B', 250.0,
            'C', 150.0,
            'D', 100.0
            );

    /**
     * Lo stipendio di base di ogni impiegato.
     */
    private static final Double BASE_SALARY = 150.0;

    /* Costruttore privato per impedire l'istanziazione di questa classe */
    private SalaryHandler() {}

    /**
     * Calcola lo stipendio di un dipendente del livello specificato in base alle ore di lavoro svolto.
     * @param rank il livello del dipendente
     * @param hours il riepilogo delle ore di lavoro svolte dal dipendente
     * @return lo stipendio calcolato
     */
    private static double computeSalary(char rank, HoursRecap hours) {
        /* Calcola le tariffe orarie per gli straordinari e i congedi parentali */
        var overtimeWage = HOUR_WAGE * OVERTIME_FACTOR;
        var parentalLeaveWage = HOUR_WAGE * PARENTAL_LEAVE_FACTOR;

        return BASE_SALARY +
                RANK_SALARIES.get(rank) +
                hours.ordinaryHours() * HOUR_WAGE +
                hours.overtimeHours() * overtimeWage +
                hours.parentalLeaveHours() * parentalLeaveWage;
    }

    /**
     * Calcola lo stipendio di tutti i dipendenti, con riferimento al periodo specificato.
     * @param referencePeriod il periodo di riferimento
     */
    public static void computeSalaries(Period referencePeriod) throws DBMSException {
        var dbms = DBMSDaemon.getInstance();
        Map<Worker, HoursRecap> salaryData = dbms.getWorkersData(referencePeriod);
        Map<Worker, Double> salaries = new HashMap<>();
        for (var workerInfo : salaryData.entrySet()) {
            var salary = computeSalary(workerInfo.getKey().getRank(), workerInfo.getValue());
            dbms.setSalary(workerInfo.getKey().getId(), referencePeriod.end(), salary);
            salaries.put(workerInfo.getKey(), salary);
        }
        MailManager.getInstance().notifyNewSalary(salaries);
    }
}
