package com.example.view;

import commons.Session;

public class ViewSalaryHandler {
    public void clickedViewSalary() {
        //getWorkerInfo(worker.getID())
        //mancano le informazioni necessarie per tutti i valori del salario
        NavigationManager.getInstance().createScreen("Salary",
                controller -> new SalaryScreen(this));
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
