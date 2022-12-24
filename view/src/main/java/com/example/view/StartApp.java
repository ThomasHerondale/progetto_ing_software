package com.example.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class StartApp extends Application{
    @Override
    public void start(Stage primaryStage){
        NavigationManager.getInstance().setPrimaryStage(primaryStage);
        NavigationManager.getInstance().createScreen("Login", controllerClass -> new LoginScreen());
    }
    public static void main(String[] args) {
        launch();
    }
}
