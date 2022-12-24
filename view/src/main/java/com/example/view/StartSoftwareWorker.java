package com.example.view;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Questa classe è il punto di ingresso dell'applicazione javafx. Essa effettua un override del metodo
 * {@link Application#start} e utilizza il metodo {@link StartSoftwareWorker#main} per fare partire il tutto.
 */
public class StartSoftwareWorker extends Application{
    /**
     * Mostra la prima schermata del software,
     * ovvero la schermata di login.
     * @param primaryStage è lo stage principale dell'applicazione in cui viene caricato una scena
     */
    @Override
    public void start(Stage primaryStage){
        NavigationManager.getInstance().setPrimaryStage(primaryStage);
        NavigationManager.getInstance().createScreen("Login", controllerClass -> new LoginScreen());
    }

    /**
     * Il metodo che fa partire l'applicazione.
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}
