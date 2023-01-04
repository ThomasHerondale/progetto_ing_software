package com.example.view;

import database.DBMSDaemon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import ssn.SSNComputer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AddWorkerScreen extends LoggedScreen{

    @FXML
    private TextField IBANField;

    @FXML
    private Label IDLabel;

    @FXML
    private RadioButton RankAdmin;

    @FXML
    private Button backButton;

    @FXML
    private DatePicker birthDateCalendar;

    @FXML
    private TextField birthPlaceField;

    @FXML
    private RadioButton femaleButton;

    @FXML
    private ToggleGroup group1;

    @FXML
    private ToggleGroup group2;

    @FXML
    private TextField mailField;

    @FXML
    private RadioButton maleButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;
    @FXML
    private RadioButton rankA;

    @FXML
    private RadioButton rankB;

    @FXML
    private RadioButton rankC;

    @FXML
    private RadioButton rankD;

    @FXML
    private Button recapButton;

    @FXML
    private TextField ssnField;

    @FXML
    private TextField surnameField;

    private String idWorker;

    private AddWorkerHandler addWorkerHandler;

    public AddWorkerScreen(String id, AddWorkerHandler addWorkerHandler){
        this.idWorker = id;
        this.addWorkerHandler = addWorkerHandler;
    }
    @FXML
    public void initialize(){
        super.initialize();
        IDLabel.setText(idWorker);
    }

    @FXML
    public void clickBack(ActionEvent event) {
        addWorkerHandler.clickedBack();
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        AccountInfoHandler accountInfoHandler = new AccountInfoHandler();
        accountInfoHandler.clickedProfile();
    }

    @FXML
    public void clickRecap(ActionEvent event){
        if (group1.getSelectedToggle() == null || nameField.getText().isEmpty() ||
                surnameField.getText().isEmpty() || birthPlaceField.getText().isEmpty() ||
                birthDateCalendar.getValue() == null || ssnField.getText().isEmpty() ||
                group2.getSelectedToggle()== null || IBANField.getText().isEmpty() ||
                phoneField.getText().isEmpty() || mailField.getText().isEmpty()){
            ;
        } else {
            String name = nameField.getText();
            String surname = surnameField.getText();
            LocalDate birthDate = birthDateCalendar.getValue();
            String birthPlace = birthPlaceField.getText();
            String ssn = ssnField.getText();
            String iban = IBANField.getText();
            String phone = phoneField.getText();
            String email = mailField.getText();
            char sex;
            if (group1.getSelectedToggle() == maleButton){
                sex = 'M';
            } else {
                sex = 'F';
            }
            char rank;
            if (group2.getSelectedToggle() == rankA){
                rank = 'A';
            } else if (group2.getSelectedToggle() == rankB) {
                rank = 'B';
            } else if (group2.getSelectedToggle() == rankC) {
                rank = 'C';
            } else if (group2.getSelectedToggle() == rankD) {
                rank = 'D';
            } else {
                rank = 'H';
            }
            addWorkerHandler.clickedRecap(idWorker, name, surname,rank, birthDate, birthPlace, ssn, iban, phone, email, sex);
        }
    }

    @FXML
    public void insertData(MouseEvent event) {
        if (group1.getSelectedToggle() == null || nameField.getText().isEmpty() ||
                surnameField.getText().isEmpty() || birthPlaceField.getText().isEmpty() ||
                birthDateCalendar.getValue() == null){
            ;
        } else {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String birthDate = birthDateCalendar.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String birthPlace = birthPlaceField.getText();
            char sex;
            if (group1.getSelectedToggle() == maleButton){
                sex = 'M';
            } else {
                sex = 'F';
            }
            ssnField.setText(addWorkerHandler.insertedData(name, surname, birthDate, birthPlace, sex));
        }
    }
}
