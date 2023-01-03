package com.example.view;

import commons.WorkerStatus;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static commons.WorkerStatus.*;

public class WorkersRecapHandler {
    List<Worker> workersList;
    Map<String, WorkerStatus> workersStatus;
    public void clickedWorkers(){
        LocalDate currentDate = LocalDate.now();
        try {
            workersList = DBMSDaemon.getInstance().getWorkersList();
            workersStatus = DBMSDaemon.getInstance().getWorkersStatus(currentDate);
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
        NavigationManager.getInstance().createScreen("Workers Recap",
                controller -> new WorkersRecapScreen(workersList, workersStatus, this));
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
        var workersStatusCopy = new HashMap<>(workersStatus);

        /* Costruisci la lista dei filtri del livello */
        List<Character> rankFilters = new ArrayList<>();
        if (rankA)
            rankFilters.add('A');
        if (rankB)
            rankFilters.add('B');
        if (rankC)
            rankFilters.add('C');
        if (rankD)
            rankFilters.add('D');
        if (rankH)
            rankFilters.add('H');

        /* Filtra i dipendenti in base al livello */

        /* Costruisci la lista dei filtri dello status */
        List<WorkerStatus> statusFilters = new ArrayList<>();
        if (workingStatus)
            statusFilters.add(WORKING);
        if (freeStatus)
            statusFilters.add(FREE);
        if (onHolidayStatus)
            statusFilters.add(ON_HOLIDAY);
        if (illStatus)
            statusFilters.add(ILL);
        if (strikingStatus)
            statusFilters.add(STRIKING);
        if (parentalLeaveStatus)
            statusFilters.add(PARENTAL_LEAVE);

        /* Filtra i dipendenti in base allo stato */
        workersStatusCopy
                .entrySet()
                .removeIf(entry -> !statusFilters.contains(entry.getValue()));

        /* Costruisci il filtro in base al testo cercato */
        Predicate<Worker> searchFilter = digitedText.isEmpty() ?
                worker -> true
                :
                worker -> worker.getFullName().concat(worker.getId()).contains(digitedText);

        List<Worker> list = workersList
                .stream()
                .filter(worker -> rankFilters.contains(worker.getRank()))
                .filter(worker -> workersStatusCopy.containsKey(worker.getId()))
                .filter(searchFilter)
                .toList();

        return new ArrayList<>(list);
    }

    /*public List<Worker> clickedSearch(String digitedText, List<Worker> workersList, boolean rankA, boolean rankB,
                                      boolean rankC, boolean rankD, boolean rankH, boolean workingStatus,
                                      boolean freeStatus, boolean onHolidayStatus, boolean illStatus,
                                      boolean strikingStatus, boolean parentalLeaveStatus) {
        List<Worker> workersFilter = new ArrayList<>();
        if (digitedText.equals("")){
            for (Worker worker : workersList) {
                Boolean flag = false;
                if (rankA && worker.getRank() == 'A') {
                    if (extracted(workingStatus, freeStatus, onHolidayStatus, illStatus, strikingStatus, parentalLeaveStatus, worker)){
                        workersFilter.add(worker);
                    }

                }
                if (rankB && worker.getRank() == 'B') {
                    if(workingStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.WORKING)){
                        workersFilter.add(worker);
                    }
                    if(freeStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.FREE)){
                        workersFilter.add(worker);
                    }
                    if(onHolidayStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.ON_HOLIDAY)){
                        workersFilter.add(worker);
                    }
                    if(illStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.ILL)){
                        workersFilter.add(worker);
                    }
                    if(strikingStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.STRIKING)){
                        workersFilter.add(worker);
                    }
                    if(parentalLeaveStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.PARENTAL_LEAVE)){
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
                *//*
                if (workingStatus){
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.WORKING)){
                        workersFilter.add(worker);
                    }
                }
                if (freeStatus){
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.FREE)){
                        workersFilter.add(worker);
                    }
                }
                if (onHolidayStatus){
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.ON_HOLIDAY)){
                        workersFilter.add(worker);
                    }
                }
                if (illStatus){
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.ILL)){
                        workersFilter.add(worker);
                    }
                }
                if (strikingStatus){
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.STRIKING)){
                        workersFilter.add(worker);
                    }
                }
                if (parentalLeaveStatus){
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.PARENTAL_LEAVE)){
                        workersFilter.add(worker);
                    }
                }

                 *//*
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
                *//*
                if (workingStatus) {
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.WORKING) && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText) )){
                        workersFilter.add(worker);
                    }
                }
                if (freeStatus) {
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.FREE) && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText) )){
                        workersFilter.add(worker);
                    }
                }
                if (onHolidayStatus) {
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.ON_HOLIDAY) && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText) )){
                        workersFilter.add(worker);
                    }
                }
                if (illStatus) {
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.ILL) && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText) )){
                        workersFilter.add(worker);
                    }
                }
                if (strikingStatus) {
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.STRIKING) && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText) )){
                        workersFilter.add(worker);
                    }
                }
                if (parentalLeaveStatus) {
                    if (workersStatus.get(worker.getId()).equals(WorkerStatus.PARENTAL_LEAVE) && (
                            worker.getId().contains(digitedText) ||
                                    worker.getName().contains(digitedText) ||
                                    worker.getSurname().contains(digitedText) )){
                        workersFilter.add(worker);
                    }
                }

                 *//*
            }
        }
        return workersFilter;
    }*/

    private boolean extracted(boolean workingStatus, boolean freeStatus, boolean onHolidayStatus,
                              boolean illStatus, boolean strikingStatus, boolean parentalLeaveStatus, Worker worker) {
        if(workingStatus && workersStatus.get(worker.getId()).equals(WORKING)){
            return true;
        }
        if(freeStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.FREE)){
            return true;
        }
        if(onHolidayStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.ON_HOLIDAY)){
            return true;
        }
        if(illStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.ILL)){
            return true;
        }
        if(strikingStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.STRIKING)){
            return true;
        }
        if(parentalLeaveStatus && workersStatus.get(worker.getId()).equals(WorkerStatus.PARENTAL_LEAVE)){
            return true;
        }
        return false;
    }
}
