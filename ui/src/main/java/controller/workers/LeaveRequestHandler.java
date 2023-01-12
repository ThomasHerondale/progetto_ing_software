package controller.workers;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import view.workers.LeaveRequestPopup;
import view.navigation.NavigationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                controller -> new LeaveRequestPopup(realShiftList, this));
    }

    public List<String> computeTimeLock(LocalTime startTime, LocalTime endTime) {
        List<String> returnList = new ArrayList<>();
        while (startTime != endTime){
            returnList.add(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            startTime = startTime.plusHours(1);
        }
        return returnList;
    }

    public void clickedConfirm(LocalDate date, String startTime, String endTime) {
        ShiftEditingHandler.editShiftProposalForLeave(Session.getInstance().getWorker(),
                date, LocalTime.parse(startTime), LocalTime.parse(endTime));
        NavigationManager.getInstance().closePopup("Leave Request");
    }
}
