package view;

import commons.Period;
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
                    getAuthorizedStrikes(Session.getInstance().getWorker().getId(),
                            Session.getInstance().getWorker().getRank());
            NavigationManager.getInstance().createPopup("Strike Participation",
                    controller-> new StrikeParticipationPopup(authorizedStrikes, this));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }

    public void clickedParticipate(String strikeName, LocalDate strikeDate) {
        try {
            DBMSDaemon.getInstance().setStrikeParticipation(Session.getInstance().getWorker().getId(),
                   strikeName, strikeDate);
            ShiftEditingHandler.editShiftProposal(Session.getInstance().getWorker(), new Period(strikeDate, strikeDate));
            NavigationManager.getInstance().closePopup("Strike Participation");
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
}
