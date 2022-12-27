package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import entities.Worker;
import java.util.Map;

public class LoginHandler {


    public void clickedLogin(String id, String password) {
        //TODO:
        try {
            /* verifica che le credenziali inserite siano corrette */
            if (DBMSDaemon.getInstance().checkCredentials(id, password)){
                Worker worker = DBMSDaemon.getInstance().getWorkerData(id);
                /* verifica che chi sta effettuando l'accesso
                * l'abbia già fatto in precedenza o la sua prima volta. */
                if (DBMSDaemon.getInstance().isFirstAccess(id)){
                    Map<String,String> questions = DBMSDaemon.getInstance().getQuestionsList();
                    NavigationManager.getInstance().createPopup("Primo Accesso",
                            controllerClass -> new FirstAccessPopup(questions, worker, this));
                }
                /* Se ha già fatto il primo accesso */
                else {
                    /* Il seguente if scopre chi sta effettuando il login,
                    * se è un Amministrativo o un generico Impiegato. */
                    String screenName;
                    if (DBMSDaemon.getInstance().getWorkerRank(id).equals('H')){
                        screenName = "Home (Admin)";
                    } else {
                        screenName = "Home";
                    }
                    NavigationManager.getInstance().createScreen(screenName,
                            controller -> new HomeScreen(worker));
                }
            } else {
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage("Impossibile effettuare il login."));
            }

        } catch (DBMSException e) {
            //TODO:
        }
        /*
        DBMSDeamon è una variabile d'istanza?
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, password)) {
                // dobbiamo prelevare gli elementi di worker dal dbms con un metodo (forse getWorkerData(id))
                Worker worker = new Worker(id, "name", "surname", "phone", "email", "iban");
                this.id = id;
                if (DBMSDaemon.getInstance().isFirstAccess(id)) {
                    //HashMap<String, String> questionsList = DBMSDaemon.getQuestionsList();
        HashMap<String, String> questionsList = new HashMap<>();
        questionsList.put("1", "Ti piace la banana?");
        questionsList.put("22", "Ti piace la mela?");
        questionsList.put("3", "Ti piace la pera?");
        Worker worker2 = new Worker("1234567", "alessandro", "borgese", "3331234567", "ale@gmail.com", "IT000");
        NavigationManager.getInstance().createPopup("Primo Accesso", controllerClass -> new FirstAccessPopup(questionsList, worker2, this));
        //NavigationManager.getInstance().createScreen("Home (Admin)", controllerClass -> new HomeScreen());
        //}
        //}
        //}
    //}
       // } catch (DBMSException e) {
         //   throw new RuntimeException(e);
      //  }
        */
    }
    public void clickedConfirm(String question, String answer){
        //TODO:
        //DBMSDaemon.registerSafetyQuestion(id, question, answer);
        NavigationManager.getInstance().closePopup("Primo Accesso");
        System.out.println("Hai chiuso e fatto tutto");
    }
}
