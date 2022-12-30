package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

public class ViewShiftsInfoHandler {
    public void clickedShift(Shift finalShift) throws DBMSException {
        Shift shiftEntity = DBMSDaemon.getInstance().getShiftFlags(finalShift);
        NavigationManager.getInstance().createPopup("Shift Info",
                controller -> new ShiftInfoPopup(shiftEntity));
    }
}
