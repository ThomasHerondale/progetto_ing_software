package com.example.view;

import commons.Session;

public class ShiftHandler {

    public void clickedShowShifts() {
        NavigationManager.getInstance().createScreen("View Shifts",
                controller -> new ViewShiftsScreen(this));
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
