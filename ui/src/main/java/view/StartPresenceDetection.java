package view;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Questa classe è il punto di ingresso dell'applicazione javafx per la rilevazione delle presenze.
 * Essa effettua un override del metodo {@link Application#start} e utilizza il
 * metodo {@link StartSoftwareWorker#main} per fare partire il tutto.
 */
public class StartPresenceDetection extends Application{
    /**
     * Mostra la prima schermata del software,
     * ovvero la schermata di login.
     * @param primaryStage è lo stage principale dell'applicazione in cui viene caricata una scena.
     */
    @Override
    public void start(Stage primaryStage){
        NavigationManager.getInstance().setPrimaryStage(primaryStage);
        NavigationManager.getInstance().createScreen("Record Presence",
                controllerClass -> new RecordPresenceScreen());
    }

    /**
     * Il metodo che fa partire l'applicazione.
     *
     */
    public static void main(String[] args) {
        launch();
    }
}
