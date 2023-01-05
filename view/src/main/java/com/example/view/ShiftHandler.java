package com.example.view;

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
        /*
        var w = new Worker("000", "", "", 'A', "", "", "");
        var x = new Worker("111", "", "", 'A', "", "", "");
        var y = new Worker("222", "", "", 'A', "", "", "");
        var z = new Worker("333", "", "", 'A', "", "", "");
        var sh = new ShiftProposalHandler(
                LocalDate.of(2023, 1, 2),
                List.of(w, x, y, z),
                Map.of(
                        w, List.of(),
                        x, List.of(),
                        y, List.of(),
                        z, List.of()
                )
        );
        sh.computeNewShiftsProposal(); */
        NavigationManager.getInstance().createScreen("View Shifts",
                controller -> new ViewShiftsScreen(shiftList, this));
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
