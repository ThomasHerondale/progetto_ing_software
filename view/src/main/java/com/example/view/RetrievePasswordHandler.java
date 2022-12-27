package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import mail.MailManager;

import java.util.Random;

public class RetrievePasswordHandler {
    private String id;
    public void clickedConfirm(String id){

        try {
            DBMSDaemon.getInstance().getPasswordRetrievalInfo(id);
        } catch (DBMSException e){
            //TODO:
        }
        this.id = id;
        //continua...
    }
    public void clickedSend(String answer){
        try {
            if (DBMSDaemon.getInstance().checkAnswer(id, answer)){
                String newPassword = generatePassword();
                DBMSDaemon.getInstance().registerPassword(id, newPassword);
                DBMSDaemon.getInstance().getMailData(id);
                MailManager.getInstance().notifyNewPassword();
                NavigationManager.getInstance().closePopup("Safety Question");
            } else {
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage("La risposta inserita è errata."));
            }
        } catch (DBMSException e) {
            //TODO:
        }
    }
    private String generatePassword(){
        Random random = new Random();
        String pass = "";
        for (int i = 0; i < 8; i++) {
            pass += random.nextInt(10);
        }
        return pass;
    }
    public void clickedRetrievePassword(){
        NavigationManager.getInstance().createPopup("Retrieve Password",
                controllerClass -> new RetrievePasswordPopup(this));
    }
}
