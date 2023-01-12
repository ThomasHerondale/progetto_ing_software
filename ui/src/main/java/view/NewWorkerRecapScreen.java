package view;

import controller.AccountInfoHandler;
import controller.AddWorkerHandler;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NewWorkerRecapScreen extends LoggedScreen {

    @FXML
    private Label IBANLabel;

    @FXML
    private Label IDLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label birthPlaceLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Label emailLabel;

    @FXML
    private Label fullNameLabel;


    @FXML
    private Label phoneLabel;


    @FXML
    private Label rankLabel;

    @FXML
    private Label sexLabel;

    @FXML
    private Label ssnLabel;

    private Worker worker;
    private LocalDate birthDate;
    private String birthPlace;
    private char sex;
    private String ssn;
    private AddWorkerHandler addWorkerHandler;

    public NewWorkerRecapScreen(Worker worker, LocalDate birthDate, String birthPlace, char sex,
                                String ssn, AddWorkerHandler addWorkerHandler){
        this.addWorkerHandler = addWorkerHandler;
        this.worker = worker;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
        this.ssn = ssn;
    }
    @FXML
    public void initialize(){
        super.initialize();
        IDLabel.setText(worker.getId());
        fullNameLabel.setText(worker.getFullName());
        birthDateLabel.setText(birthDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        birthPlaceLabel.setText(birthPlace);
        sexLabel.setText(String.valueOf(sex));
        ssnLabel.setText(ssn);
        rankLabel.setText(String.valueOf(worker.getRank()));
        if (worker.getRank() == 'H'){
            rankLabel.setText("Admin");
        }
        IBANLabel.setText(worker.getIban());
        phoneLabel.setText(worker.getPhone());
        emailLabel.setText(worker.getEmail());
    }

    @FXML
    void clickBack(ActionEvent event) {
        addWorkerHandler.clickedBack(worker, birthDate, birthPlace, sex, ssn);
    }

    @FXML
    void clickConfirm(ActionEvent event) {
        addWorkerHandler.clickedConfirm(worker, birthDate, birthPlace, sex, ssn);
    }

    @FXML
    void clickProfile(MouseEvent event) {
        AccountInfoHandler accountInfoHandler = new AccountInfoHandler();
        accountInfoHandler.clickedProfile();
    }

}
