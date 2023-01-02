package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowPresencesHandler {
    public void clickedPresences() {
        LocalDate currentDate = LocalDate.now();
        List<HashMap<String, String>> presences;
        try {
            presences = DBMSDaemon.getInstance().getPresencesList(currentDate);
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createScreen("Presence",
                controller -> new PresenceScreen(presences));
    }
}
