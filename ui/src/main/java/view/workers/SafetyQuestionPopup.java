package view.workers;

import controller.workers.RetrievePasswordHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SafetyQuestionPopup {

    @FXML
    private TextField answerField;

    @FXML
    private Label questionLabel;

    @FXML
    private Button sendButton;

    private RetrievePasswordHandler retrievePasswordHandler;
    private String question;

    public SafetyQuestionPopup (String question, RetrievePasswordHandler handler){
        this.retrievePasswordHandler = handler;
        this.question = question;
    }
    @FXML
    public void initialize(){
        questionLabel.setText(question);
    }

    @FXML
    void clickSend(ActionEvent event) {
        retrievePasswordHandler.clickedSend(answerField.getText());
    }

}
