package view;

import controller.AccountInfoHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SuccessPopup {

    @FXML
    private Button okayButton;

    private final AccountInfoHandler accountInfoHandler;

    public SuccessPopup(AccountInfoHandler accountInfoHandler) {
        this.accountInfoHandler = accountInfoHandler;
    }

    @FXML
    void clickOkay(ActionEvent event) {
        accountInfoHandler.clickedOkay();
    }
}
