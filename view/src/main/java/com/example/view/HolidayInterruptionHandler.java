package com.example.view;

import commons.Period;
import database.DBMSDaemon;
import database.DBMSException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayInterruptionHandler {

    private LocalDate startDate;

    public void clickedHolidayInterruption() {
        NavigationManager.getInstance().createPopup("Holiday Interruption",
                controller -> new HolidayInterruptionPopup(computeDateLock(), this));
    }
    private List<Period> computeDateLock(){
        List<Period> locksList = new ArrayList<>();
        locksList.add(new Period(LocalDate.MIN, LocalDate.now()));
        try {
            locksList.addAll(DBMSDaemon.getInstance().getHolidayInterruptions());
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
        return locksList;
    }
    private List<Period> computeDateLock(LocalDate startDate){
        List<Period> locksList = computeDateLock();
        /* Rimuovo tutti i periodi che iniziano prima di startDate */
        for (int i = 0; i<locksList.size(); i++){
            if (locksList.get(i).start().isBefore(startDate) || locksList.get(i).start().isEqual(startDate)){
                locksList.remove(locksList.get(i));
            }
        }
        List<Period> returnLocksList = new ArrayList<>();
        returnLocksList.add(new Period(LocalDate.MIN, startDate));
        LocalDate y = LocalDate.MAX;
        LocalDate z = LocalDate.MAX;
        long distance = 100000000;
        for (int i = 0; i < locksList.size(); i++){
            if (distance > Math.abs(locksList.get(i).start().toEpochDay() - startDate.toEpochDay())){
                distance = Math.abs(locksList.get(i).start().toEpochDay() - startDate.toEpochDay());
                y = locksList.get(i).start();
            }
        }
        returnLocksList.add(new Period(y, z));
        return returnLocksList;
    }
    public void selectedStartDate(LocalDate startDate, HolidayInterruptionPopup holidayInterruptionPopup){
        this.startDate = startDate;
        holidayInterruptionPopup.lockEndDates(computeDateLock(startDate));
    }
    public void clickedConfirm(LocalDate endDate){
        try {
            DBMSDaemon.getInstance().insertHolidayInterruption(startDate, endDate);
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().closePopup("Holiday Interruption");

    }
}
