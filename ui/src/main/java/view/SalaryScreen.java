package view;

import commons.HoursRecap;
import commons.Session;
import controller.AccountInfoHandler;
import controller.ViewSalaryHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class SalaryScreen extends LoggedScreen {

    @FXML
    private Label IDLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label baseSalary;

    @FXML
    private Label nameLabel;

    @FXML
    private Label overtimeSalary;

    @FXML
    private Label parentalLeaveSalary;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label totSalary;

    @FXML
    private Label baseAmount;

    @FXML
    private Label rankAmount;

    private final ViewSalaryHandler viewSalaryHandler;
    private final AccountInfoHandler accountInfoHandler;
    private Map<HoursRecap, Double> salaryData;

    public SalaryScreen(Map<HoursRecap, Double> salaryData, ViewSalaryHandler handler){
        this.viewSalaryHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
        this.salaryData = salaryData;
    }
    @FXML
    public void initialize(){
        super.initialize();
        IDLabel.setText(Session.getInstance().getWorker().getId());
        nameLabel.setText(Session.getInstance().getWorker().getName());
        surnameLabel.setText(Session.getInstance().getWorker().getSurname());
        baseSalary.setText(String.valueOf(salaryData.entrySet().iterator().next().getKey().ordinaryHours()));
        overtimeSalary.setText(String.valueOf(salaryData.entrySet().iterator().next().getKey().overtimeHours()));
        parentalLeaveSalary.setText(String.valueOf(salaryData.entrySet().iterator().next().getKey().parentalLeaveHours()));
        totSalary.setText(totSalary.getText() + " " + salaryData.entrySet().iterator().next().getValue());
        baseAmount.setText("€ 150");
        rankAmount.setText("€ " + getRankAmount());
    }

    private String getRankAmount() {
        char rank = Session.getInstance().getWorker().getRank();
        switch (rank){
            case 'A' -> {return "300";}
            case 'B' -> {return "250";}
            case 'C' -> {return "150";}
            case 'D' -> {return "100";}
            default -> {return "350";}
        }
    }

    @FXML
    public void clickBack(ActionEvent event) {
        viewSalaryHandler.clickedBack();
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }

}

