package com.example.view;

import commons.Period;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;

public class AuthorizeStrikePopup {

    @FXML
    private Button confirmButton;

    @FXML
    private DatePicker dateCalendar;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox rankA;

    @FXML
    private CheckBox rankB;

    @FXML
    private CheckBox rankC;

    @FXML
    private CheckBox rankD;

    @FXML
    private CheckBox rankH;

    private ShiftsRecapHandler shiftsRecapHandler;
    private ObservableMap<Character, BooleanProperty> selectionRank;
    private Period locks;
    public AuthorizeStrikePopup(Period locks, ShiftsRecapHandler shiftsRecapHandler) {
        this.locks = locks;
        this.shiftsRecapHandler = shiftsRecapHandler;
        selectionRank = FXCollections.observableHashMap();
        selectionRank.put('A', new SimpleBooleanProperty(false));
        selectionRank.put('B', new SimpleBooleanProperty(false));
        selectionRank.put('C', new SimpleBooleanProperty(false));
        selectionRank.put('D', new SimpleBooleanProperty(false));
        selectionRank.put('H', new SimpleBooleanProperty(false));
    }

    @FXML
    public void initialize(){
        confirmButton.setDisable(true);
        nameField.setOnKeyPressed(this::enableConfirmButton);
        descriptionField.setOnKeyPressed(this::enableConfirmButton);
        nameField.setOnMouseClicked(this::enableConfirmButton);
        descriptionField.setOnMouseClicked(this::enableConfirmButton);
        rankA.setOnMouseClicked(this::enableConfirmButton);
        rankB.setOnMouseClicked(this::enableConfirmButton);
        rankC.setOnMouseClicked(this::enableConfirmButton);
        rankD.setOnMouseClicked(this::enableConfirmButton);
        rankH.setOnMouseClicked(this::enableConfirmButton);
        descriptionField.getStylesheets().add(String.valueOf(getClass().getResource(
                "css/text-area-background.css")));
        dateCalendar.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                if (locks.comprehends(localDate)) {
                    setDisabled(true);
                    setMouseTransparent(true);
                } else {
                    setDisabled(false);
                    setMouseTransparent(false);
                }
            }
        });
        rankA.selectedProperty().bindBidirectional(selectionRank.get('A'));
        rankB.selectedProperty().bindBidirectional(selectionRank.get('B'));
        rankC.selectedProperty().bindBidirectional(selectionRank.get('C'));
        rankD.selectedProperty().bindBidirectional(selectionRank.get('D'));
        rankH.selectedProperty().bindBidirectional(selectionRank.get('H'));
    }

    private void enableConfirmButton(Event inputEvent) {
        if (!nameField.getText().equals("") && !descriptionField.getText().equals("") &&
                dateCalendar.getValue() != null && (rankA.isSelected() || rankB.isSelected() ||
                rankC.isSelected() || rankD.isSelected() || rankH.isSelected())) {
            confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(true);
        }
    }

    @FXML
    public void clickConfirm(ActionEvent event) {
        shiftsRecapHandler.clickedConfirm(nameField.getText(), descriptionField.getText(), dateCalendar.getValue(),
                selectionRank);
    }
}

