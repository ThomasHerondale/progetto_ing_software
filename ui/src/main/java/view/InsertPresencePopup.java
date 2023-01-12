package view;

import controller.InsertPresenceHandler;
import entities.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InsertPresencePopup {

    @FXML
    private Button confirmButton;

    @FXML
    private Label dateLabel;

    @FXML
    private ComboBox<String> workersBox;

    private InsertPresenceHandler insertPresenceHandler;
    private List<Worker> abstents;
    private Worker worker;
    private LocalDate date;

    public InsertPresencePopup(List<Worker> abstents, LocalDate currentDate, InsertPresenceHandler insertPresenceHandler) {
        this.insertPresenceHandler = insertPresenceHandler;
        this.date = currentDate;
        this.abstents = abstents;
    }

    @FXML
    public void initialize(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateLabel.setText(date.format(formatter));
        for (Worker abstent : abstents) {
            workersBox.getItems().add(abstent.getId() + " - " + abstent.getFullName());
        }
        workersBox.setOnAction(this::onWorkerSelected);
    }

    private void onWorkerSelected(ActionEvent actionEvent) {
        for (Worker abstent : abstents){
            if (workersBox.getValue().equals(abstent.getId() + " - " + abstent.getFullName())){
                worker = abstent;
            }
        }
    }

    @FXML
    public void clickConfirm(ActionEvent event) {
        if (worker != null){
            insertPresenceHandler.clickedConfirm(worker);
        }
    }

}
