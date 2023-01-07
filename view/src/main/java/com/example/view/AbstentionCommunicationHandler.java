package com.example.view;

import commons.Abstention;
import commons.Period;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbstentionCommunicationHandler {

    List<Period> locksList;
    public void clickedCommunicate(Abstention abstention){
        locksList = new ArrayList<>();
        if (abstention == Abstention.HOLIDAY) {
            long distance = 100000000;
            LocalDate start = LocalDate.MIN;
            LocalDate end = LocalDate.MAX;
            for (int i=0; i<firstDaysOfQuarters.size(); i++){
                if (LocalDate.now().isBefore(firstDaysOfQuarters.get(i))){
                    if (firstDaysOfQuarters.get(i).toEpochDay() - LocalDate.now().toEpochDay() < distance){
                        distance = firstDaysOfQuarters.get(i).toEpochDay() - LocalDate.now().toEpochDay();
                        end = firstDaysOfQuarters.get(i).plusDays(1);
                    }
                }
            }
            System.out.println(end);
            System.out.println(distance);
            try {
                locksList = DBMSDaemon.getInstance().getHolidayInterruptions();
                locksList.add(new Period(start, end));
            } catch (DBMSException e) {
                //TODO:
                throw new RuntimeException(e);
            }
        }
        locksList.add(new Period(LocalDate.MIN, LocalDate.now()));
        List<Period> finalLocksList = locksList;
        NavigationManager.getInstance().createPopup("Abstention Communication",
                    controller -> new AbstentionCommunicationPopup(abstention, finalLocksList, this));
    }
    private static final List<LocalDate> firstDaysOfQuarters = List.of(
            LocalDate.of(2023, 1, 2),
            LocalDate.of(2023, 4, 3),
            LocalDate.of(2023, 7, 3),
            LocalDate.of(2023, 10, 2)
    );

    public void selectedStartDate(LocalDate startDate, AbstentionCommunicationPopup abstentionCommunicationPopup) {
        abstentionCommunicationPopup.lockEndDates(computeDateLock(startDate.minusDays(1)));
    }

    private List<Period> computeDateLock(LocalDate startDate) {
        /* Rimuovo tutti i periodi che iniziano prima di startDate */
        for (int i = 0; i<locksList.size(); i++){
            if (locksList.get(i).start().isBefore(startDate) || locksList.get(i).start().isEqual(startDate)){
                locksList.set(i, new Period(LocalDate.MAX.minusDays(1), LocalDate.MAX));
            }
        }
        List<Period> returnLocksList = new ArrayList<>();
        returnLocksList.add(new Period(LocalDate.MIN, startDate));
        LocalDate y = LocalDate.now().plusYears(2);
        LocalDate z = LocalDate.MAX;
        long distance = 100000000;
        for (Period period : locksList) {
            if (distance > period.start().toEpochDay() - startDate.toEpochDay()) {
                distance = period.start().toEpochDay() - startDate.toEpochDay();
                y = period.start();
            }
        }
        returnLocksList.add(new Period(y, z));
        return returnLocksList;
    }

    public void clickedConfirm(Period abstentionPeriod, Abstention abstention) {
        if (abstention == Abstention.ILLNESS){
            try {
                DBMSDaemon.getInstance().setIllnessPeriod(Session.getInstance().getWorker().getId(),
                        abstentionPeriod.start(), abstentionPeriod.end());
            } catch (DBMSException e) {
                //TODO:
                throw new RuntimeException(e);
            }
            //TODO: invoca modifica turnazione
        }
        if (abstention == Abstention.HOLIDAY){
            try {
                if (DBMSDaemon.getInstance().checkHolidayCounter(Session.getInstance().getWorker().getId(),
                        abstentionPeriod.start(), abstentionPeriod.end())){
                    DBMSDaemon.getInstance().setHolidayPeriod(Session.getInstance().getWorker().getId(),
                            abstentionPeriod.start(), abstentionPeriod.end());
                } else {
                    NavigationManager.getInstance().createPopup("Error Message",
                            controller -> new ErrorMessage("Ferie insufficienti."));
                }
            } catch (DBMSException e) {
                //TODO:
                throw new RuntimeException(e);
            }
        }
        if (abstention == Abstention.PARENTAL_LEAVE){
            try {
                if (DBMSDaemon.getInstance().checkParentalLeaveCounter(Session.getInstance().getWorker().getId(),
                        abstentionPeriod.start(), abstentionPeriod.end())){
                    DBMSDaemon.getInstance().setParentalLeavePeriod(Session.getInstance().getWorker().getId(),
                            abstentionPeriod.start(), abstentionPeriod.end());
                    //TODO: invoca modifica turnazione
                } else {
                    NavigationManager.getInstance().createPopup("Error Message",
                            controller -> new ErrorMessage("Limite ore congedo parentale raggiunto."));
                }

            } catch (DBMSException e) {
                //TODO:
                throw new RuntimeException(e);
            }
        }
        NavigationManager.getInstance().closePopup("Abstention Communication");
    }
}
