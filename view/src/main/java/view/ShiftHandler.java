package view;

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
            NavigationManager.getInstance().createScreen("View Shifts",
                    controller -> new ViewShiftsScreen(shiftList, this));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
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
