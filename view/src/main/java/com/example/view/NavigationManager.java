package com.example.view;

import commons.Counters;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NavigationManager {

    private static NavigationManager instance;
    private Stage primaryStage;
    private Stage accountInfoStage = new Stage();

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {

        this.primaryStage = primaryStage;
        //aggiunta forse errata
        this.stagesPopup = new HashMap<>();
    }
    private static final Map<String, String> SCREEN_MAP = new HashMap<>();
    static {
        //TODO:
        SCREEN_MAP.put("Login", "FXML/LoginScreen.fxml");
        SCREEN_MAP.put("Home (Admin)", "FXML/HomeScreen_Admin.fxml");
        SCREEN_MAP.put("First Access", "FXML/FirstAccessPopup.fxml");
        SCREEN_MAP.put("Home", "FXML/HomeScreen_Employee.fxml");
        SCREEN_MAP.put("Retrieve Password", "FXML/RetrievePasswordPopup.fxml");
        SCREEN_MAP.put("Safety Question", "FXML/SafetyQuestionPopup.fxml");
        SCREEN_MAP.put("Info Account", "FXML/AccountInfoScreen.fxml");
        SCREEN_MAP.put("Success", "FXML/SuccessPopup.fxml");
        SCREEN_MAP.put("Edit", "FXML/EditPopup.fxml");
        SCREEN_MAP.put("Error Message","FXML/ErrorMessage.fxml");
        SCREEN_MAP.put("Confirm","FXML/ConfirmPopup.fxml");
        SCREEN_MAP.put("Salary","FXML/SalaryScreen.fxml");
        SCREEN_MAP.put("View Shifts", "FXML/ViewShiftsScreen.fxml");
        SCREEN_MAP.put("Shift Info", "FXML/ShiftInfoPopup.fxml");
        SCREEN_MAP.put("Presence", "FXML/PresenceScreen.fxml");
        SCREEN_MAP.put("Insert Presence", "FXML/InsertPresencePopup.fxml");
        SCREEN_MAP.put("Workers Recap", "FXML/WorkersRecapScreen.fxml");
        //...
    }
    private HashMap<String, Stage> stagesPopup;

    private void showScene(String title, Scene scene, boolean isScene, Stage stage){
        stage.setTitle(title);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        if (!isScene) { // se è un popup (identificabile dal flag impostato a false)
            stage.initModality(Modality.APPLICATION_MODAL); //impedisce di interagire con la finestra sottostante
        }
        stage.show();
    }
    public void createScreen(String screenName, Callback<Class<?>,Object> controllerFactory)  {
        String fxmlFile = SCREEN_MAP.get(screenName);
        assert fxmlFile != null : "Il file fxml non esiste";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            showScene(screenName, scene, true, primaryStage);
        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void createPopup(String popupName, Callback<Class<?>, Object> controllerFactory){
        String fxmlFile = SCREEN_MAP.get(popupName);
        assert fxmlFile != null : "Il file fxml non esiste";
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            Scene scene = new Scene(root);
            showScene(popupName, scene, false, newStage);
            stagesPopup.put(popupName, newStage);

            if (popupName.equals("Success")){
                /* Ignora l'evento di chiusura della finestra */
                newStage.setOnCloseRequest(Event::consume);
            } else {
                /* per chiudere con la X */
                newStage.setOnCloseRequest(event -> closePopup(popupName));
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void closePopup(String currentPopupName) {
        stagesPopup.get(currentPopupName).close();
        stagesPopup.remove(currentPopupName);
    }

    public void openAccountInfoScreen(Counters workerCounters, AccountInfoHandler accountInfoHandler) {
        String popupName = "Info Account";
        String fxmlFile = SCREEN_MAP.get(popupName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.setControllerFactory(controller ->
                    new AccountInfoScreen(workerCounters, accountInfoHandler));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            showScene(popupName, scene, true, accountInfoStage);

            /* per chiudere con la X */
            accountInfoStage.setOnCloseRequest(event -> closeAccountInfoScreen());

        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        primaryStage.hide();
    }

    public void closeAccountInfoScreen() {
        primaryStage.show();
        accountInfoStage.close();
    }
}

