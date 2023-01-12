package controller.workers;

import commons.WorkerStatus;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import view.navigation.NavigationManager;
import view.workers.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static commons.WorkerStatus.*;

public class WorkersRecapHandler {
    List<Worker> workersList;
    Map<String, WorkerStatus> workersStatus;
    public void clickedWorkers(){
        LocalDate currentDate = LocalDate.now();
        try {
            workersList = DBMSDaemon.getInstance().getWorkersList();
            workersStatus = DBMSDaemon.getInstance().getWorkersStatus(currentDate);
            NavigationManager.getInstance().createScreen("Workers Recap",
                    controller -> new WorkersRecapScreen(workersList, workersStatus, this));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
    public void clickedBack(Boolean flag){
        if (flag){
            NavigationManager.getInstance().createScreen("Home (Admin)",
                    controller -> new HomeScreen());
        } else {
            List<Worker> updatedWorkersList;
            Map<String, WorkerStatus> updatedWorkersStatus;
            try {
                updatedWorkersList = DBMSDaemon.getInstance().getWorkersList();
               updatedWorkersStatus = DBMSDaemon.getInstance().getWorkersStatus(LocalDate.now());
                NavigationManager.getInstance().createScreen("Workers Recap",
                        controller -> new WorkersRecapScreen(updatedWorkersList, updatedWorkersStatus, this));
            } catch (DBMSException e) {
                e.printStackTrace();
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage(true));
            }
        }

    }

    public void selectedWorker(Worker worker) {
        Map<String, String> workerInfo;
        try {
            workerInfo = DBMSDaemon.getInstance().getWorkerInfo(worker.getId());
            NavigationManager.getInstance().createScreen("Worker Info",
                    controller -> new WorkerInfoScreen(worker, workerInfo, this));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }

    public List<Worker> clickedSearch(String digitedText, List<Worker> workersList,
                                      Map<String, WorkerStatus> workersStatus, boolean rankA, boolean rankB,
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
