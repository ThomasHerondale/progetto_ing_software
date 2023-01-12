package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class RecordPresenceScreen {

    @FXML
    private PasswordField idField;

    @FXML
    private ImageView logo;

    @FXML
    private TextField nameField;

    @FXML
    private Button recordEntranceButton;

    @FXML
    private Button recordExitButton;

    @FXML
    private TextField surnameField;

    private RecordPresenceHandler recordPresenceHandler;

    public RecordPresenceScreen(){
        recordPresenceHandler = new RecordPresenceHandler();
    }

    @FXML
    void clickRecordEntrance(ActionEvent event) {
        recordPresenceHandler.clickedRecordEntrance(nameField.getText(), surnameField.getText(), idField.getText());
    }

    @FXML
    void clickRecordExit(ActionEvent event) {
        recordPresenceHandler.clickedRecordExit(nameField.getText(), surnameField.getText(), idField.getText());
    }

}
