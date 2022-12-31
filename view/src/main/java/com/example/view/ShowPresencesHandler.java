package com.example.view;

import database.DBMSDaemon;

import java.time.LocalDate;

public class ShowPresencesHandler {
    public void clickedPresences() {
        LocalDate currentDate = LocalDate.now();
        //DBMSDaemon.getInstance().getPresencesList(currentDate);
        NavigationManager.getInstance().createScreen("Presence",
                controller -> new PresenceScreen());
    }
}
