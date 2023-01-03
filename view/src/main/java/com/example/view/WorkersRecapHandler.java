package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkersRecapHandler {
    List<Worker> workersList;
    public void clickedWorkers(){
        try {
            workersList = DBMSDaemon.getInstance().getWorkersList();
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createScreen("Workers Recap",
                controller -> new WorkersRecapScreen(workersList, this));
    }
    public void clickedBack(){
        NavigationManager.getInstance().createScreen("Home (Admin)",
                controller -> new HomeScreen());
    }

    public void selectedWorker(Worker worker) {
        //TODO:
        //NavigationManager.getInstance().createScreen();
        System.out.println("prova");
    }

    public List<Worker> clickedSearch(String digitedText, List<Worker> workersList, boolean rankA, boolean rankB,
                                      boolean rankC, boolean rankD, boolean rankH, boolean workingStatus,
                                      boolean freeStatus, boolean onHolidayStatus, boolean illStatus,
                                      boolean strikingStatus, boolean parentalLeaveStatus) {
        List<Worker> workersFilter = new ArrayList<>();
        if (digitedText.equals("")){
            for (Worker worker : workersList) {
                if (rankA) {
                    if (worker.getRank() == 'A') {
                        workersFilter.add(worker);
                    }
                }
                if (rankB) {
                    if (worker.getRank() == 'B') {
                        workersFilter.add(worker);
                    }
                }
                if (rankC) {
                    if (worker.getRank() == 'C') {
                        workersFilter.add(worker);
                    }
                }
                if (rankD) {
                    if (worker.getRank() == 'D') {
                        workersFilter.add(worker);
                    }
                }
                if (rankH) {
                    if (worker.getRank() == 'H') {
                        workersFilter.add(worker);
                    }
                }
                if (workingStatus){
                    //TODO:
                }
                if (freeStatus){
                    //TODO:
                }
                if (onHolidayStatus){
                    //TODO:
                }
                if (illStatus){
                    //TODO:
                }
                if (strikingStatus){
                    //TODO:
                }
                if (parentalLeaveStatus){
                    //TODO:
                }
            }
        } else {
            for (Worker worker : workersList) {
                if (rankA) {
                    if (worker.getRank() == 'A' && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText))) {
                        workersFilter.add(worker);
                    }
                }
                if (rankB) {
                    if (worker.getRank() == 'B' && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText))) {
                        workersFilter.add(worker);
                    }
                }
                if (rankC) {
                    if (worker.getRank() == 'C' && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText))) {
                        workersFilter.add(worker);
                    }
                }
                if (rankD) {
                    if (worker.getRank() == 'D' && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText))) {
                        workersFilter.add(worker);
                    }
                }
                if (rankH) {
                    if (worker.getRank() == 'H' && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText))) {
                        workersFilter.add(worker);
                    }
                }
                if (workingStatus) {
                    //TODO:
                }
                if (freeStatus) {
                    //TODO:
                }
                if (onHolidayStatus) {
                    //TODO:
                }
                if (illStatus) {
                    //TODO:
                }
                if (strikingStatus) {
                    //TODO:
                }
                if (parentalLeaveStatus) {
                    //TODO:
                }
            }
        }
        return workersFilter;
    }
}
