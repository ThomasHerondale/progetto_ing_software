package view;

import commons.Session;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;

public class LoggedScreen {
    @FXML
    private Label initialsLabel;
    @FXML
    private Group profileIcon;
    @FXML
    public void initialize(){
        String nameInit = String.valueOf(Session.getInstance().getWorker().getName().charAt(0));
        String surnameInit = String.valueOf(Session.getInstance().getWorker().getSurname().charAt(0));
        initialsLabel.setText(nameInit + " " + surnameInit);
    }
}
