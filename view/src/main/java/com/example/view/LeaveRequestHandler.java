package com.example.view;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestHandler {
    public void clickedLeave() {
        List<Shift> shiftList;
        List<Shift> realShiftList = new ArrayList<>();
        try {
            shiftList = DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
        for (Shift shift : shiftList) {
            if (shift.getDate().isAfter(LocalDate.now()) ||
                    shift.getDate().isEqual(LocalDate.now())) {
                realShiftList.add(shift);
            }
        }
        NavigationManager.getInstance().createPopup("Leave Request",
                controller -> LeaveRequestPopup(realShiftList, this));
    }
}
