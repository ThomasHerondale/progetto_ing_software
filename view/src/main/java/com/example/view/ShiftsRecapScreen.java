package com.example.view;

import commons.Period;
import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class ShiftsRecapScreen extends LoggedScreen{

    @FXML
    private Button backButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Button nextWeekButton;

    @FXML
    private Button previousMonthButton;

    @FXML
    private Button previousWeekButton;

    @FXML
    private ScrollPane verticalAndHorizonatalPane;

    @FXML
    private ScrollBar verticalBar;

    @FXML
    private ScrollPane verticalPane;

    @FXML
    private ScrollBar horizontalBar;

    @FXML
    private ScrollPane horizontalPane;
    @FXML
    private ScrollBar horizontalBarChild1;
    @FXML
    private ScrollBar horizontalBarChild2;
    @FXML
    private ScrollBar horizontalBarChild3;
    @FXML
    private ScrollBar horizontalBarChild4;
    @FXML
    private ScrollBar horizontalBarChild5;
    @FXML
    private ScrollBar horizontalBarChild6;
    @FXML
    private ScrollBar horizontalBarChild7;
    @FXML
    private ScrollPane horizontalPaneChild1;
    @FXML
    private ScrollPane horizontalPaneChild2;
    @FXML
    private ScrollPane horizontalPaneChild3;
    @FXML
    private ScrollPane horizontalPaneChild4;
    @FXML
    private ScrollPane horizontalPaneChild5;
    @FXML
    private ScrollPane horizontalPaneChild6;
    @FXML
    private ScrollPane horizontalPaneChild7;
    @FXML
    private ScrollPane lowHorizontalPane;

    @FXML
    private AnchorPane mondayShiftsPane;
    @FXML
    private AnchorPane tuesdayShiftsPane;
    @FXML
    private AnchorPane wednesdayShiftsPane;
    @FXML
    private AnchorPane thursdayShiftsPane;
    @FXML
    private AnchorPane fridayShiftsPane;
    @FXML
    private AnchorPane saturdayShiftsPane;
    @FXML
    private AnchorPane sundayShiftsPane;

    @FXML
    private Label weekLabel;

    private LocalDate currentDate;
    private Period showedWeek;
    private List<Shift> weekShiftsList;
    private List<Shift> shiftsList;
    private static Random RANDOM = new Random();

    public ShiftsRecapScreen(){
        //TODO:
    }
    @FXML
    public void initialize(){
        super.initialize();
        synchroBar();
        currentDate = LocalDate.now();
        updateAllShiftsPane(currentDate);
    }

    /**
     * Aggiorna i pannelli dei turni con i nuovi turni relativi alla settimana della data passata per parametro.
     * @param date data su cui vengono aggiornati i turni della settimana
     */
    private void updateAllShiftsPane(LocalDate date) {
        showedWeek = computeWeek(date);
        weekShiftsList = shiftsOfShowedWeek(showedWeek);
        List<AnchorPane> shiftsPanes = getPanesList();
        weekLabel.setText(weekString(date));
        List<Shift> dailyShiftsList;
        for (int i=0 ; i<shiftsPanes.size(); i++){
            dailyShiftsList = insertShiftsOfTheDay(i, weekShiftsList);
            shiftsPanes.get(i).getChildren().clear();
            insertAllShiftsCard(dailyShiftsList, shiftsPanes.get(i));
        }
    }

    private void insertAllShiftsCard(List<Shift> dailyShiftsList, AnchorPane currentShiftPane) {
        Shift shift;
        double height;
        double cardLayoutY;
        double cardLayoutX;
        for (int i=0; i<dailyShiftsList.size(); i++){
            shift = dailyShiftsList.get(i);
            AnchorPane shiftCard = createShiftCard(shift.getOwner().getId(),shift.getOwner().getFullName());
            height = getShiftCardSize(shift);
            shiftCard.setPrefHeight(height);
            shiftCard.setMinHeight(height);
            shiftCard.setMaxHeight(height);
            cardLayoutY = computeLayoutY(shift);
            cardLayoutX = 0;
            if (i>=1){
                AnchorPane analizyingCard;
                for (int j=0; j<currentShiftPane.getChildren().size(); j++){
                    analizyingCard =(AnchorPane) currentShiftPane.getChildren().get(j);
                    cardLayoutX = cardLayoutX + computeLayoutX(height, cardLayoutY,
                            currentShiftPane.getChildren().get(j).getLayoutX(),
                            currentShiftPane.getChildren().get(j).getLayoutY(), analizyingCard.getPrefHeight());

                    currentShiftPane.setPrefWidth(currentShiftPane.getWidth() + cardLayoutX);
                }
            }
            currentShiftPane.getChildren().add(i, shiftCard);
            currentShiftPane.getChildren().get(i).setLayoutY(cardLayoutY);
            currentShiftPane.getChildren().get(i).setLayoutX(cardLayoutX);
            Shift finalShift = shift;
            String color = getRandomColor();
            shiftCard.setStyle("-fx-background-color: " + color);
            if (color.equals("#558D92") || color.equals("#929292")){
                shiftCard.getChildren().get(0).setStyle("-fx-text-fill: white");
                shiftCard.getChildren().get(1).setStyle("-fx-text-fill: white");
            }
            shiftCard.setOnMouseClicked(mouseEvent ->
                    new ViewShiftsInfoHandler().clickedShift(finalShift));
        }
    }

    private double computeLayoutX(double thisHeightCard, double thisLayoutY, double layoutX,
                               double layoutY, double secondHeightCard) {
        int startCurrentCard = (int) thisLayoutY;
        int endCurrentCard = (int) (thisLayoutY + thisHeightCard);
        int startOtherCard = (int) layoutY;
        int endOtherCard = (int) (layoutY + secondHeightCard);
        if ((startCurrentCard >= startOtherCard && startCurrentCard <= endOtherCard) ||
                (endCurrentCard >= startOtherCard && endCurrentCard <= endOtherCard) ||
                (startOtherCard >= startCurrentCard && startOtherCard <= endCurrentCard) ||
                (endOtherCard >= startCurrentCard && endOtherCard <= endCurrentCard)){
            return (15.0 + 227) + layoutX;
        } else {
            return 0.0;
        }
    }

    /**
     * Calcola un colore random e ritorna la stringa esadecimale.
     * @return ritorna la stringa esadecimale di un colore
     */

    private String getRandomColor(){
        return switch (RANDOM.nextInt(5)){
            case 0 -> "#8FBA90";
            case 1 -> "#558D92";
            case 2 -> "#A99494";
            case 3 -> "#EABC8E";
            default -> "#929292";
        };
    }
    /**
     * Calcola l'altezza di uno shiftCard in relazione all'ammontare di ore del suo turno.
     * @param shift turno che contiene il numero di ore.
     */
    private double getShiftCardSize(Shift shift) {
        int numberOfHours = shift.getHours();
        return (87.0 * numberOfHours) + (12 * (numberOfHours-1));
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
     * Crea una lista di turni facenti parte del giorno e della settimana indicati per parametro.
     * L'intero i corrisponde al giorno della settimana della data lista di turni della settimana.
     * In particolare se i = 0, il giorno è Lunedì, se i = 1 il giorno è Martedì, e così via...
     *
     * @param i parametro che indica il giorno della settimana
     * @param showedWeek parametro che indica i turni della settimana
     * @return ritorna la lista completa di turni del giorno della settimana
     */
    private List<Shift> insertShiftsOfTheDay(int i, List<Shift> showedWeek) {
        List<Shift> dailyShiftsList = new ArrayList<>();
        for (Shift shift : showedWeek) {
            if (i == 0 && shift.dayOfWeek() == DayOfWeek.MONDAY) {
                dailyShiftsList.add(shift);
            }
            if (i == 1 && shift.dayOfWeek() == DayOfWeek.TUESDAY) {
                dailyShiftsList.add(shift);
            }
            if (i == 2 && shift.dayOfWeek() == DayOfWeek.WEDNESDAY) {
                dailyShiftsList.add(shift);
            }
            if (i == 3 && shift.dayOfWeek() == DayOfWeek.THURSDAY) {
                dailyShiftsList.add(shift);
            }
            if (i == 4 && shift.dayOfWeek() == DayOfWeek.FRIDAY) {
                dailyShiftsList.add(shift);
            }
            if (i == 5 && shift.dayOfWeek() == DayOfWeek.SATURDAY) {
                dailyShiftsList.add(shift);
            }
            if (i == 6 && shift.dayOfWeek() == DayOfWeek.SUNDAY) {
                dailyShiftsList.add(shift);
            }
        }
        return dailyShiftsList;
    }

    /**
     * Ritorna la stringa della settimana corrente con tutte le informazioni tipo numero della settimana,
     * mese e anno.
     * @param date data su cui calcola le informazioni
     * @return ritorna la stringa con le informazioni
     */
    private String weekString(LocalDate date) {
        return getWeekNumber(date) + " Sett. "
                + (date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.ITALIAN))
                + " " + date.getYear();
    }
    /**
     * Ritorna la stringa in numero romano della settimana che contiene la data passata per parametro.
     * @param date data su cui calcolare il numero della settimana
     * @return ritorna una stringa in numero romano
     */
    private String getWeekNumber(LocalDate date) {
        //TODO:
        LocalDate firstMonday = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        int weeksBetween = (int) ChronoUnit.WEEKS.between(firstMonday, date);
        return intToRoman(weeksBetween + 1);
    }

    /**
     * Converte un intero in stringa che corrisponde al numero romano del numero passato.
     * @param num numero che si vuole convertire
     * @return ritorna la rappresentazione del parametro in numero romano come stringa
     */
    private String intToRoman(int num) {
        // Array che contiene tutti i simboli romani e i loro valori
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] strs = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        // Crea una stringa vuota per memorizzare il numero romano
        StringBuilder sb = new StringBuilder();

        // Itera attraverso il nostro array di valori
        for (int i = 0; i < values.length; i++) {
            // Continua a sottrarre il valore corrente finché num è maggiore o uguale a esso
            while (num >= values[i]) {
                // Aggiungi il simbolo romano corrispondente alla stringa
                sb.append(strs[i]);
                // Sottrai il valore corrente da num
                num -= values[i];
            }
        }

        return sb.toString();
    }

    /**
     * Crea una lista di AnchorPane con dentro tutti i pane che riguardano i giorni della settimana.
     * @return ritorna la lista di AnchorPane
     */
    private List<AnchorPane> getPanesList() {
        return List.of(mondayShiftsPane, tuesdayShiftsPane, wednesdayShiftsPane,
                thursdayShiftsPane, fridayShiftsPane, saturdayShiftsPane, sundayShiftsPane);
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
     * calcola la settimana relativa al parametro LocalDate passato.
     * Ritorna una variabile Period.
     * @param localDate data su cui calcolare il Period
     * @return variabile Period di una settimana che contiene il parametro passato
     */
    private Period computeWeek(LocalDate localDate) {
        LocalDate startWeekDate = localDate.with(DayOfWeek.MONDAY);
        LocalDate endWeekDate = localDate.with(DayOfWeek.SUNDAY);
        return new Period(startWeekDate, endWeekDate);
    }

    /**
     * Sincronizza le barre di scorrimento per gli orari e per i giorni.
     */
    private void synchroBar() {
        verticalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.vvalueProperty());
        verticalBar.valueProperty().bindBidirectional(verticalPane.vvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(verticalAndHorizonatalPane.hvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(horizontalPane.hvalueProperty());
        horizontalBar.valueProperty().bindBidirectional(lowHorizontalPane.hvalueProperty());
        horizontalBarChild1.valueProperty().bindBidirectional(horizontalPaneChild1.hvalueProperty());
        horizontalBarChild2.valueProperty().bindBidirectional(horizontalPaneChild2.hvalueProperty());
        horizontalBarChild3.valueProperty().bindBidirectional(horizontalPaneChild3.hvalueProperty());
        horizontalBarChild4.valueProperty().bindBidirectional(horizontalPaneChild4.hvalueProperty());
        horizontalBarChild5.valueProperty().bindBidirectional(horizontalPaneChild5.hvalueProperty());
        horizontalBarChild6.valueProperty().bindBidirectional(horizontalPaneChild6.hvalueProperty());
        horizontalBarChild7.valueProperty().bindBidirectional(horizontalPaneChild7.hvalueProperty());

    }
    /**
     * Ritorna la posizione Y da settare per una shiftCard dato lo shift corrispondente.
     * @param shift shift corrispondente a una shiftCard.
     * @return il valore intero della posizione Y.
     */
    private int computeLayoutY(Shift shift) {
        Map<LocalTime, Integer> position = positionY();
        return 4 + (87+12) * position.get(shift.getStartTime());
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
    void clickBack(ActionEvent event) {

    }

    @FXML
    void clickNextMonth(ActionEvent event) {

    }

    @FXML
    void clickNextWeek(ActionEvent event) {

    }

    @FXML
    void clickPreviousMonth(ActionEvent event) {

    }

    @FXML
    void clickPreviousWeek(ActionEvent event) {

    }

    @FXML
    void clickProfile(MouseEvent event) {
        //AccountInfoHandler accountInfoHandler = new AccountInfoHandler();
        //accountInfoHandler.clickedProfile();
    }

}

