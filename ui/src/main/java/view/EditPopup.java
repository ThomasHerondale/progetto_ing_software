package view;

import commons.EditableProperty;
import controller.AccountInfoHandler;
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
        titleLabel.setText("Modifica " + property.getStringValue());
        descriptionLabel.setText("nuovo " + property.getStringValue());
        inputField.setPromptText(property.getPromptText());
    }
    @FXML
    void clickConfirm(ActionEvent event) {
        var regexp = switch (property) {
            case PHONE -> "\\d{10}";
            case EMAIL -> "\\w+@\\S+";
            case IBAN -> "IT\\d{2}\\w\\d{22}";
        };
        if (inputField.getText().matches(regexp))
            accountInfoHandler.clickedConfirmEdit(inputField.getText(), property);
    }

}

