package database;

import commons.Counters;
import commons.Period;
import entities.Shift;
import entities.Worker;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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

    /**
     * Ottiene i dati memorizzati nel database del dipendente specificato.
     * @param id la matricola del dipendente
     * @return un oggetto {@link Worker} contenente tutti i dati ottenuti
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Worker getWorkerData(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select workerName, workerSurname, workerRank, telNumber, email, IBAN
                from worker
                where ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* A ogni id dovrebbe corrispondere un solo dipendente */

            var map = maps.get(0);
            return new Worker(id, map.get("workerName"), map.get("workerSurname"), map.get("workerRank").charAt(0),
                    map.get("telNumber"), map.get("email"), map.get("IBAN"));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene le informazioni relative al recupero della password per il dipendente specificato.
     * @param id la matricola del dipendente
     * @return una mappa del tipo {("firstAccessFlag", int), ("question", string)} se
     * la matricola specificata ha trovato riscontro nel database, altrimenti una mappa vuota {}
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote essendo la mappa <String, String> gli <i>int</i> ai valori della mappa corrispondono a stringhe
     * contenenti interi, da castare con {@link Integer#parseInt(String)}
     */
    public Map<String, String> getPasswordRetrievalInfo(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select firstAccessFlag, question
                from security left outer join securityquestion on refQuestionID = IDQuestion
                where refWorkerID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);

            if (maps.isEmpty())
                return Collections.emptyMap();

            assert maps.size() == 1; /* A ogni id dovrebbe corrispondere una sola domanda */
            return maps.get(0);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Controlla che la risposta specificata alla domanda di sicurezza del dipendente specificato
     * corrisponda con quella inserita al momento del primo accesso.
     * @param id la matricola del dipendente
     * @param answer la risposta da controllare
     * @return true se la risposta corrisponde, false altrimenti
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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

    // TODO: tre - ormai due - metodi getMailData da analizzare?

    /**
     * Ottiene le informazioni necessarie alla composizione di notifiche sottoforma di e-mail per il
     * dipendente specificato.
     * @param id la matricola del dipendente
     * @return una mappa del tipo {("name", string), ("surname", string), ("email", string)}
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Map<String, String> getMailData(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select workerName as name, workerSurname as surname, email
                from worker
                where ID = ?
               """)
        ) {
            st.setString(1, id);
            var resulSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resulSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per matricola */

            return maps.get(0);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene la matricola più grande presente nel database.
     * @return il massimo dell'insieme delle matricole dei dipendenti nel database
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
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

    /**
     * Memorizza nel database la password specificata per il dipendente specificato, eventualmente
     * sovrascrivendo quella precedente.
     * @param id la matricola del dipendente
     * @param password la nuova password del dipendente
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void registerPassword(String id, String password) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into Security (refWorkerID, workerPassword)
                values (?, ?)
                on duplicate key update workerPassword = ?
                """)
        ) {
            st.setString(1, id);
            st.setString(2, password);
            st.setString(3, password);
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

    /**
     * Sostituisce la mail dell'impiegato specificato memorizzata nel database con quella specificata.
     * @param id la matricola dell'impiegato
     * @param newMail la nuova mail
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void changeEmail(String id, String newMail) throws DBMSException {
       try (
               var st = connection.prepareStatement("""
               update Worker
               set email = ?
               where ID = ?
               """)
       ) {
           st.setString(1, newMail);
           st.setString(2, id);
           st.execute();
       } catch (SQLException e) {
           throw new DBMSException(e);
       }
    }

    /**
     * Sostituisce l'iban dell'impiegato specificato memorizzato nel database con quello specificato.
     * @param id la matricola dell'impiegato
     * @param newIban il nuovo iban
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void changeIban(String id, String newIban) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                update Worker
                set IBAN = ?
                where ID = ?
                """)
        ) {
            st.setString(1, newIban);
            st.setString(2, id);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Sostituisce il numero di telefono dell'impiegato specificato memorizzato nel database
     * con quello specificato.
     * @param id la matricola dell'impiegato
     * @param newPhone il nuovo numero di telefono
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void changePhone(String id, String newPhone) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                update Worker
                set telNumber = ?
                where ID = ?
                """)
        ) {
            st.setString(1, newPhone);
            st.setString(2, id);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Promuove il dipendente specificato al livello successivo, secondo la scala D > C > B > A.
     * @param id la matricola del dipendente
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote gli impiegati del livello Amministrativo 'H' non possono essere promossi
     */
    public void promoteWorker(String id) throws DBMSException {
        var rankSuccession = List.of('D', 'C', 'B', 'A');

        /* Calcola il livello dopo la promozione */
        var currentRank = getWorkerRank(id);
        assert rankSuccession.contains(currentRank);
        var newRank = rankSuccession.get(rankSuccession.indexOf(currentRank) + 1);

        try (
                var st = connection.prepareStatement("""
                update Worker
                set workerRank = ?
                where ID = ?
                """)
        ) {
            st.setString(1, String.valueOf(newRank));
            st.setString(2, id);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Rimuove dal database il dipendente specificato e tutti i dati ad esso associati.
     * @param id la matricola del dipendente
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void removeWorker(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                delete from worker
                where ID = ?
                """)
        ) {
            st.setString(1, id);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Resetta i contatori di tutti i dipendenti presenti nel database.
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void resetCounters() throws DBMSException {
        //noinspection SqlWithoutWhere
        try (
                var st = connection.prepareStatement("""
                update Counters
                set delayCount = 0 , autoExitCount = 0, holidayCount = 0
                """)
        ) {
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene la lista di tutti i dipendenti memorizzati nel database.
     * @return una lista di tutti i dipendenti presenti nel database
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<Worker> getWorkersList() throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select W.ID, W.workerName, W.workerSurname, W.workerRank, W.telNumber, W.email, W.IBAN
                from Worker W
                """)
        ) {
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);

            List<Worker> workers = new ArrayList<>(maps.size());
            for (var map : maps) {
                /* Crea un dipendente coi dati estratti dal database */
                workers.add(new Worker(
                        map.get("ID"),
                        map.get("workerName"),
                        map.get("workerSurname"),
                        map.get("workerRank").charAt(0),
                        map.get("telNumber"),
                        map.get("email"),
                        map.get("IBAN")
                ));
            }

            return workers;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene i dati dell'account memorizzati nel database del dipendente specificato.
     * @param id la matricola del dipendente
     * @return una mappa del tipo {("ID", string), ("workerName", string), ("workerSurname", string),
     * ("telNumber", string), ("email", string), ("IBAN", string), ("delayCount", int), ("autoExitCount", int),
     * ("holidayCount", int), ("parentalLeaveCount", int)}
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote essendo la mappa <String, String> gli <i>int</i> ai valori della mappa corrispondono a stringhe
     * contenenti interi, da castare con {@link Integer#parseInt(String)}
     */
    public Map<String, String> getAccountData(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select W.ID, W.workerName, W.workerSurname, W.telNumber, W.email, W.IBAN,
                C.delayCount, C.autoExitCount, C.holidayCount, C.parentalLeaveCount
                from Worker W join Counters C on (W.ID = C.refWorkerID)
                where W.ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per matricola */

            return maps.get(0);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Abilita il dipendente specificato a ricevere il congedo parentale per un totale di ore specificato.
     * @param id la matricola del dipendente
     * @param hours il numero di ore di congedo concesse
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void enableParentalLeave(String id, int hours) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into counters(refWorkerID, parentalLeaveCount)
                values(?, ?)
                on duplicate key update parentalLeaveCount = ?
                """)
        ) {
            st.setString(1, id);
            st.setInt(2, hours);
            st.setInt(3, hours);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene la lista degli scioperi autorizzati presenti nel database per il livello specificato.
     * @param rank il livello di cui ottenere gli scioperi
     * @return una lista di mappe, ognuna rappresentante un singolo sciopero. Ogni mappa è formata da coppie
     * (nome_attributo, valore_attributo)
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<HashMap<String, String>> getAuthorizedStrikes(char rank) throws DBMSException {
        var queryStr = """
                select strikeName, strikeDate, descriptionStrike
                from Strike
                """;
        queryStr = queryStr + "where Strike." + rank + " = true";
        try (
                var st = connection.createStatement()
        ) {
            var resultSet = st.executeQuery(queryStr);

            return extractResults(resultSet);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene il livello del dipendente specificato.
     * @param id la matricola del dipendente
     * @return il carattere associato al livello del dipendente ('H' per Amministrativo)
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Character getWorkerRank(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT workerRank
                FROM Worker
                WHERE ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente con un id */

            var map = maps.get(0);
            return map.get("workerRank").charAt(0); /* Converte la stringa in char */
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Memorizza nel database la partecipazione del dipendente specificato allo sciopero specificato.
     * @param id la matricola del dipendente
     * @param strikeName il nome dello sciopero
     * @param strikeDate la data di svolgimento dello sciopero
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void setStrikeParticipation(String id, String strikeName, LocalDate strikeDate) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into strikeparticipation(refStrikeName, refStrikeDate, refWorkerID)
                values (?, ?, ?)
                """)
                ) {
            st.setString(1, strikeName);
            st.setDate(2, Date.valueOf(strikeDate));
            st.setString(3, id);
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Verifica che il contatore delle ore di congedo parentale disponibili per il dipendente specificato
     * sia sufficiente a coprire le ore di congedo parentale richieste.
     * @param id la matricola del dipendente
     * @param startDate la data di inizio del periodo di congedo parentale
     * @param endDate la data di fine del periodo di congedo parentale
     * @return true se il contatore è sufficiente, false altrimenti
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public boolean checkParentalLeaveCounter(String id, LocalDate startDate, LocalDate endDate) throws DBMSException {
        /*  Ottiene i giorni e moltiplica per 24 per le ore */
        var dayCount = Period.dayCount(startDate, endDate) * 24;

        try (
                var st = connection.prepareStatement("""
                select parentalLeaveCount
                from counters
                where refWorkerID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Ci dovrebbe essere un solo conteggio per dipendente */

            var map = maps.get(0);
            var counter = Integer.parseInt(map.get("parentalLeaveCount"));
            return counter >= dayCount;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }

    }

    /**
     * Memorizza nel database il periodo di congedo parentale specificato richiesto dal dipendente specificato.
     * @param id la matricola del dipendente
     * @param startDate la data di inizio del periodo di congedo parentale
     * @param endDate la data di fine del periodo di congedo parentale
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    @SuppressWarnings("DuplicatedCode")
    public void setParentalLeavePeriod(String id, LocalDate startDate, LocalDate endDate) throws DBMSException {
        try (
                var inSt = connection.prepareStatement("""
                insert into abstention (refWorkerID, startDate, endDate, type)
                values (?, ?, ?, 'ParentalLeave')
                """);
                var upSt = connection.prepareStatement("""
                update Counters
                set parentalLeaveCount = parentalLeaveCount - ?
                where refWorkerID = ?
                """)
        ) {
            /* Riempi l'insert */
            inSt.setString(1, id);
            inSt.setDate(2, Date.valueOf(startDate));
            inSt.setDate(3, Date.valueOf(endDate));

            /* Calcola le ore di congedo parentale */
            var dayCount = Period.dayCount(startDate, endDate) * 24;

            /* Riempi l'update */
            upSt.setInt(1, dayCount);
            upSt.setString(2, id);

            inSt.execute();
            upSt.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene tutti i periodi di blocco delle ferie memorizzati nel database.
     * @return una lista di oggetti {@link Period} rappresentanti ciascuno un periodo di blocco
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<Period> getHolidayInterruptions() throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select startDate, endDate
                from holidayinterruption
                """)
        ) {
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);

            List<Period> holidayInterruptions = new ArrayList<>(maps.size());
            for (var map : maps) {
                var startDate = LocalDate.parse(map.get("startDate"));
                var endDate = LocalDate.parse(map.get("endDate"));
                holidayInterruptions.add(new Period(startDate, endDate));
            }

            return holidayInterruptions;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Verifica che il contatore dei giorni di ferie disponibili per il dipendente specificato sia sufficiente
     * a coprire i giorni di ferie richiesti.
     * @param id la matricola del dipendente
     * @param startDate la data di inizio del periodo di ferie
     * @param endDate la data di fine del periodo di ferie
     * @return true se il contatore è sufficiente, false altrimenti
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public boolean checkHolidayCounter(String id, LocalDate startDate, LocalDate endDate) throws DBMSException {
         /*  Ottiene i giorni e moltiplica per 24 per le ore */
        var dayCount = Period.dayCount(startDate, endDate) * 24;

        try (
                var st = connection.prepareStatement("""
                select holidayCount
                from counters
                where refWorkerID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Ci dovrebbe essere un solo conteggio per dipendente */

            var map = maps.get(0);
            var counter = Integer.parseInt(map.get("holidayCount"));
            return counter >= dayCount;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Memorizza nel database un periodo di ferie per il dipendente specificato.
     * @param id la matricola del dipendente
     * @param startDate la data di inizio del periodo di ferie
     * @param endDate la data di fine del periodo di ferie
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    @SuppressWarnings("DuplicatedCode")
    public void setHolidayPeriod(String id, LocalDate startDate, LocalDate endDate) throws DBMSException {
        try (
                var inSt = connection.prepareStatement("""
                insert into abstention (refWorkerID, startDate, endDate, type)
                values (?, ?, ?, 'Holiday')
                """);
                var upSt = connection.prepareStatement("""
                update Counters
                set holidayCount = holidayCount - ?
                WHERE refWorkerID = ?
                """)
        ) {
            inSt.setString(1, id);
            inSt.setDate(2, Date.valueOf(startDate));
            inSt.setDate(3, Date.valueOf(endDate));

            /* Calcola le ore di ferie */
            var dayCount = Period.dayCount(startDate, endDate) * 24;

            /* Riempi l'update */
            upSt.setInt(1, dayCount);
            upSt.setString(2, id);

            inSt.execute();
            upSt.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Memorizza nel database un periodo di malattia per il dipendente specificato.
     * @param id la matricola del dipendente
     * @param startDate la data di inizio del periodo di ferie
     * @param endDate la data di fine del periodo di ferie
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void setIllnessPeriod(String id, LocalDate startDate, LocalDate endDate) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                insert into abstention (refWorkerID, startDate, endDate, type)
                values (?, ?, ?, 'Illness')
                """)
        ) {
            st.setString(1, id);
            st.setDate(2, Date.valueOf(startDate));
            st.setDate(3, Date.valueOf(endDate));
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    // TODO: getData chi cazzu è?

    /**
     * Inserisce nel database i turni contenuti nella proposta di turnazione specificata.
     * @param shiftProposal la proposta di turnazione
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void uploadShiftProposal(List<Shift> shiftProposal) throws DBMSException {
        if (shiftProposal.isEmpty()) throw new AssertionError(); /* Dovrebbe esserci almeno un turno */

        /* Assembla la stringa per la query */
        var valuesBuilder = new StringBuilder();
        for (var shift : shiftProposal) {
            valuesBuilder.append("('");
            valuesBuilder.append(shift.getOwner().getId()).append("', '");
            valuesBuilder.append(shift.getRank()).append("', '");
            valuesBuilder.append(shift.getDate().toString()).append("', '");
            valuesBuilder.append(shift.getStartTime().toString()).append("', '");
            valuesBuilder.append(shift.getEndTime().toString()).append("'");
            valuesBuilder.append(")");
            valuesBuilder.append(", ");
        }

        /* Rimuovendo da valuesBuilder l'ultima virgola */
        var sql = "insert into shift (refWorkerID, shiftRank, shiftDate, shiftStart, shiftEnd) " +
                "values " + valuesBuilder.deleteCharAt(valuesBuilder.lastIndexOf(", "));
        try (var st = connection.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    // TODO: getWorkersDataList?

    /**
     * Ottiene la lista di tutti i turni memorizzati nel database.
     * @return una lista di tutti i turni presenti nel database
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<Shift> getShiftsList() throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select W.ID, W.workerName, W.workerSurname, W.workerRank, W.telNumber, W.email, W.IBAN,
                S.shiftRank, S.shiftDate, S.shiftStart, S.shiftEnd
                from Worker W join Shift S on ( W.ID = S.refWorkerID )
                """)
        ) {
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);

            List<Shift> shifts = new ArrayList<>(maps.size());
            for (var map : maps) {
                /* Crea un turno coi dati estratti dal database */
                shifts.add(new Shift(
                        new Worker(
                                map.get("ID"),
                                map.get("workerName"),
                                map.get("workerSurname"),
                                map.get("workerRank").charAt(0),
                                map.get("telNumber"),
                                map.get("email"),
                                map.get("IBAN")
                        ),
                        map.get("shiftRank").charAt(0),
                        LocalDate.parse(map.get("shiftDate")),
                        LocalTime.parse(map.get("shiftStart")),
                        LocalTime.parse(map.get("shiftEnd"))
                ));
            }

            return shifts;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
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
