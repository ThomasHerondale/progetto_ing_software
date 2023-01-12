package controller;

import database.DBMSDaemon;
import database.DBMSException;
import view.ErrorMessage;
import view.HomeScreen;
import view.NavigationManager;
import view.PresenceScreen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowPresencesHandler {
    public void clickedPresences() {
        LocalDate currentDate = LocalDate.now();
        List<HashMap<String, String>> presences;
        try {
            presences = DBMSDaemon.getInstance().getPresencesList(currentDate);
            NavigationManager.getInstance().createScreen("Presence",
                    controller -> new PresenceScreen(presences, this));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }

    }
    public void clickedBack() {
        NavigationManager.getInstance().createScreen("Home (Admin)",
                controller -> new HomeScreen());
    }

    public List<HashMap<String, String>> clickedSearch(String digitedText, List<HashMap<String, String>> presences,
                                                       Boolean rankA, Boolean rankB,
                                                       Boolean rankC, Boolean rankD, Boolean rankH) {
        List<HashMap<String, String>> presencesFilter = new ArrayList<>();
        String shiftRank = "shiftRank";
        String workerName = "workerName";
        String workerSurname = "workerSurname";
        if (digitedText.equals("")){
            for (HashMap<String, String> presence : presences) {
                if (rankA) {
                    if (presence.get(shiftRank).equals("A")) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankB) {
                    if (presence.get(shiftRank).equals("B")) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankC) {
                    if (presence.get(shiftRank).equals("C")) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankD) {
                    if (presence.get(shiftRank).equals("D")) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankH) {
                    if (presence.get(shiftRank).equals("H")){
                        presencesFilter.add(presence);
                    }
                }
            }
        }
        else {
            for (HashMap<String, String> presence : presences) {
                if (rankA) {
                    if (presence.get(shiftRank).equals("A") && (
                            presence.get("ID").contains(digitedText) ||
                                    presence.get(workerName).contains(digitedText) ||
                                    presence.get(workerSurname).contains(digitedText))) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankB) {
                    if (presence.get(shiftRank).equals("B") && (
                            presence.get("ID").contains(digitedText) ||
                                    presence.get(workerName).contains(digitedText) ||
                                    presence.get(workerSurname).contains(digitedText))) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankC) {
                    if (presence.get(shiftRank).equals("C") && (
                            presence.get("ID").contains(digitedText) ||
                                    presence.get(workerName).contains(digitedText) ||
                                    presence.get(workerSurname).contains(digitedText))) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankD) {
                    if (presence.get(shiftRank).equals("D") && (
                            presence.get("ID").contains(digitedText) ||
                                    presence.get(workerName).contains(digitedText) ||
                                    presence.get(workerSurname).contains(digitedText))) {
                        presencesFilter.add(presence);
                    }
                }
                if (rankH) {
                    if (presence.get(shiftRank).equals("H") && (
                            presence.get("ID").contains(digitedText) ||
                                    presence.get(workerName).contains(digitedText) ||
                                    presence.get(workerSurname).contains(digitedText))) {
                        presencesFilter.add(presence);
                    }
                }
            }
        }
        return presencesFilter;
    }
}
