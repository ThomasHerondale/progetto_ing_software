package com.example.view;

import com.example.control.ShiftProposalHandler;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

import java.util.List;

public class ShiftHandler {

    public void clickedShowShifts() {
        List<Shift> shiftList;
        try {
            shiftList = DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
    }

    public void clickedBack(){
        if (Session.getInstance().getWorker().getRank() == 'H'){
            NavigationManager.getInstance().createScreen("Home (Admin)",
                    controller -> new HomeScreen());
        } else {
            NavigationManager.getInstance().createScreen("Home",
                    controller -> new HomeScreen());
        }
    }
}
