package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

public class ViewShiftsInfoHandler {
    public void clickedShift(Shift finalShift, boolean flag) {
        Shift shiftEntity;
        try {
            shiftEntity = DBMSDaemon.getInstance().getShiftFlags(finalShift);
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createPopup("Shift Info",
                controller -> new ShiftInfoPopup(shiftEntity, flag));
    }
}
