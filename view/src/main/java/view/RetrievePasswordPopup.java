package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RetrievePasswordPopup {

    @FXML
    private TextField IDField;

    @FXML
    private Button confirmButton;

    private final RetrievePasswordHandler retrievePasswordHandler;

    public RetrievePasswordPopup(RetrievePasswordHandler handler){
        this.retrievePasswordHandler = handler;
    }

    @FXML
    public void initialize(){
        IDField.setText("");
    }
    @FXML
    public void clickConfirm(ActionEvent event) {
        retrievePasswordHandler.clickedConfirm(IDField.getText());
    }

}
