package com.example.view;

import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewShiftsScreen extends LoggedScreen {

    @FXML
    private Button nextWeekButton;
    @FXML
    private Button nextMonthButton;
    @FXML
    private Button previousWeekButton;
    @FXML
    private Button previousMonthButton;
    @FXML
    private Label weekLabel;
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
    private int shiftIndex = 0;

    public ViewShiftsScreen(ShiftHandler handler){
        this.shiftHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
        try {
        /* questo è giusto ma per ora faccio prove
            shiftsList = DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
        */
            DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
            //prove
            shiftsList = new ArrayList<>();
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2023-01-02"),
                    LocalTime.parse("09:00:00"),LocalTime.parse("10:00:00") ));
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2023-01-02"),
                    LocalTime.parse("12:00:00"),LocalTime.parse("21:00:00") ));
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2023-01-02"),
                    LocalTime.parse("21:00:00"),LocalTime.parse("22:00:00") ));
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2023-01-05"),
                    LocalTime.parse("09:00:00"),LocalTime.parse("15:00:00") ));
        } catch (DBMSException e) {
            //TODO:
        }
    }

    @FXML
    public void initialize(){
        super.initialize();
        //inizializza pure weekLabel
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
        for (int i=0; i<shiftsList.size(); i++){
            int indexChild = 0;
            shift = shiftsList.get(i);
            createShiftCard(idWorker,fullNameWorker, shift, indexChild);
        }
    }
    private void createShiftCard(String id, String fullName, Shift shift, int indexChild){
        AnchorPane shiftCard = new AnchorPane();
        shiftCard.setPrefWidth(227);
        shiftCard.setMaxWidth(227);
        shiftCard.setMinWidth(227);
        Label idLabel = new Label(id);
        shiftCard.getChildren().add(indexChild, idLabel);
        shiftCard.getChildren().get(indexChild).setLayoutY(20);
        shiftCard.getChildren().get(indexChild).setLayoutX(20);
        Label fullNameLabel = new Label(fullName);
        indexChild++;
        shiftCard.getChildren().add(indexChild, fullNameLabel);
        shiftCard.getChildren().get(indexChild).setLayoutY(40);
        shiftCard.getChildren().get(indexChild).setLayoutX(20);

        shiftCard.getStylesheets().add(String.valueOf(getClass().getResource("css/styleShiftCard.css")));
        shiftCard.getStyleClass().add("shiftCard");

        shiftCard.setCursor(Cursor.HAND);
        //set onMouseClicked va qua?
        computeCardSize(shiftCard, shift);
    }

    private void computeCardSize(AnchorPane shiftCard,Shift shift) {
        int numberOfHours = shift.getHours();
        int height = (87 * numberOfHours) + (12 * (numberOfHours-1));
        shiftCard.setPrefHeight(height);
        shiftCard.setMinHeight(height);
        shiftCard.setMaxHeight(height);
        Map<LocalTime, Integer> posizioneLayoutY = posizioneY();
        int layoutY = 5 + (87+12) * posizioneLayoutY.get(shift.getStartTime());
        int day = shift.dayOfWeek().getValue();
        int layoutX = 38 + (227 + 27) * (day - 1);

        //Inserisco la shiftCard

        shiftsPane.getChildren().add(shiftIndex, shiftCard);
        shiftsPane.getChildren().get(shiftIndex).setLayoutY(layoutY);
        shiftsPane.getChildren().get(shiftIndex).setLayoutX(layoutX);
        shiftIndex++;


    }
    private Map<LocalTime, Integer> posizioneY(){
        Map<LocalTime, Integer> posizioneLayoutY = new HashMap<>(Map.of(
                LocalTime.parse("08:00:00"), 0,
                LocalTime.parse("09:00:00"), 1,
                LocalTime.parse("10:00:00"), 2,
                LocalTime.parse("11:00:00"), 3,
                LocalTime.parse("12:00:00"), 4,
                LocalTime.parse("13:00:00"), 5,
                LocalTime.parse("14:00:00"), 6,
                LocalTime.parse("15:00:00"), 7,
                LocalTime.parse("16:00:00"), 8,
                LocalTime.parse("17:00:00"), 9));
        Map<LocalTime, Integer> map = Map.of(
                LocalTime.parse("18:00:00"), 10,
                LocalTime.parse("19:00:00"), 11,
                LocalTime.parse("20:00:00"), 12,
                LocalTime.parse("21:00:00"), 13,
                LocalTime.parse("22:00:00"), 14);
        posizioneLayoutY.putAll(map);
        return posizioneLayoutY;
    }

    @FXML
    public void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }
    @FXML
    public void clickBack(ActionEvent event){
        shiftHandler.clickedBack();
    }

    @FXML
    public void clickNextMonth(ActionEvent event) {

    }

    @FXML
    public void clickNextWeek(ActionEvent event) {

    }

    @FXML
    public void clickPreviousMonth(ActionEvent event) {

    }

    @FXML
    public void clickPreviousWeek(ActionEvent event) {

    }

}

