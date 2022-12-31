package com.example.view;

import commons.Period;
import commons.Session;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class ViewShiftsScreen extends LoggedScreen {

    @FXML
    private MenuItem holidayButton;
    @FXML
    private MenuItem leavesButton;
    @FXML
    private MenuItem illnessButton;
    @FXML
    private MenuItem strikesButton;
    @FXML
    private MenuItem parentalLeaveButton;

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
    private static Random RANDOM = new Random();
    private LocalDate updatedDate;

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
        synchroBar();
        abstentionsMenu.getStylesheets().add(String.valueOf(getClass().getResource("css/AbstentionsMenuStyle.css")));
        abstentionsMenu.getStyleClass().add("abstentionsMenu");
        updatedDate = LocalDate.now();
        updateShiftsPane(updatedDate);
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
            String color = getRandomColor();
            shiftCard.setStyle("-fx-background-color: " + color);
            if (color.equals("#558D92") || color.equals("#929292")){
                shiftCard.getChildren().get(0).setStyle("-fx-text-fill: white");
                shiftCard.getChildren().get(1).setStyle("-fx-text-fill: white");
            }
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
        updatedDate = updatedDate.plusMonths(1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        updateShiftsPane(updatedDate);
    }

    @FXML
    public void clickNextWeek(ActionEvent event) {
        updatedDate = updatedDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        updateShiftsPane(updatedDate);
    }

    @FXML
    public void clickPreviousMonth(ActionEvent event) {
        updatedDate = updatedDate.minusMonths(1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        updateShiftsPane(updatedDate);
    }

    @FXML
    public void clickPreviousWeek(ActionEvent event) {
        updatedDate = updatedDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateShiftsPane(updatedDate);
    }

    /**
     * Aggiorna il panello dei turni con i nuovi turni relativi alla settimana della data passata per parametro.
     * @param date data su cui vengono aggiornati i turni della settimana
     */
    private void updateShiftsPane(LocalDate date) {
        showedWeek = computeWeek(date);
        weekShiftsList = shiftsOfShowedWeek(showedWeek);
        shiftsPane.getChildren().clear();
        insertAllShiftsCard(weekShiftsList);
        weekLabel.setText(weekString(date));
    }

    @FXML
    public void clickStrikes(ActionEvent event){
        //TODO:
    }
    @FXML
    public void clickParentalLeave(ActionEvent event){
        //TODO:
    }
    @FXML
    public void clickHoliday(ActionEvent event){
        //TODO:
    }
    @FXML
    public void clickIllness(ActionEvent event){
        //TODO:
    }
    @FXML
    public void clickLeave(ActionEvent event){
        //TODO:
    }

}

