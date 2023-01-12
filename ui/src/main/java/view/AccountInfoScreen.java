package view;

import commons.Counters;
import commons.Session;
import controller.AccountInfoHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AccountInfoScreen extends LoggedScreen{

    @FXML
    private Label IBANLabel;

    @FXML
    private Label IDLabel;

    @FXML
    private Label autoExitCountLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label delayCountLabel;

    @FXML
    private Button editEmailButton;

    @FXML
    private Button editIBANButton;

    @FXML
    private Button editPhoneButton;

    @FXML
    private Label emailLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label holidayCountLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Label parentalLeaveLabel;

    @FXML
    private Label phoneLabel;

    private final AccountInfoHandler accountInfoHandler;
    private final Counters workerCounters;

    public AccountInfoScreen(Counters workerCounters, AccountInfoHandler accountInfoHandler) {
        this.accountInfoHandler = accountInfoHandler;
        this.workerCounters = workerCounters;

    }
    @Override
    public void initialize(){
        super.initialize();
        IDLabel.setText(Session.getInstance().getWorker().getId());
        fullNameLabel.setText(Session.getInstance().getWorker().getFullName().toUpperCase());
        phoneLabel.setText(Session.getInstance().getWorker().getPhone());
        emailLabel.setText(Session.getInstance().getWorker().getEmail());
        IBANLabel.setText(Session.getInstance().getWorker().getIban());
        autoExitCountLabel.setText(String.valueOf(workerCounters.autoExit()));
        delayCountLabel.setText(String.valueOf(workerCounters.delay()));
        holidayCountLabel.setText(String.valueOf(workerCounters.holiday()));
        parentalLeaveLabel.setText(String.valueOf(workerCounters.parentalLeave()));

    }

    @FXML
    public void clickBack(ActionEvent event) {
        accountInfoHandler.clickedBack();
    }

    @FXML
    public void clickEditEmail(ActionEvent event) {
        accountInfoHandler.clickedEditEmail();
    }

    @FXML
    public void clickEditIBAN(ActionEvent event) {
        accountInfoHandler.clickedEditIBAN();
    }

    @FXML
    public void clickEditPhone(ActionEvent event) {
        accountInfoHandler.clickedEditPhone();
    }

    @FXML
    public void clickLogout(ActionEvent event) {
        accountInfoHandler.clickedLogout();
    }

}

