package com.example.view;

import javafx.application.Application;
import javafx.stage.Stage;
import timer.TimerManager;

/**
 * Questa classe è il punto di ingresso dell'applicazione javafx. Essa effettua un override del metodo
 * {@link Application#start} e utilizza il metodo {@link StartSoftwareWorker#main} per fare partire il tutto.
 */
public class StartSoftwareWorker extends Application{
    /**
     * Mostra la prima schermata del software,
     * ovvero la schermata di login.
     * @param primaryStage è lo stage principale dell'applicazione in cui viene caricata una scena.
     */
    @Override
    public void start(Stage primaryStage){
        //var manager = TimerManager.getInstance();
        //manager.setDebugMode();
        //manager.initialize();
        NavigationManager.getInstance().setPrimaryStage(primaryStage);
        NavigationManager.getInstance().createScreen("Login", controllerClass -> new LoginScreen());
    }

    /**
     * Il metodo che fa partire l'applicazione.
     *
     */
    public static void main(String[] args) {
        launch();
    }
}
