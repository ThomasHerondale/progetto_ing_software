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
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        //mancano le informazioni necessarie per tutti i valori del salario
        NavigationManager.getInstance().createScreen("Salary",
                controller -> new SalaryScreen(salaryData, this));
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
