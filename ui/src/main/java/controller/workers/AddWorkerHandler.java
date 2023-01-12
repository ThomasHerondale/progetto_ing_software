package controller.workers;

import commons.WorkerStatus;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import mail.MailManager;
import ssn.SSNComputer;
import view.navigation.NavigationManager;
import view.workers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddWorkerHandler {

    WorkersRecapScreen workersRecapScreen;
    public void clickedAddWorker(WorkersRecapScreen workersRecapScreen) {
        this.workersRecapScreen = workersRecapScreen;
        long lastID;
        try {
            lastID = (DBMSDaemon.getInstance().getLastid());
            String newID = computeLastID(lastID);
            NavigationManager.getInstance().createScreen("Add Worker",
                    controller -> new AddWorkerScreen(newID, this));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }

    private String computeLastID(long lastID) {
        return String.valueOf(lastID + 1 );
    }
    public void clickedBack(){
        NavigationManager.getInstance().createScreen("Workers Recap",
                    controller -> workersRecapScreen);
    }
    public void clickedBack(Worker worker, LocalDate birthDate, String birthPlace, char sex, String ssn){
        NavigationManager.getInstance().createScreen("Add Worker",
                controller -> new AddWorkerScreen(worker, birthDate, birthPlace, sex, ssn, this));
    }

    public String insertedData(String name, String surname, String birthDate, String birthPlace, char sex) {
        return SSNComputer.computeSSN(name, surname, birthDate, sex, birthPlace);
    }

    public void clickedRecap(String idWorker, String name, String surname, char rank, LocalDate birthDate,
                             String birthPlace, String ssn, String iban, String phone, String email, char sex) {
        Worker worker = new Worker(idWorker, name, surname, rank, phone, email, iban);
        NavigationManager.getInstance().createScreen("New Worker Recap",
                controller -> new NewWorkerRecapScreen(worker, birthDate, birthPlace, sex, ssn, this));
    }

    public void clickedConfirm(Worker worker, LocalDate birthDate, String birthPlace, char sex, String ssn) {
        String password = computePassword();
        List<Worker> workersList;
        Map<String, WorkerStatus> workersStatus;
        try {
            DBMSDaemon.getInstance().createWorker(worker, birthDate, birthPlace, sex, ssn, worker.getRank());
            DBMSDaemon.getInstance().registerPassword(worker.getId(), password);

            workersList = DBMSDaemon.getInstance().getWorkersList();
            workersStatus = DBMSDaemon.getInstance().getWorkersStatus(LocalDate.now());
            MailManager.getInstance().notifyHiring(worker.getEmail(), worker.getFullName(), password);
            NavigationManager.getInstance().createScreen("Workers Recap",
                    controller -> new WorkersRecapScreen(workersList, workersStatus, new WorkersRecapHandler()));
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }

    private String computePassword() {
        String password = "";
        Random random = new Random();

        // Sceglie casualmente 10 caratteri tra lettere e numeri
        for (int i = 0; i < 10; i++) {
            int charType = random.nextInt(3);
            if (charType == 0) {
                // Genera una lettera maiuscola
                char c = (char) (random.nextInt(26) + 'A');
                password += c;
            } else if (charType == 1) {
                // Genera una lettera minuscola
                char c = (char) (random.nextInt(26) + 'a');
                password += c;
            } else {
                // Genera un numero
                char c = (char) (random.nextInt(10) + '0');
                password += c;
            }
        }
        return password;
    }
}
