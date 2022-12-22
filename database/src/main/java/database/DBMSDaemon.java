package database;

import entities.Worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
                    DriverManager.getConnection("jdbc:mysql://localhost:3306/project", connectionProperties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBMSDaemon getInstance() {
        if (instance == null) {
            instance = new DBMSDaemon();
        }
        return instance;
    }

    // TODO: err cmmdbms

    public boolean checkCredentials(String id, String password) {
        var result = formatExecute("""
                select w.ID, s.workerPassword
                from worker w join security s on w.ID = s.refWorkerID
                where ID = %s
                """, id);

        assert result != null;
        if (isResultEmpty(result)) {
            /* Se la query ha ritornato l'insieme vuoto, la matricola non esiste */
            return false;
        }
        else {
            /* Altrimenti ottieni le credenziali dal resultSet e controlla che corrispondano */
            String dbId = null;
            String dbPassword = null;
            
            try {
                result.next(); /* Sposta il puntatore sulla prima (e unica) tupla */

                dbId = result.getString("ID");
                dbPassword = result.getString("workerPassword");

                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return id.equals(dbId) && password.equals(dbPassword); 
        }
    }

    public static void main(String[] args) {
        var db = DBMSDaemon.getInstance();
        System.out.println(db.checkCredentials("0718424", "gabri"));
    }

    private ResultSet formatExecute(String query, Object... args) {
        try {
            var st = connection.createStatement();
            return st.executeQuery(String.format(query, args));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Non dovrebbe ritornare mai
    }

    private boolean isResultEmpty(ResultSet resultSet) {
        try {
            return !resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
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

    public long getLastid() {
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

    public void getWorkerInfo(String id) {
        // TODO:
    }

    public void changeEmail(String id, String newMail) {
        // TODO:
    }

    public void changeIban(String id, String newIban) {
        // TODO:
    }

    public void changePhone(String id, String newPhone) {
        // TODO:
    }

    public void promoteWorker(String id) {
        // TODO:
    }

    public void removeWorker(String id) {
        // TODO:
    }

    public void resetCounters() {
        // TODO:
    }

    public void getWorkersList() {
        // TODO:
    }

    // TODO: metodo getAccountData?

    public void enableParentalLeave(String id, int hours) {
        // TODO:
    }

    public void getAuthorizedStrikes(char rank) {
        // TODO:
    }
    public void getWorkerRank(String id) {
        // TODO:
    }

    // TODO: metodo setStrikeParticipation?

    public void checkParentalLeaveCounter(LocalDate startDate, LocalDate endDate) {
        // TODO:
    }

    public void setParentalLeavePeriod(String id, LocalDate startDate, LocalDate endDate) {
        // TODO:    
    }
    
    

}
