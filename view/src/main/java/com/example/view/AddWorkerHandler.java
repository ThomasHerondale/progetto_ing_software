package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import ssn.SSNComputer;

import java.time.LocalDate;

public class AddWorkerHandler {

    WorkersRecapScreen workersRecapScreen;
    public void clickedAddWorker(WorkersRecapScreen workersRecapScreen) {
        this.workersRecapScreen = workersRecapScreen;
        long lastID;
        try {
            lastID = (DBMSDaemon.getInstance().getLastid());
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        String newID = computeLastID(lastID);
        NavigationManager.getInstance().createScreen("Add Worker",
                controller -> new AddWorkerScreen(newID, this));
    }

    private String computeLastID(long lastID) {
        return String.valueOf(lastID + 1 );
    }
    public void clickedBack(){
        NavigationManager.getInstance().createScreen("Workers Recap",
                controller -> workersRecapScreen);
    }

    public String insertedData(String name, String surname, String birthDate, String birthPlace, char sex) {
        return SSNComputer.computeSSN(name, surname, birthDate, sex, birthPlace);
    }

    public void clickedRecap(String idWorker, String name, String surname, char rank, LocalDate birthDate,
                             String birthPlace, String ssn, String iban, String phone, String email, char sex) {
        Worker worker = new Worker(idWorker, name, surname, rank, phone, email, iban);
        NavigationManager.getInstance().createScreen("New Worker Recap",
                controller -> new NewWorkerRecapScreen(worker, birthDate, birthPlace, sex, ssn));
    }
}
