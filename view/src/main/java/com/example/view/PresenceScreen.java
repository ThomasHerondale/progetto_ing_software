package com.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PresenceScreen extends LoggedScreen{

    @FXML
    private Button backButton;

    @FXML
    private Button insertPresenceButton;
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
    private AnchorPane cardsPane;

    @FXML
    private ScrollPane scrollPane;

    private final AccountInfoHandler accountInfoHandler;
    private final ShowPresencesHandler showPresencesHandler;
    private List<HashMap<String, String>> presences;
    private List<HashMap<String, String>> presencesFilter;

    public PresenceScreen(List<HashMap<String, String>> presences, ShowPresencesHandler showPresencesHandler) {
        this.presences = presences;
        this.showPresencesHandler = showPresencesHandler;
        accountInfoHandler = new AccountInfoHandler();
        presencesFilter = new ArrayList<>();
    }
    @FXML
    public void initialize(){
        super.initialize();
        verticalBar.valueProperty().bindBidirectional(scrollPane.vvalueProperty());
        rankA.setSelected(true);
        rankB.setSelected(true);
        rankC.setSelected(true);
        rankD.setSelected(true);
        insertAllPresencesCard(presences);
    }

    /**
     * Aggiorna il pannello {@link PresenceScreen#cardsPane} che visualizza le presenze, eliminando, se esistono, le precedenti
     * presenze e inserendo tutte quelle che si trovano dentro la nuova lista aggiornata
     * passata per parametro.
     * @param presencesFilter lista aggiornata di cui si vogliono visualizzare le presenze
     */
    private void updateCardsPane(List<HashMap<String, String>> presencesFilter) {
        cardsPane.getChildren().clear();
        insertAllPresencesCard(presencesFilter);
    }

    /**
     * Crea attraverso il metodo {@link PresenceScreen#createPresenceCard} tutte le presenceCard
     * corrispondenti agli elementi che si trovano nella Lista passata per parametro
     * e li inserisce nel pannello {@link PresenceScreen#cardsPane} per la visualizzazione delle presenze.
     * @param presencesFilter lista di presenze che si vogliono inserire nel pannello
     */
    private void insertAllPresencesCard(List<HashMap<String, String>> presencesFilter) {
        for (int i=0 ; i<presencesFilter.size(); i++){
            String id = presencesFilter.get(i).get("ID");
            String fullName = presencesFilter.get(i).get("workerName") + " " +
                    presencesFilter.get(i).get("workerSurname");
            String rank = presencesFilter.get(i).get("shiftRank");
            String entryTime = presencesFilter.get(i).get("entryTime");
            cardsPane.getChildren().add(i, createPresenceCard(id, fullName, rank, entryTime));
            cardsPane.getChildren().get(i).setLayoutY(computeLayoutY(i));
            cardsPane.getChildren().get(i).setLayoutX(24);
            cardsPane.setPrefHeight(computePaneHeight(i));
        }
    }

    /**
     * Calcola l'altezza del pannello {@link PresenceScreen#cardsPane}.
     * @param i indice utilizzato per il calcolo dell'altezza
     * @return ritorna l'altezza nel formato double
     */
    private double computePaneHeight(int i) {
        if (i>5){
            return 592 + ((80.0 + 12.0) * (i - 5));
        }
        return 578;
    }

    /**
     * Calcola la distanza dal bordo del pannello {@link PresenceScreen#cardsPane} di una PresenceCard.
     * @param i indice utilizzato per il calcolo della distanza
     * @return ritorna la distanza nel formato double
     */
    private double computeLayoutY(int i) {
        return 26 + ((80.0 + 12.0) * i);
    }

    @FXML
    public void clickProfile(MouseEvent event){
        accountInfoHandler.clickedProfile();
    }

    @FXML
    public void clickBack(ActionEvent event) {
        showPresencesHandler.clickedBack();
    }

    @FXML
    public void clickInsertPresence(ActionEvent event) {

    }

    @FXML
    public void clickSearch(MouseEvent event) {
        presencesFilter.clear();
        presencesFilter = showPresencesHandler.clickedSearch(searchBar.getText(), presences, rankA.isSelected(),
                rankB.isSelected(), rankC.isSelected(), rankD.isSelected());
        updateCardsPane(presencesFilter);
    }

    @FXML
    public void onFilterClick(ActionEvent event) {
        presencesFilter.clear();
        presencesFilter = showPresencesHandler.clickedSearch(searchBar.getText(), presences, rankA.isSelected(),
                rankB.isSelected(), rankC.isSelected(), rankD.isSelected());
        updateCardsPane(presencesFilter);
    }

    /**
     * Crea una nuova PresenceCard aggiungendo dentro tutto il necessario grazie ai valori forniti per parametro.
     * @param id matricola dell'impiegato
     * @param fullName nome e cognome dell'impiegato
     * @param rank livello del turno
     * @param entryTime orario d'ingresso
     * @return ritorna la nuova PresenceCard
     */
    private AnchorPane createPresenceCard(String id, String fullName, String rank, String entryTime){
        int indexChildren = 0;
        AnchorPane presenceCard = new AnchorPane();
        presenceCard.setPrefWidth(650);
        presenceCard.setMaxWidth(650);
        presenceCard.setMinWidth(650);
        presenceCard.setPrefHeight(80);
        presenceCard.setMaxHeight(80);
        presenceCard.setMinHeight(80);
        Label idLabel = new Label(id);
        Label fullNameLabel = new Label(fullName);
        Label rankLabel = new Label("Livello " + rank);
        Label entryTimeLabel = new Label(entryTime);
        Circle greenCircle = new Circle();
        greenCircle.setRadius(10);
        greenCircle.setFill(Color.web("#00FF38"));
        presenceCard.getChildren().add(indexChildren, idLabel);
        presenceCard.getChildren().get(indexChildren).setLayoutX(17);
        presenceCard.getChildren().get(indexChildren).setLayoutY(28);
        presenceCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");
        indexChildren++;

        presenceCard.getChildren().add(indexChildren, fullNameLabel);
        presenceCard.getChildren().get(indexChildren).setLayoutX(121);
        presenceCard.getChildren().get(indexChildren).setLayoutY(28);
        presenceCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");
        indexChildren++;

        presenceCard.getChildren().add(indexChildren, rankLabel);
        presenceCard.getChildren().get(indexChildren).setLayoutX(366);
        presenceCard.getChildren().get(indexChildren).setLayoutY(28);
        presenceCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");
        indexChildren++;

        presenceCard.getChildren().add(indexChildren, greenCircle);
        presenceCard.getChildren().get(indexChildren).setLayoutX(475);
        presenceCard.getChildren().get(indexChildren).setLayoutY(40);
        indexChildren++;

        presenceCard.getChildren().add(indexChildren, entryTimeLabel);
        presenceCard.getChildren().get(indexChildren).setLayoutX(544);
        presenceCard.getChildren().get(indexChildren).setLayoutY(28);
        presenceCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");

        presenceCard.setStyle("-fx-background-color: #313146");
        presenceCard.getStylesheets().add(String.valueOf(getClass().getResource("css/stylePresenceCard.css")));
        presenceCard.getStyleClass().add("presenceCard");
        return presenceCard;
    }
}
