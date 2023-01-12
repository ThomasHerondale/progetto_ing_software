package view;

import commons.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.util.Map;

public class FirstAccessPopup {

    @FXML
    private TextField answerField;

    @FXML
    private Button confirmButton;

    @FXML
    private Text nameLabel;

    @FXML
    private ComboBox<String> questionBox;
    private final Map<String, String> questionsList;
    private String questionSelectedID;
    private final LoginHandler loginHandler;

    //costruttore
    public FirstAccessPopup(Map<String, String> questionsList, LoginHandler handler) {
        this.questionsList = questionsList;
        this.loginHandler = handler;
    }

    @FXML
    public void initialize() {
        nameLabel.setText(Session.getInstance().getWorker().getFullName());
        questionsList.forEach((chiave, valore) -> questionBox.getItems().add(valore));
        questionSelectedID = "";
        questionBox.setOnAction(this::onQuestionSelected);
    }
    private void onQuestionSelected(ActionEvent event){
        questionsList.forEach((chiave,valore) -> {
            if (questionBox.getValue().equals(valore)){
                questionSelectedID = chiave;
            }
        });
    }
    @FXML
    public void clickConfirm(ActionEvent event) {
        if (!questionSelectedID.isEmpty()){
            loginHandler.clickedConfirm(Session.getInstance().getWorker().getId(), questionSelectedID, answerField.getText());
        }
    }
}

