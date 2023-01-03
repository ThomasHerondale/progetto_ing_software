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
}
