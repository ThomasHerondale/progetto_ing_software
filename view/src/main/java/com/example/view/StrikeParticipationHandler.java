package com.example.view;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class StrikeParticipationHandler {
    public void clickedStrikes() {
        List<HashMap<String, String>> authorizedStrikes;
        try {
            authorizedStrikes = DBMSDaemon.getInstance().
                    getAuthorizedStrikes(Session.getInstance().getWorker().getRank());
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createPopup("Strike Participation",
                controller-> new StrikeParticipationPopup(authorizedStrikes, this));
    }

    public void clickedParticipate(String strikeName, LocalDate strikeDate) {
        try {
            DBMSDaemon.getInstance().setStrikeParticipation(Session.getInstance().getWorker().getId(),
                   strikeName, strikeDate);
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
        //TODO: chiamare la handler di modifica turnazione
        NavigationManager.getInstance().closePopup("Strike Participation");
    }
}
