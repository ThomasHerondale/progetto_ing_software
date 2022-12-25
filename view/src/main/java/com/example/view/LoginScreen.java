package com.example.view;

import database.DBMSException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    private LoginHandler loginHandler;
    public LoginScreen() {

        loginHandler = new LoginHandler();
    }
    @FXML
    public void clickLogin(ActionEvent event) {
        loginHandler.clickedLogin(idField.getText(), passwordField.getText());

    }

    @FXML
    public void clickRetrievePassword(ActionEvent event) {

    }
}
