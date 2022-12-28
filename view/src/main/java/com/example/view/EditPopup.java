package com.example.view;

import commons.EditableProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditPopup {

    @FXML
    private Button confirmButton;

    @FXML
    private Label descriptionLabel;

    @FXML
    private TextField inputField;

    @FXML
    private Label titleLabel;

    private EditableProperty property;
    private AccountInfoHandler accountInfoHandler;

    public EditPopup(EditableProperty propertyToEdit, AccountInfoHandler handler){

        this.property = propertyToEdit;
        this.accountInfoHandler = handler;
    }
    @FXML
    public void initialize(){
        /*
        switch (property){

            case PHONE -> {
                titleLabel.setText("Modifica numero di cellulare");
                descriptionLabel.setText("nuovo numero di telefono");
                inputField.setPromptText("+39");
            }
            case EMAIL -> {
                titleLabel.setText("Modifica E-mail");
                descriptionLabel.setText("nuovo indirizzo di posta elettronica");
                inputField.setPromptText("user@email.com");
            }
            case IBAN -> {
                titleLabel.setText("Modifica IBAN");
                descriptionLabel.setText("nuovo IBAN");
                inputField.setPromptText("IT01234567890123456789");
            }
        }
        */
        titleLabel.setText("Modifica " + property.getStringValue());
        descriptionLabel.setText("nuovo " + property.getStringValue());
        inputField.setPromptText(property.getPromptText());
    }

    @FXML
    void clickConfirm(ActionEvent event) {
        accountInfoHandler.clickedConfirmEdit(inputField.getText(), property);
    }

}

