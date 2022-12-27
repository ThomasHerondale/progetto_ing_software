package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;
import mail.MailManager;

import java.util.Map;
import java.util.Random;

public class RetrievePasswordHandler {
    private String id;
    public void clickedConfirm(String id){
        try {
            Map<String,String> passwordRetrievalInfo = DBMSDaemon.getInstance().getPasswordRetrievalInfo(id);
            if (!passwordRetrievalInfo.isEmpty()){
                if (passwordRetrievalInfo.get("firstAccessFlag").equals("1")){
                    NavigationManager.getInstance().createPopup("Safety Question",
                            controller -> new SafetyQuestionPopup(passwordRetrievalInfo.get("question"), this));
                    this.id = id;
                } else {
                    NavigationManager.getInstance().createPopup("Error Message",
                            controller -> new ErrorMessage("Non è possibile recuperare la password se non " +
                                    "è stato ancora effettuato il primo accesso." +
                                    "\nControllare la propria casella di posta" +
                                    " dove è specificata la password per il primo accesso."));
                }


            } else {
                NavigationManager.getInstance().createPopup("Error Message",
                        controller -> new ErrorMessage("La matricola inserita non esiste."));
            }
        } catch (DBMSException e) {
            throw new RuntimeException(e);
        }
    }
    public void clickedSend(String answer){
        try {
            if (DBMSDaemon.getInstance().checkAnswer(id, answer)){
                String newPassword = generatePassword();
                DBMSDaemon.getInstance().registerPassword(id, newPassword);
                Map<String, String> mailData = DBMSDaemon.getInstance().getMailData(id);
                String fullName = mailData.get("name") + " " + mailData.get("surname");
                MailManager.getInstance().notifyNewPassword(mailData.get("email"), fullName, newPassword);
                NavigationManager.getInstance().closePopup("Safety Question");
                NavigationManager.getInstance().closePopup("Retrieve Password");
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
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            pass.append(random.nextInt(10));
        }
        return pass.toString();
    }
    public void clickedRetrievePassword(){
        NavigationManager.getInstance().createPopup("Retrieve Password",
                controllerClass -> new RetrievePasswordPopup(this));
    }
}
