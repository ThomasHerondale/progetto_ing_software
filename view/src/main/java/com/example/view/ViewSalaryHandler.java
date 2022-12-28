package com.example.view;

import entities.Worker;

public class ViewSalaryHandler {
    private Worker worker;
    public ViewSalaryHandler(Worker worker) {
        this.worker = worker;
    }

    public void clickedViewSalary() {
        //getWorkerInfo(worker.getID())
        //mancano le informazioni necessarie per tutti i valori del salario
        NavigationManager.getInstance().createScreen("Salary",
                controller -> new SalaryScreen(worker, this));
    }

    public void clickedBack() {
        if (worker.getRank() == 'H'){
            NavigationManager.getInstance().createScreen("Home (Admin)",
                    controller -> new HomeScreen(worker));
        } else {
            NavigationManager.getInstance().createScreen("Home",
                    controller -> new HomeScreen(worker));
        }

    }
}
