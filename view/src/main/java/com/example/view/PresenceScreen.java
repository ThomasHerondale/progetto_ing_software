package com.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.List;

public class PresenceScreen extends LoggedScreen{

    @FXML
    private Button backButton;

    @FXML
    private Button insertPresenceButton;

    @FXML
    private ListView<?> listPresenceView;

    @FXML
    private CheckBox rankA;

    @FXML
    private CheckBox rankB;

    @FXML
    private CheckBox rankC;

    @FXML
    private CheckBox rankD;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private ScrollBar verticalBar;

    @FXML
    private ScrollPane verticalPane;

    @FXML
    public void initialize(){
        super.initialize();
    }

    @FXML
    void clickBack(ActionEvent event) {

    }

    @FXML
    void clickInsertPresence(ActionEvent event) {

    }

    @FXML
    void clickSearch(MouseEvent event) {

    }

    @FXML
    void onFilterClick(ActionEvent event) {

    }

    List<HashMap<String, String>> presences;
    public PresenceScreen(List<HashMap<String, String>> presences) {
        this.presences = presences;
    }
}
