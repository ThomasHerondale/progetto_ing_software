package database;

import entities.Worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Properties;

public class DBMSDaemon {
    private Connection connection;
    private static DBMSDaemon instance;

    private DBMSDaemon() {
        var connectionProperties = new Properties();
        connectionProperties.put("user", "gabri");
        connectionProperties.put("password", "gabri");
        connectionProperties.put("database", "project");
        try {
            this.connection =
                    DriverManager.getConnection("jdbc:mysql://localhost:3306", connectionProperties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBMSDaemon getInstance() {
        return instance;
    }

    public boolean checkCredentials(String id, String password) {
        // TODO:
        return false;
    }

    public boolean checkCredentials(String id, String name, String surname) {
        // TODO:
        return false;
    }

    public String getFullName(String id) {
        // TODO:
        return null;
    }

    public boolean isFirstAccess(String id) {
        // TODO:
        return false;
    }

    public Map<String, String> getQuestionsList() {
        // TODO:
        return null;
    }

    public void registerSafetyQuestion(String id, String question, String answer) {
        // TODO:
    }

    public Map<String, String> getPasswordRetrievalInfo(String id) {
        // TODO:
        return null;
    }

    public boolean checkAnswer(String id, String answer) {
        // TODO:
        return false;
    }

    // TODO: tre metodi getMailData da analizzare?

    public long getLastID() {
        return 0;
    }

    public void createWorker(Worker worker, LocalDate birthDate, String birthPlace,
                             char sex, String ssn, char rank) {
        // TODO:
    }

    public void registerPassword(String id, String password) {
        // TODO:
    }

    public void createStrike(String name, String description, LocalDate date, Map<String, String> ranks) {
        // TODO:
    }

    public void insertHolidayInterruption(LocalDate startDate, LocalDate endDate) {
        // TODO:
    }

    public void getWorkerInfo(String ID) {
        // TODO:
    }
}
