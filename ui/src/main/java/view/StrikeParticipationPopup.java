package view;

import controller.StrikeParticipationHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
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

    private List<HashMap<String, String>> authorizedStrikes;
    StrikeParticipationHandler strikeParticipationHandler;
    AnchorPane previouslySelectedStrike = null;
    String selectedStrikeName;
    LocalDate selectedStrikeDate;

    public StrikeParticipationPopup(List<HashMap<String, String>> authorizedStrikes,
                                    StrikeParticipationHandler strikeParticipationHandler) {
        this.authorizedStrikes = authorizedStrikes;
        this.strikeParticipationHandler = strikeParticipationHandler;
    }

    @FXML
    public void initialize(){
        verticalBar.valueProperty().bindBidirectional(verticalScrollPane.vvalueProperty());
        insertStrikeCards(authorizedStrikes);
        participateButton.setDisable(true);
    }

    private void insertStrikeCards(List<HashMap<String, String>> authorizedStrikes) {
        String name;
        String date;
        String description;
        for ( int i=0 ; i<authorizedStrikes.size(); i++){
            name = authorizedStrikes.get(i).get("strikeName");
            date = authorizedStrikes.get(i).get("strikeDate");
            description = authorizedStrikes.get(i).get("descriptionStrike");
            cardsPane.getChildren().add(i, createStrikeCard(name, date, description));
            cardsPane.getChildren().get(i).setLayoutX(16);
            cardsPane.getChildren().get(i).setLayoutY(computeLayoutY(i));
            cardsPane.setPrefHeight(computePaneHeight(i));

            String selectedName = name;
            LocalDate selectedDate = LocalDate.parse(date);
            AnchorPane strikeToSelect = (AnchorPane) cardsPane.getChildren().get(i);
            strikeToSelect.setOnMouseClicked(mouseEvent ->
                    onStrikeSelected(strikeToSelect, selectedName, selectedDate));
        }
    }

    private void onStrikeSelected(AnchorPane strikeCard, String selectedStrikeName, LocalDate selectedStrikeDate) {
        if (previouslySelectedStrike != null){
            previouslySelectedStrike.setStyle("");
        }
        strikeCard.setStyle("-fx-border-color: white; -fx-border-width: 1px; -fx-border-radius: 2;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        previouslySelectedStrike = strikeCard;
        participateButton.setDisable(false);
        this.selectedStrikeDate = selectedStrikeDate;
        this.selectedStrikeName = selectedStrikeName;
        }

    private AnchorPane createStrikeCard(String name, String date, String description) {
        AnchorPane strikeCard = new AnchorPane();
        strikeCard.setPrefWidth(390);
        strikeCard.setPrefHeight(41);
        strikeCard.setMinHeight(41);
        strikeCard.setMinWidth(390);
        strikeCard.setMaxHeight(41);
        strikeCard.setMaxWidth(390);
        Label titleLabel = new Label(name);
        Label dateLabel = new Label(date);
        Label descriptionLabel = new Label(description);

        descriptionLabel.setTooltip(new Tooltip(descriptionLabel.getText()));
        titleLabel.setTooltip(new Tooltip(titleLabel.getText()));

        descriptionLabel.setWrapText(false);
        titleLabel.setWrapText(false);
        dateLabel.setWrapText(false);
        strikeCard.getChildren().add(0, titleLabel);
        AnchorPane.setTopAnchor(strikeCard.getChildren().get(0), 8.0);
        AnchorPane.setBottomAnchor(strikeCard.getChildren().get(0), 8.0);
        AnchorPane.setLeftAnchor(strikeCard.getChildren().get(0), 17.0);
        AnchorPane.setRightAnchor(strikeCard.getChildren().get(0), 279.0);

        strikeCard.getChildren().add(1, dateLabel);
        AnchorPane.setTopAnchor(strikeCard.getChildren().get(1), 8.0);
        AnchorPane.setBottomAnchor(strikeCard.getChildren().get(1), 8.0);
        AnchorPane.setLeftAnchor(strikeCard.getChildren().get(1), 124.0);
        AnchorPane.setRightAnchor(strikeCard.getChildren().get(1), 180.0);

        strikeCard.getChildren().add(2, descriptionLabel);
        AnchorPane.setTopAnchor(strikeCard.getChildren().get(2), 8.0);
        AnchorPane.setBottomAnchor(strikeCard.getChildren().get(2), 8.0);
        AnchorPane.setLeftAnchor(strikeCard.getChildren().get(2), 244.0);
        AnchorPane.setRightAnchor(strikeCard.getChildren().get(2), 9.0);

        strikeCard.setFocusTraversable(true);
        strikeCard.getStylesheets().add(String.valueOf(getClass().getResource("css/styleStrikeCard.css")));
        strikeCard.getStyleClass().add("strikeCard");
        strikeCard.setCursor(Cursor.HAND);

        return strikeCard;
    }

    private double computeLayoutY(int i) {
        return 20.0 + (41 + 11) * i;
    }

    private double computePaneHeight(int i) {
        if (i<5){
            return 236.0;
        }
        return 236.0 + (41 + 11) * (i - 4);
    }

    @FXML
    void clickParticipate(ActionEvent event) {
        strikeParticipationHandler.clickedParticipate(selectedStrikeName, selectedStrikeDate);
    }

}


