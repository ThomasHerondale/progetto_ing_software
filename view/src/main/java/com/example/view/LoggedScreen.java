package com.example.view;

import entities.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoggedScreen {
    private final Worker worker;
    @FXML
    private Label initialsLabel;

    protected LoggedScreen(Worker worker) {
        this.worker = worker;
    }

    @FXML
    public void initialize(){
        String nameInit = String.valueOf(worker.getName().charAt(0));
        String surnameInit = String.valueOf(worker.getSurname().charAt(0));
        initialsLabel.setText(nameInit + " " + surnameInit);
    }

    public Worker getWorker() {
        return worker;
    }
}
