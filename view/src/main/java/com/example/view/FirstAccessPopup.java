package com.example.view;

import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.util.Map;

public class FirstAccessPopup {

    @FXML
    private TextField answerField;

    @FXML
    private Button confirmButton;

    @FXML
    private Label labelDaAzzerare;

    @FXML
    private Text nameLabel;

    @FXML
    private ComboBox<String> questionBox;
    private Map<String, String> questionsList;

    private Worker worker;

    private String questionSelectedID;
    private LoginHandler loginHandler;

    //costruttore
    public FirstAccessPopup(Map<String, String> questionsList, Worker worker, LoginHandler handler) {
        this.worker = worker;
        this.questionsList = questionsList;
        this.loginHandler = handler;
    }

    @FXML
    public void initialize() {
        nameLabel.setText(worker.getFullName());
        questionsList.forEach((chiave, valore) -> questionBox.getItems().add(valore));
        questionSelectedID = "";
        questionBox.setOnAction(this::onQuestionSelected);
    }
    private void onQuestionSelected(ActionEvent event){
        questionsList.forEach((chiave,valore) -> {
            if (questionBox.getValue().equals(valore)){
                questionSelectedID = chiave;
            }
        });
    }
    @FXML
    public void clickConfirm(ActionEvent event) {
        if (!questionSelectedID.isEmpty()){
            loginHandler.clickedConfirm(worker.getId(), questionSelectedID, answerField.getText());
        }
    }
}

