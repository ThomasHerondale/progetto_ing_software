package database;

import entities.Worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

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

    /**
     * Verifica che le credenziali specificate esistano e corrispondano con quelle nel database.
     * @param id la matricola da controllare
     * @param password la password da controllare
     * @return true se le credenziali corrispondono, false altrimenti.
     */
    public boolean checkCredentials(String id, String password) {
        ResultSet resultSet = formatExecute("""
                select w.ID, s.workerPassword
                from worker w join security s on w.ID = s.refWorkerID
                where ID = %s
                """, id);


        assert resultSet != null; /* Solo per non fare lamentare il linter */
        if (isResultEmpty(resultSet)) {
            /* Se la query ha ritornato l'insieme vuoto, la matricola non esiste */
            return false;
        } else {
            String dbId = null;
            String dbPassword = null;

            /* Altrimenti ottieni le credenziali dal resultSet e controlla che corrispondano */
            try {
                List<HashMap<String, String>> maps = extractResults(resultSet);
                assert maps.size() == 1; /* Dovrebbe esserci solo una tupla nel risultato */

                var result = maps.get(0);

                dbId = result.get("ID");
                dbPassword = result.get("workerPassword");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return id.equals(dbId) && dbPassword.equals(password);
        }
    }

    /**
     * Esegue la query specificata nella stringa, formattata <i>c-printf-like</i> con gli argomenti specificati.
     */
    private ResultSet formatExecute(String query, Object... args) {
        try {
            var st = connection.createStatement();
            return st.executeQuery(String.format(query, args));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; /* Non dovrebbe ritornare mai */
    }

    /**
     * Estrae tutte le righe del resultSet specificato, convertendole in mappe (nome_colonna, valore_colonna).
     */
    private List<HashMap<String, String>> extractResults(ResultSet resultSet) throws SQLException {
        var results = new ArrayList<HashMap<String, String>>();

        /* Fino a quando c'è un'altra riga, vacci ed estrai i risultati in una mappa */
        while (resultSet.next()) {
            var rowMap = extractRow(resultSet);
            results.add(rowMap); /* Aggiungi la mappa all'array di mappe */
        }
        return results;
    }

    /**
     * Estrae una riga dal resultSet specificato e la converte in una mappa (nome_colonna, valore_colonna).
     */
    private HashMap<String, String> extractRow(ResultSet resultSet) throws SQLException {
        var labels = getColumnLabels(resultSet);
        var rowMap = new HashMap<String, String>();

        /* Per ogni colonna del risultato */
        for (String label : labels) {
            rowMap.put(label, resultSet.getString(label));
        }
        return rowMap;
    }

    /**
     * Ottiene i nomi delle colonne del resultSet specificato.
     */
    private String[] getColumnLabels(ResultSet resultSet) throws SQLException {
        var meta = resultSet.getMetaData();
        var labels = new ArrayList<String>();

        /* 1 ... il numero delle colonne + 1, poiché gli indici vanno da 1 */
        for (var i = 1; i < meta.getColumnCount() + 1; i++) {
            labels.add(meta.getColumnLabel(i));
        }

        return labels.toArray(new String[0]);
    }

    /**
     * Ritorna vero se il resultSet specificato è vuoto.
     */
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
