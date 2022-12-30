package com.example.view;

import database.DBMSDaemon;
import entities.Shift;

public class ViewShiftsInfoHandler {
    public void clickedShift(Shift finalShift){
        Shift shiftEntity = DBMSDaemon.getInstance().getShiftInfo(finalShift);
        NavigationManager.getInstance().createPopup("Shift Info",
                controller -> new ShiftInfoPopup(shiftEntity));
    }
}
