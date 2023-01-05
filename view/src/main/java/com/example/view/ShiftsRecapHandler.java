package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

import java.util.List;

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
}
