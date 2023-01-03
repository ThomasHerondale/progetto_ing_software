package com.example.view;

import com.example.control.ShiftProposalHandler;
import commons.Period;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import entities.Worker;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ShiftHandler {

    public void clickedShowShifts() {
        List<Shift> shiftList;
       /* try {
            shiftList = DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }*/
        var w = new Worker("098", "", "", 'A', "", "", "");
        var x = new Worker("678", "", "", 'A', "", "", "");
        var y = new Worker("123", "", "", 'A', "", "", "");
        var z = new Worker("000", "", "", 'A', "", "", "");
        var p = new Worker("999", "", "", 'A', "", "", "");
        var sh = new ShiftProposalHandler(List.of(w, x, y, z, p), Map.of(x, List.<Period>of(
                new Period(LocalDate.of(2023, 1, 9),
                        LocalDate.of(2023, 1, 9))), w, List.<Period>of(), y, List.of(),
                z, List.of(), p, List.of())
        );
        sh.computeNewShiftsProposal();
        NavigationManager.getInstance().createScreen("View Shifts",
                controller -> new ViewShiftsScreen(sh.shiftProposal, this));
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
