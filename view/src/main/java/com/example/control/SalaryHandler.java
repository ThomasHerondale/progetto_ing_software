package com.example.control;

import java.util.Map;

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

    static double computeSalary(char rank, double ordinaryHours, double overtimeHours, double parentLeaveHours) {
        /* Calcola le tariffe orarie per gli straordinari e i congedi parentali */
        var overtimeWage = hourWage * overtimeFactor;
        var parentalLeaveWage = hourWage * parentalLeaveFactor;

        return baseSalary +
                rankSalaries.get(rank) +
                ordinaryHours * hourWage +
                overtimeHours * overtimeWage +
                parentLeaveHours * parentalLeaveWage;
    }


}
