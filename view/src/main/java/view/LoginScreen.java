package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginScreen {

    /**
     * Questa classe
     * */

    @FXML
    private TextField idField;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink passwordRetrievalLink;
    @FXML
    private ImageView logo;

    private final LoginHandler loginHandler;
    private final RetrievePasswordHandler retrievePasswordHandler;
    public LoginScreen(){
        loginHandler = new LoginHandler();
        retrievePasswordHandler = new RetrievePasswordHandler();
    }
    @FXML
    public void clickLogin(ActionEvent event) {
        loginHandler.clickedLogin(idField.getText(), passwordField.getText());
    }
    @FXML
    public void clickRetrievePassword(ActionEvent event) {
        retrievePasswordHandler.clickedRetrievePassword();
    }
}
