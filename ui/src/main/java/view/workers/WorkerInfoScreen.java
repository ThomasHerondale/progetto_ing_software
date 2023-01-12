package view.workers;

import controller.workers.AccountInfoHandler;
import controller.workers.EnableParentalLeaveHandler;
import controller.workers.WorkerInfoHandler;
import controller.workers.WorkersRecapHandler;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WorkerInfoScreen extends LoggedScreen{
    @FXML
    private Label IBANLabel;

    @FXML
    private Label IDLabel;

    @FXML
    private Label autoExitCountLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label birthPlaceLabel;

    @FXML
    private Label delayCountLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Button enableParentalLeaveButton;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label holidayCountLabel;


    @FXML
    private Label parentalLeaveLabel;

    @FXML
    private Label phoneLabel;


    @FXML
    private Button promoteButton;

    @FXML
    private Label rankLabel;

    @FXML
    private Button removeButton;

    @FXML
    private Label sexLabel;

    @FXML
    private Label ssnLabel;

    private WorkersRecapHandler workersRecapHandler;
    private Worker viewedWorker;
    private Map<String, String> workerInfo;
    private WorkerInfoHandler workerInfoHandler;

    public WorkerInfoScreen(Worker worker, Map<String, String> workerInfo, WorkersRecapHandler workersRecapHandler){
        this.workersRecapHandler = workersRecapHandler;
        this.viewedWorker = worker;
        this.workerInfo = workerInfo;
        workerInfoHandler = new WorkerInfoHandler();
    }

    @FXML
    public void initialize(){
        super.initialize();
        IDLabel.setText(viewedWorker.getId());
        fullNameLabel.setText(viewedWorker.getFullName());
        birthDateLabel.setText(LocalDate.parse(workerInfo.get("birthdate")).
                format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        birthPlaceLabel.setText(workerInfo.get("birthplace"));
        sexLabel.setText(workerInfo.get("sex"));
        ssnLabel.setText(workerInfo.get("SSN"));
        rankLabel.setText(String.valueOf(viewedWorker.getRank()));
        if (viewedWorker.getRank() == 'H'){
            rankLabel.setText("Admin");
        }
        if (viewedWorker.getRank() == 'H' || viewedWorker.getRank() == 'A'){
            promoteButton.setDisable(true);
        }
        IBANLabel.setText(viewedWorker.getIban());
        phoneLabel.setText(viewedWorker.getPhone());
        emailLabel.setText(viewedWorker.getEmail());
        autoExitCountLabel.setText(workerInfo.get("autoExitCount"));
        delayCountLabel.setText(workerInfo.get("delayCount"));
        holidayCountLabel.setText(workerInfo.get("holidayCount"));
        parentalLeaveLabel.setText(workerInfo.get("availabilityParentalLeave"));
    }


    @FXML
    public void clickBack(ActionEvent event) {
        workersRecapHandler.clickedBack(false);
    }

    @FXML
    public void clickEnableParentalLeave(ActionEvent event) {
        EnableParentalLeaveHandler enableParentalLeaveHandler = new EnableParentalLeaveHandler();
        enableParentalLeaveHandler.clickedEnableParentalLeave(viewedWorker);
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        AccountInfoHandler accountInfoHandler = new AccountInfoHandler();
        accountInfoHandler.clickedProfile();
    }

    @FXML
    public void clickPromote(ActionEvent event) {
        workerInfoHandler.clickedPromote(viewedWorker);
    }

    @FXML
    public void clickRemove(ActionEvent event) {
        workerInfoHandler.clickedRemove(viewedWorker);
    }

}

