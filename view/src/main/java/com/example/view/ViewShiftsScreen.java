package com.example.view;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class ViewShiftsScreen extends LoggedScreen {

    @FXML
    private ScrollBar horizontalBar;

    @FXML
    private ScrollPane horizontalPane;

    @FXML
    private AnchorPane shiftsPane;

    @FXML
    private ScrollPane verticalAndHorizonatalPane;

    @FXML
    private ScrollBar verticalBar;

    @FXML
    private ScrollPane verticalPane;
    @FXML
    private MenuButton abstentionsMenu;

    private ShiftHandler shiftHandler;
    private AccountInfoHandler accountInfoHandler;
    private List<Shift> weekShiftsList;
    private List<Shift> shiftsList;

    public ViewShiftsScreen(ShiftHandler handler){
        this.shiftHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
        try {
            shiftsList = DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
        } catch (DBMSException e) {
            //TODO:
        }
    }

    @FXML
    public void initialize(){
        super.initialize();
        abstentionsMenu.getStylesheets().add(String.valueOf(getClass().getResource("css/AbstentionsMenuStyle.css")));
        abstentionsMenu.getStyleClass().add("abstentionsMenu");
        synchroBar();
        insertAllShiftsCard(Session.getInstance().getWorker().getId(),
                Session.getInstance().getWorker().getFullName(), shiftsList);
    }

    private void synchroBar() {
        verticalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.vvalueProperty());
        verticalBar.valueProperty().bindBidirectional(verticalPane.vvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.hvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(horizontalPane.hvalueProperty());
    }
    private void insertAllShiftsCard(String idWorker, String fullNameWorker, List<Shift> shiftsList){
        Shift shift;
        AnchorPane shiftCard;
        int indexChild = 0;
        for (int i=0; i<shiftsList.size(); i++){
            shift = shiftsList.get(i);
            createShiftCard(idWorker,fullNameWorker, shift, indexChild);
            //ogni shiftCard ha due children
            indexChild = indexChild + 2;
            shiftCard.getStylesheets().add(String.valueOf(getClass().getResource("css/styleShiftCard.css")));
            shiftCard.getStyleClass().add("shiftCard");
        }
    }
    private void createShiftCard(String id, String fullName, Shift shift, int indexChild){
        AnchorPane shiftCard = new AnchorPane();
        shiftCard.setPrefHeight(87);
        shiftCard.setPrefWidth(227);
        Label idLabel = new Label(id);
        shiftCard.getChildren().add(indexChild, idLabel);
        shiftCard.getChildren().get(indexChild).setLayoutY(20);
        shiftCard.getChildren().get(indexChild).setLayoutX(20);
        Label fullNameLabel = new Label(fullName);
        indexChild++;
        shiftCard.getChildren().add(indexChild, fullNameLabel);
        shiftCard.getChildren().get(indexChild).setLayoutY(40);
        shiftCard.getChildren().get(indexChild).setLayoutX(20);

        shiftCard.setCursor(Cursor.HAND);
        //set onMouseClicked va qua?
        computeCardSize(shift);
    }

    private void computeCardSize(Shift shift) {

    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }
    @FXML
    public void clickBack(ActionEvent event){
        shiftHandler.clickedBack();
    }

}

