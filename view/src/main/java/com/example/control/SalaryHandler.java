package com.example.control;

import commons.HoursRecap;
import commons.Period;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

import java.util.Map;

/**
 * Questa classe di metodi statici si occupa di calcolare gli stipendi degli impiegati.
 */
public class SalaryHandler {
    /**
     * La tariffa oraria dei dipendenti.
     */
    private static final double hourWage = 12.5;
    /**
     * Il fattore per cui saranno moltiplicate le tariffe relative allo straordinario.
     */
    private static final double overtimeFactor = 1.5;
    /**
     * Il fattore per cui saranno moltiplicate le tariffe relative al congedo parentale.
     */
    private static final double parentalLeaveFactor = 0.7;

    /**
     * Le gratifiche sullo stipendio per ogni livello.
     */
    private static final Map<Character, Double> rankSalaries = Map.of(
            'H', 350.0,
            'A', 300.0,
            'B', 250.0,
            'C', 150.0,
            'D', 100.0
            );

    /**
     * Lo stipendio di base di ogni impiegato.
     */
    private static final Double baseSalary = 150.0;

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
        var overtimeWage = hourWage * overtimeFactor;
        var parentalLeaveWage = hourWage * parentalLeaveFactor;

        return baseSalary +
                rankSalaries.get(rank) +
                hours.ordinaryHours() * hourWage +
                hours.overtimeHours() * overtimeWage +
                hours.parentalLeaveHours() * parentalLeaveWage;
    }

    /**
     * Calcola lo stipendio di tutti i dipendenti, con riferimento al periodo specificato.
     * @param referencePeriod il periodo di riferimento
     */
    public static void computeSalaries(Period referencePeriod) {
        try {
            var dbms = DBMSDaemon.getInstance();
            Map<Worker, HoursRecap> salaryData = dbms.getWorkersData(referencePeriod);
            for (var workerInfo : salaryData.entrySet()) {
                var salary = computeSalary(workerInfo.getKey().getRank(), workerInfo.getValue());
                dbms.setSalary(workerInfo.getKey().getId(), referencePeriod.end(), salary);
            }
        } catch (DBMSException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
