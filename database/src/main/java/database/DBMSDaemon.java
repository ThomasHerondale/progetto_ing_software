package database;

import entities.Worker;

import java.sql.*;
import java.sql.Date;
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
     * @return true se le credenziali corrispondono, false altrimenti
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public boolean checkCredentials(String id, String password) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select w.ID, s.workerPassword
                from worker w join security s on w.ID = s.refWorkerID
                where ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet  = st.executeQuery();

            if (isResultEmpty(resultSet)) {
                /* Se la query ha ritornato l'insieme vuoto, la matricola non esiste */
                return false;
            } else {
                /* Altrimenti ottieni le credenziali dal resultSet e controlla che corrispondano */
                List<HashMap<String, String>> maps = extractResults(resultSet);
                assert maps.size() == 1; /* Dovrebbe esserci solo una tupla nel risultato */

                HashMap<String, String> result = maps.get(0);
                var dbId = result.get("ID");
                var dbPassword = result.get("workerPassword");

                return id.equals(dbId) && dbPassword.equals(password);
            }
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Verifica che le credenziali specificate esistano e corrispondano con quelle nel database.
     * @param id la matricola da controllare
     * @param name il nome da controllare
     * @param surname il cognome da controllare
     * @return true se le credenziali corrispondono, false altrimenti
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public boolean checkCredentials(String id, String name, String surname) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select w.ID, w.workerName, w.workerSurname
                from worker w
                where w.ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            if (isResultEmpty(resultSet)) {
                /* Se la query ha ritornato l'insieme vuoto, la matricola non esiste */
                return false;
            } else {
                /* Altrimenti ottieni le credenziali dal resultSet e controlla che corrispondano */
                List<HashMap<String, String>> maps = extractResults(resultSet);
                assert maps.size() == 1; /* Dovrebbe esserci solo una tupla nel risultato */

                HashMap<String, String> result = maps.get(0);
                var dbId = result.get("ID");
                var dbName = result.get("workerName");
                var dbSurname = result.get("workerSurname");

                return id.equals(dbId) && name.equals(dbName) && surname.equals(dbSurname);
            }
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene dal database il nome completo del dipendente associato alla matricola specificata.
     * @param id la matricola del dipendente
     * @return una stringa del tipo "nome cognome" del dipendente
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public String getFullName(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select w.workerName, w.workerSurname
                from worker w
                where w.ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            assert !isResultEmpty(resultSet);

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per id */

            HashMap<String, String> result = maps.get(0);

            return result.get("workerName") + " " + result.get("workerSurname");
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Verifica che il dipendente associato alla matricola specificata debba ancora eseguire il primo accesso.
     * @param id la matricola del dipendente
     * @return true se il dipendente non ha ancora effettuato il primo accesso, false altrimenti
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public boolean isFirstAccess(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select firstAccessFlag
                from worker join security s on worker.ID = s.refWorkerID
                where worker.ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            assert !isResultEmpty(resultSet); /* Le credenziali esistono, viene controllato prima */

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo flag per matricola */

            HashMap<String, String> result = maps.get(0);

            /* Se il flag è settato a 0, il primo accesso è ancora da fare */
            return result.get("firstAccessFlag").equals("0");
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene dal database la lista di tutte le possibili domande di sicurezza, insieme al loro ID.
     * @return una mappa contenente coppie (ID, domanda)
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Map<String, String> getQuestionsList() throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select q.IDQuestion, q.question
                from securityquestion q
                """)
        ) {
            var resultSet = st.executeQuery();
            assert !isResultEmpty(resultSet); /* Dovrebbero sempre esserci domande nel database */

            List<HashMap<String, String>> maps = extractResults(resultSet);

            var questionsList = new HashMap<String, String>();
            for (var map : maps) {
                /* Rimuovi i nomi delle colonne, creando coppie (ID, domanda) */
                questionsList.put(map.get("IDQuestion"), map.get("question"));
            }
            return questionsList;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Registra la domanda di sicurezza specificata e la relativa risposta per l'utente specificato.
     * Imposta inoltre a vero il flag di primo accesso per l'utente specificato.
     * @param id la matricola dell'utente
     * @param questionId l'id della domanda di sicurezza nel database
     * @param answer la risposta da associare alla domanda
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void registerSafetyQuestion(String id, String questionId, String answer) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                update security
                set firstAccessFlag = true,
                    refQuestionID = ?,
                    answer = ?
                where refWorkerID = ?
                """)
        ) {
            st.setString(1, questionId);
            st.setString(2, answer);
            st.setString(3, id);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }


    public Map<String, String> getPasswordRetrievalInfo(String id) {
        // TODO:
        return null;
    }

    /**
     * Controlla che la risposta specificata alla domanda di sicurezza del dipendente specificato
     * corrisponda con quella inserita al momento del primo accesso.
     * @param id la matricola del dipendente
     * @param answer la risposta da controllare
     * @return true se la risposta corrisponde, false altrimenti
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public boolean checkAnswer(String id, String answer) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select s.answer
                from worker w join security s on w.ID = s.refWorkerID
                where w.ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            assert !isResultEmpty(resultSet);

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per id */

            HashMap<String, String> result = maps.get(0);

            return answer.equals(result.get("answer"));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    // TODO: tre metodi getMailData da analizzare?


    /**
     * Ottiene la matricola più grande presente nel database.
     * @return il massimo dell'insieme delle matricole dei dipendenti nel database
     * @throws SQLException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public long getLastid() throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select max(w.ID) as lastId
                from worker w
                """)
        ) {
            var resultSet = st.executeQuery();

            /* Spostati sulla prima e unica riga */
            resultSet.next();

            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Inserisce un dipendente nel database usando i dati specificati.
     * @param worker l'oggetto {@link Worker} contenente alcuni dei dati
     * @param birthDate la data di nascita del dipendente
     * @param birthPlace il luogo di nascita del dipendente
     * @param sex il sesso del dipendente
     * @param ssn il codice fiscale del dipendente
     * @param rank il livello del dipendente
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void createWorker(Worker worker, LocalDate birthDate, String birthPlace,
                             char sex, String ssn, char rank) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into Worker(ID, workerName, workerSurname, birthDate, birthplace, sex,
                SSN, workerRank, IBAN, telNumber, email)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)
        ) {
            st.setString(1, worker.getId());
            st.setString(2, worker.getName());
            st.setString(3, worker.getSurname());
            st.setDate(4, Date.valueOf(birthDate));
            st.setString(5, birthPlace);
            st.setString(6, String.valueOf(sex));
            st.setString(7, ssn);
            st.setString(8, String.valueOf(rank));
            st.setString(9, worker.getIban());
            st.setString(10, worker.getPhone());
            st.setString(11, worker.getEmail());
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    // TODO: problema perché è update o insert?
    public void registerPassword(String id, String password) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into Security (refWorkerID, workerPassword)
                values (?, ?)
                """)
        ) {
            st.setString(1, id);
            st.setString(2, password);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Inserisce uno sciopero nel database usando i dati specificati.
     * @param name il nome dello sciopero
     * @param description la descrizione dello sciopero
     * @param date la data dello sciopero
     * @param ranks una mappa di coppie (livello, stringa_flag), dove stringa_flag è una stringa
     *              "true" o "false"
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void createStrike(String name, String description, LocalDate date, Map<Character, String> ranks)
            throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into Strike(strikeName, strikeDate, descriptionStrike, A, B, C, D, Adm)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """)
        ) {
            /* Assicurati che i valori della mappa siano solo stringhe "true" e "false" */
            if (!ranks.values().stream().allMatch(str -> str.equals("true") || str.equals("false")))
                throw new AssertionError("ranks deve contenere solo flag, i.e." +
                        "stringhe \"true\" o \"false\".");
            /* Assicurati che le chiavi della mappa siano solo caratteri corrispondenti ai ranghi */
            if (!ranks.keySet().containsAll(List.of('A', 'B', 'C', 'D', 'H')))
                throw new AssertionError("ranks risulta mancante dei flag per alcuni livelli.");

            st.setString(1, name);
            st.setDate(2, Date.valueOf(date));
            st.setString(3, description);

            /* Setta i flag dei livelli nella query */
            int paramCounter = 4;
            for (var rank : List.of('A', 'B', 'C', 'D', 'H')) {

                var strFlag = ranks.get(rank);
                var intflag = strFlag.equals("true") ? 1 : 0;

                st.setInt(paramCounter, intflag);
                paramCounter++;
            }

            assert paramCounter == 8;

            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Inserisce un periodo di blocco delle ferie nel database.
     * @param startDate la data di inizio del periodo di blocco
     * @param endDate la data di fine del periodo di blocco
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void insertHolidayInterruption(LocalDate startDate, LocalDate endDate) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into HolidayInterruption(startDate, endDate)
                values (?, ?)
                """)
        ) {
            st.setDate(1, Date.valueOf(startDate));
            st.setDate(2, Date.valueOf(endDate));
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
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
    

}
