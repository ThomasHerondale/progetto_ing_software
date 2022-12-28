package com.example.view;

import entities.Worker;

public class ViewSalaryHandler {
    private Worker worker;
    public ViewSalaryHandler(Worker worker) {
        this.worker = worker;
    }

    public void clickedViewSalary() {
        //getWorkerInfo(worker.getID())
        NavigationManager.getInstance().createScreen("Salary",
                controller -> new SalaryScreen(worker, this));
    }

    public void clickedBack() {
        if (worker.getRank().equals("H")){
            NavigationManager.getInstance().createScreen("Home (Admin)",
                    controller -> new HomeScreen(worker));
        } else {
            NavigationManager.getInstance().createScreen("Home",
                    controller -> new HomeScreen(worker));
        }

    }
}
