package com.example.view;

import commons.HoursRecap;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;

import java.util.Map;

public class ViewSalaryHandler {
    public void clickedViewSalary() {
        Map<HoursRecap, Double> salaryData;
        try {
            salaryData = DBMSDaemon.getInstance().getWorkerSalaryData(Session.getInstance().getWorker().getId());
            NavigationManager.getInstance().createScreen("Salary",
                    controller -> new SalaryScreen(salaryData, this));
        } catch (Exception ignored) {
            //quando un impiegato è stato appena assunto non può visualizzare la schermata dello stipendio.
        }

    }

    public void clickedBack() {
        if (Session.getInstance().getWorker().getRank() == 'H'){
            NavigationManager.getInstance().createScreen("Home (Admin)",
                    controller -> new HomeScreen());
        } else {
            NavigationManager.getInstance().createScreen("Home",
                    controller -> new HomeScreen());
        }

    }
}
