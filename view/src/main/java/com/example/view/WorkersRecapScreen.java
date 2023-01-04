package com.example.view;

import commons.WorkerStatus;
import database.DBMSDaemon;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkersRecapScreen extends LoggedScreen{

    @FXML
    private Button addWorkerButton;

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane cardsPane;

    @FXML
    private CheckBox freeStatus;

    @FXML
    private CheckBox illStatus;

    @FXML
    private CheckBox onHolidayStatus;

    @FXML
    private CheckBox parentalLeaveStatus;

    @FXML
    private CheckBox rankABox;

    @FXML
    private CheckBox rankBBox;

    @FXML
    private CheckBox rankCBox;

    @FXML
    private CheckBox rankDBox;

    @FXML
    private CheckBox rankHBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private CheckBox strikingStatus;

    @FXML
    private ScrollBar verticalBar;

    @FXML
    private CheckBox workingStatus;


    private List<Worker> workersList;
    private List<Worker> workersFilter;
    private Map<String, WorkerStatus> workersStatus;
    private final WorkersRecapHandler workersRecapHandler;
    private final AccountInfoHandler accountInfoHandler;

    public WorkersRecapScreen(List<Worker> workersList, Map<String, WorkerStatus> workersStatus, WorkersRecapHandler workersRecapHandler) {
        this.workersList = workersList;
        this.workersRecapHandler = workersRecapHandler;
        this.accountInfoHandler = new AccountInfoHandler();
        workersFilter = new ArrayList<>();
        this.workersStatus = workersStatus;
    }
    @FXML
    public void initialize(){
        super.initialize();
        verticalBar.valueProperty().bindBidirectional(scrollPane.vvalueProperty());
        rankABox.setSelected(true);
        rankBBox.setSelected(true);
        rankCBox.setSelected(true);
        rankDBox.setSelected(true);
        rankHBox.setSelected(true);
        freeStatus.setSelected(true);
        strikingStatus.setSelected(true);
        illStatus.setSelected(true);
        onHolidayStatus.setSelected(true);
        workingStatus.setSelected(true);
        parentalLeaveStatus.setSelected(true);
        insertAllWorkersCard(workersList);
    }

    private void insertAllWorkersCard(List<Worker> workersList) {
        for (int i=0 ; i< workersList.size(); i++){
            String id = workersList.get(i).getId();
            String fullName = workersList.get(i).getFullName();
            char rank = workersList.get(i).getRank();
            String status = workersStatus.get(id).getStringValue();
            String colorStatus = workersStatus.get(id).getColorString();
            cardsPane.getChildren().add(i, createWorkerCard(id, fullName, rank, status, colorStatus));
            cardsPane.getChildren().get(i).setLayoutY(computeLayoutY(i));
            cardsPane.getChildren().get(i).setLayoutX(24);
            cardsPane.setPrefHeight(computePaneHeight(i));

            Worker worker = workersList.get(i);
            cardsPane.getChildren().get(i).setOnMouseClicked(mouseEvent ->
                    workersRecapHandler.selectedWorker(worker));
        }
    }

    private AnchorPane createWorkerCard(String id, String fullName, char rank, String status, String colorStatus) {
        int indexChildren = 0;
        AnchorPane workerCard = new AnchorPane();
        workerCard.setPrefWidth(650);
        workerCard.setMaxWidth(650);
        workerCard.setMinWidth(650);
        workerCard.setPrefHeight(80);
        workerCard.setMaxHeight(80);
        workerCard.setMinHeight(80);
        Label idLabel = new Label(id);
        Label fullNameLabel = new Label(fullName);
        Label rankLabel = new Label("Livello " + rank);
        if (rank == 'H'){
            rankLabel = new Label("Admin");
        }
        Label statusLabel = new Label (status);
        Circle circle = new Circle();
        circle.setRadius(10);
        circle.setFill(Color.web(colorStatus));
        workerCard.getChildren().add(indexChildren, idLabel);
        workerCard.getChildren().get(indexChildren).setLayoutX(17);
        workerCard.getChildren().get(indexChildren).setLayoutY(28);
        workerCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");
        indexChildren++;

        workerCard.getChildren().add(indexChildren, fullNameLabel);
        workerCard.getChildren().get(indexChildren).setLayoutX(121);
        workerCard.getChildren().get(indexChildren).setLayoutY(28);
        workerCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");
        indexChildren++;

        workerCard.getChildren().add(indexChildren, rankLabel);
        workerCard.getChildren().get(indexChildren).setLayoutX(366);
        workerCard.getChildren().get(indexChildren).setLayoutY(28);
        workerCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");
        indexChildren++;

        workerCard.getChildren().add(indexChildren, circle);
        workerCard.getChildren().get(indexChildren).setLayoutX(475);
        workerCard.getChildren().get(indexChildren).setLayoutY(40);
        indexChildren++;

        workerCard.getChildren().add(indexChildren, statusLabel);
        workerCard.getChildren().get(indexChildren).setLayoutX(506);
        workerCard.getChildren().get(indexChildren).setLayoutY(28);
        workerCard.getChildren().get(indexChildren).setStyle("-fx-text-fill: white");

        workerCard.setStyle("-fx-background-color: #313146");
        workerCard.getStylesheets().add(String.valueOf(getClass().getResource("css/styleWorkerCard.css")));
        workerCard.getStyleClass().add("workerCard");
        workerCard.setCursor(Cursor.HAND);

        return workerCard;
    }

    /**
     * Calcola l'altezza del pannello {@link WorkersRecapScreen#cardsPane}.
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
     * Calcola la distanza dal bordo del pannello {@link WorkersRecapScreen#cardsPane} di una WorkerCard.
     * @param i indice utilizzato per il calcolo della distanza
     * @return ritorna la distanza nel formato double
     */
    private double computeLayoutY(int i) {
        return 26 + ((80.0 + 12.0) * i);
    }

    /**
     * Aggiorna il pannello {@link WorkersRecapScreen#cardsPane} che visualizza gli impiegati, eliminando, se esistono, i precedenti
     * impiegati e inserendo tutti quelli che si trovano dentro la nuova lista aggiornata
     * passata per parametro.
     * @param workersFilter lista aggiornata di cui si vogliono visualizzare gli impiegati
     */
    private void updateCardsPane(List<Worker> workersFilter) {
        cardsPane.getChildren().clear();
        insertAllWorkersCard(workersFilter);
    }

    @FXML
    void clickAddWorker(ActionEvent event) {
        //TODO:
    }

    @FXML
    void clickBack(ActionEvent event) {
        workersRecapHandler.clickedBack(true);
    }

    @FXML
    void clickProfile(MouseEvent event) {
        accountInfoHandler.clickedProfile();
    }

    @FXML
    void clickSearch(MouseEvent event) {
        workersFilter.clear();
        workersFilter = workersRecapHandler.clickedSearch(searchBar.getText(), workersList, workersStatus, rankABox.isSelected(),
                rankBBox.isSelected(), rankCBox.isSelected(), rankDBox.isSelected(), rankHBox.isSelected(),
                workingStatus.isSelected(), freeStatus.isSelected(), onHolidayStatus.isSelected(),
                illStatus.isSelected(), strikingStatus.isSelected(), parentalLeaveStatus.isSelected());
        updateCardsPane(workersFilter);
    }


    @FXML
    void onFilterClick(ActionEvent event) {
        workersFilter.clear();
        workersFilter = workersRecapHandler.clickedSearch(searchBar.getText(), workersList, workersStatus, rankABox.isSelected(),
                rankBBox.isSelected(), rankCBox.isSelected(), rankDBox.isSelected(), rankHBox.isSelected(),
                workingStatus.isSelected(), freeStatus.isSelected(), onHolidayStatus.isSelected(),
                illStatus.isSelected(), strikingStatus.isSelected(), parentalLeaveStatus.isSelected());
        updateCardsPane(workersFilter);
    }

}

