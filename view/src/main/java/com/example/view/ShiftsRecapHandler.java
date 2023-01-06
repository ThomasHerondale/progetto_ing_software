package com.example.view;

import commons.Period;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableMap;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftsRecapHandler {
    public void clickedShiftsRecap() {
        List<Shift> shiftsList;
        try {
            shiftsList = DBMSDaemon.getInstance().getShiftsList();
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createScreen("Shifts Recap",
                controller -> new ShiftsRecapScreen(shiftsList, this));
    }
    public void clickedBack(){
        NavigationManager.getInstance().createScreen("Home (Admin)",
                controller -> new HomeScreen());
    }
    public void clickedAuthorizeStrike(){
        Period locks = computeDateLock();
        NavigationManager.getInstance().createPopup("Authorize Strike",
                controller -> new AuthorizeStrikePopup(locks, this));
    }

    private Period computeDateLock() {
        return new Period(LocalDate.MIN, LocalDate.now());
    }

    public void clickedConfirm(String name, String description, LocalDate date,
                               ObservableMap<Character, BooleanProperty> selectionRank) {
        Map<Character, String> mapRanks = new HashMap<>();
        mapRanks.put('A', String.valueOf(selectionRank.get('A').get()));
        mapRanks.put('B', String.valueOf(selectionRank.get('B').get()));
        mapRanks.put('C', String.valueOf(selectionRank.get('C').get()));
        mapRanks.put('D', String.valueOf(selectionRank.get('D').get()));
        mapRanks.put('H', String.valueOf(selectionRank.get('H').get()));
        try {
            DBMSDaemon.getInstance().createStrike(name, description, date, mapRanks);
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().closePopup("Authorize Strike");
    }
}
