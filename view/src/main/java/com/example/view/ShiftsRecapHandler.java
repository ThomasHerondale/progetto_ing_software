package com.example.view;

public class ShiftsRecapHandler {
    public void clickedShiftsRecap() {
        //fa qualcosa e poi...
        //TODO:
        NavigationManager.getInstance().createScreen("Shifts Recap",
                controller -> new ShiftsRecapScreen());
    }
}
