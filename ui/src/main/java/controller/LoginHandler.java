package controller;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import view.ErrorMessage;
import view.FirstAccessPopup;
import view.HomeScreen;
import view.NavigationManager;

import java.util.Map;

public class LoginHandler {
    public void clickedLogin(String id, String password) {
        try {
            /* verifica che le credenziali inserite siano corrette */
            if (DBMSDaemon.getInstance().checkCredentials(id, password)){
                Session.getInstance().update(DBMSDaemon.getInstance().getWorkerData(id));
                /* verifica che chi sta effettuando l'accesso
                * l'abbia già fatto in precedenza o la sua prima volta. */
                if (DBMSDaemon.getInstance().isFirstAccess(id)){
                    Map<String,String> questions = DBMSDaemon.getInstance().getQuestionsList();
                    NavigationManager.getInstance().createPopup("First Access",
                            controllerClass -> new FirstAccessPopup(questions,this));
                }
                /* Se ha già fatto il primo accesso */
                else {
                    /*apre la schermata home o dell'admin o dell'impiegato */
                    chooseHomeScreen();
                }
            } else {
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage("Impossibile effettuare il login."));
            }
        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
    private void chooseHomeScreen(){
        String screenName;
        if (Session.getInstance().getWorker().getRank() == 'H' ){
            screenName = "Home (Admin)";
        } else {
            screenName = "Home";
        }
        NavigationManager.getInstance().createScreen(screenName,
                controller -> new HomeScreen());


    }
    public void clickedConfirm(String id, String questionID, String answer){
        try {
            DBMSDaemon.getInstance().registerSafetyQuestion(id, questionID, answer);
            NavigationManager.getInstance().closePopup("First Access");
            /*apre la schermata home o dell'admin o dell'impiegato */
            chooseHomeScreen();

        } catch (DBMSException e) {
            e.printStackTrace();
            NavigationManager.getInstance().createPopup("Error Message",
                    controller -> new ErrorMessage(true));
        }
    }
}
