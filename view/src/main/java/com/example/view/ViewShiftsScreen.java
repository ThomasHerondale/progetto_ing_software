package com.example.view;

import commons.Period;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

    private final ShiftHandler shiftHandler;
    private final AccountInfoHandler accountInfoHandler;
    private List<Shift> weekShiftsList;
    private List<Shift> shiftsList;
    private Period showedWeek;

    public ViewShiftsScreen(ShiftHandler handler){
        this.shiftHandler = handler;
        accountInfoHandler = new AccountInfoHandler();
        try {
            shiftsList = DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());

        /*    DBMSDaemon.getInstance().getShiftsList(Session.getInstance().getWorker().getId());
            //prove
            shiftsList = new ArrayList<>();
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2023-01-02"),
                    LocalTime.parse("09:00:00"),LocalTime.parse("10:00:00") ));
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2022-12-26"),
                    LocalTime.parse("12:00:00"),LocalTime.parse("21:00:00") ));
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2022-12-26"),
                    LocalTime.parse("21:00:00"),LocalTime.parse("22:00:00") ));
            shiftsList.add(new Shift(Session.getInstance().getWorker(),
                    Session.getInstance().getWorker().getRank(), LocalDate.parse("2022-12-27"),
                    LocalTime.parse("09:00:00"),LocalTime.parse("15:00:00") ));
         */
        } catch (DBMSException e) {
            //TODO:
        }
    }

    @FXML
    public void initialize(){
        super.initialize();
        synchroBar();
        //inizializza pure weekLabel
        weekLabel.setText(getWeekString());

        showedWeek = computeWeek(LocalDate.now());
        weekShiftsList = shiftsOfShowedWeek(showedWeek);
        abstentionsMenu.getStylesheets().add(String.valueOf(getClass().getResource("css/AbstentionsMenuStyle.css")));
        abstentionsMenu.getStyleClass().add("abstentionsMenu");
        insertAllShiftsCard(weekShiftsList);
    }

    /**
     * calcola la settimana relativa al parametro LocalDate passato.
     * Ritorna una variabile Period.
     * @param localDate
     * @return variabile Period di una settimana che contiene il parametro passato
     */
    private Period computeWeek(LocalDate localDate) {
        LocalDate startWeekDate = localDate.with(DayOfWeek.MONDAY);
        LocalDate endWeekDate = localDate.with(DayOfWeek.SUNDAY);
        return new Period(startWeekDate, endWeekDate);
    }

    private String getWeekString() {
        return "";
    }

    /**
     * Ritorna la lista dei turni contenuti all'interno del Period passato per parametro.
     * @param showedWeek Period, ovvero la settiman che si intende mostrare.
     * @return lista di turni facenti parte del Period.
     */
    private List<Shift> shiftsOfShowedWeek(Period showedWeek) {
        List<Shift> week = new ArrayList<>();
        for (var shift : shiftsList) {
            if (showedWeek.comprehends(shift.getDate())) {
                week.add(shift);
            }
        }
        return week;
    }

    /**
     * Sincronizza le barre di scorrimento per gli orari e per i giorni.
     */
    private void synchroBar() {
        verticalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.vvalueProperty());
        verticalBar.valueProperty().bindBidirectional(verticalPane.vvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.hvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(horizontalPane.hvalueProperty());
    }

    /**
     * Per ogni turno in shiftsList crea una ShiftCard e la inserisce nello shiftsPane.
     * Inoltre qui dentro viene impostato il metodo setOnMouseClicked di ogni ShiftCard.
     * @param shiftsList lista dei turni.
     */
    private void insertAllShiftsCard(List<Shift> shiftsList){
        Shift shift;
        for (int i=0; i<shiftsList.size(); i++){
            shift = shiftsList.get(i);
            AnchorPane shiftCard = createShiftCard(shift.getOwner().getId(),shift.getOwner().getFullName());
            setShiftCardSize(shiftCard, shift);
            int cardLayoutY = computeLayoutY(shift);
            int cardLayoutX = computeLayoutX(shift);
            shiftsPane.getChildren().add(i, shiftCard);
            shiftsPane.getChildren().get(i).setLayoutY(cardLayoutY);
            shiftsPane.getChildren().get(i).setLayoutX(cardLayoutX);
            Shift finalShift = shift;
            shiftCard.setOnMouseClicked(mouseEvent -> {
                try {
                    new ViewShiftsInfoHandler().clickedShift(finalShift);
                } catch (DBMSException e) {
                    //TODO:
                }
            });
        }
    }

    /**
     * Ritorna la posizione X da settare per una shiftCard dato lo shift corrispondente.
     * @param shift shift corrispondente a una shiftCard.
     * @return il valore intero della posizione X.
     */
    private int computeLayoutX(Shift shift) {
        int day = shift.dayOfWeek().getValue();
        return 38 + (227 + 27) * (day - 1);
    }

    /**
     * Ritorna la posizione Y da settare per una shiftCard dato lo shift corrispondente.
     * @param shift shift corrispondente a una shiftCard.
     * @return il valore intero della posizione Y.
     */
    private int computeLayoutY(Shift shift) {
        Map<LocalTime, Integer> position = positionY();
        return 5 + (87+12) * position.get(shift.getStartTime());
    }

    /**
     * Crea una nuova ShiftCard che contiene e mostra matricola, nome e cognome del dipendente relativo al turno.
     * @param id matricola del dipendente.
     * @param fullName nome e cognome del dipendente.
     * @return un AnchorPane che è una ShiftCard.
     */
    private AnchorPane createShiftCard(String id, String fullName){
        int indexChildCard = 0;
        AnchorPane shiftCard = new AnchorPane();
        shiftCard.setPrefWidth(227);
        shiftCard.setMaxWidth(227);
        shiftCard.setMinWidth(227);
        Label idLabel = new Label(id);
        shiftCard.getChildren().add(indexChildCard, idLabel);
        shiftCard.getChildren().get(indexChildCard).setLayoutY(20);
        shiftCard.getChildren().get(indexChildCard).setLayoutX(20);
        Label fullNameLabel = new Label(fullName);
        indexChildCard++;
        shiftCard.getChildren().add(indexChildCard, fullNameLabel);
        shiftCard.getChildren().get(indexChildCard).setLayoutY(40);
        shiftCard.getChildren().get(indexChildCard).setLayoutX(20);
        shiftCard.getStylesheets().add(String.valueOf(getClass().getResource("css/styleShiftCard.css")));
        shiftCard.getStyleClass().add("shiftCard");
        shiftCard.setCursor(Cursor.HAND);
        return shiftCard;
    }

    /**
     * Imposta l'altezza di uno shiftCard in relazione all'ammontare di ore del suo turno.
     * @param shiftCard shiftCard su cui calcola e imposta l'altezza.
     * @param shift turno che contiene il numero di ore.
     */
    private void setShiftCardSize(AnchorPane shiftCard, Shift shift) {
        int numberOfHours = shift.getHours();
        int height = (87 * numberOfHours) + (12 * (numberOfHours-1));
        shiftCard.setPrefHeight(height);
        shiftCard.setMinHeight(height);
        shiftCard.setMaxHeight(height);
    }
    /**
     * Ritorna una mappa con valore gli interi da utilizzare per il calcolo della posizione Y.
     * @return una mappa chiave LocalTime e valore Integer per calcolare la posizione Y di una shiftCard.
     */
    private Map<LocalTime, Integer> positionY(){
        Map<LocalTime, Integer> positionLayoutY = new HashMap<>(Map.of(
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
        positionLayoutY.putAll(map);
        return positionLayoutY;
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

