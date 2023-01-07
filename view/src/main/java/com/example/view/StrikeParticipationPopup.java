package com.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;

public class StrikeParticipationPopup {

    @FXML
    private AnchorPane cardsPane;

    @FXML
    private Button participateButton;

    @FXML
    private ScrollBar verticalBar;

    @FXML
    private ScrollPane verticalScrollPane;

    public StrikeParticipationPopup(List<HashMap<String, String>> authorizedStrikes,
                                    StrikeParticipationHandler strikeParticipationHandler) {
    }

    @FXML
    public void initialize(){

    }

    @FXML
    void clickParticipate(ActionEvent event) {

    }

}


