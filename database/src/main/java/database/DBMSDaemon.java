package database;

import commons.HoursRecap;
import commons.Period;
import commons.WorkerStatus;
import entities.Shift;
import entities.Worker;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static commons.WorkerStatus.*;

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
     * Ottiene l'attuale conteggio dei ritardi del dipendente specificato.
     * @param id la matricola del dipendente
     * @return il conteggio dein ritardi
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public int getDelayCounter(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT delayCount
                FROM counters
                WHERE refWorkerID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo conteggio per dipendente */

            return Integer.parseInt(maps.get(0).get("delayCount"));
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
     * @apiNote essendo la mappa {@code <String, String>} gli <i>int</i> ai valori della mappa corrispondono a stringhe
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
                """);
                var countSt = connection.prepareStatement("""
                INSERT INTO counters(refWorkerID)
                VALUES (?)
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

            countSt.setString(1, worker.getId());

            st.execute();
            countSt.execute();
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
                insert into Strike(strikeName, strikeDate, descriptionStrike, A, B, C, D, H)
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

            assert paramCounter == 9;

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

    /**
     * Ottiene dal database le informazioni relative al dipendente specificato.
     * @param id la matricola del dipendente
     * @return una mappa del tipo {("birthdate", date), ("birthPlace", string), ("sex", char), ("SSN", string),
     * ("delayCount", int), ("autoExitCount", int), ("holidayCount", int), ("availabilityParentalLeave", int)}
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote si veda la documentazione di {@link DBMSDaemon#getPresencesList} per chiarimenti sulla
     * mappa di ritorno
     */
    public Map<String, String> getWorkerInfo(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT birthdate, birthplace, sex, SSN,
                delayCount, autoExitCount, holidayCount, availabilityParentalLeave
                FROM worker JOIN counters c ON worker.ID = c.refWorkerID
                WHERE ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();
            
            var maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per matricola */
            
            return maps.get(0);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene casualmente dal database la mail di un dipendente del settore amministrativo.
     * @return la mail di uno dei dipendenti amministrativi
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public String getAdminEmail() throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT email
                FROM worker
                WHERE workerRank = 'H'
                ORDER BY RAND()
                LIMIT 1;
                """)
        ) {
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);
            assert maps.size() == 1; /* Deve ritornare per forza una mail */

            return maps.get(0).get("email");
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
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
     * Ottiene la lista dei periodi di ferie richiesti da ogni dipendente, dalla data specificata in poi.
     * @param date la data da cui iniziare il controllo
     * @return una mappa di coppie (id, lista_ferie)
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Map<String, List<Period>> getRequestedHolidays(LocalDate date) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT refWorkerID, startDate, endDate
                FROM abstention
                WHERE startDate >= ? AND type = 'Holiday'
                """)
        ) {
            st.setDate(1, Date.valueOf(date));
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);

            Map<String, List<Period>> holidays = new HashMap<>();
            for (var map : maps) {
                var id = map.get("refWorkerID");
                var startDate = LocalDate.parse(map.get("startDate"));
                var endDate = LocalDate.parse(map.get("endDate"));

                if (!holidays.containsKey(id))
                    holidays.put(id, new ArrayList<>());

                holidays.get(id).add(new Period(startDate, endDate));
            }

            return holidays;
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
                set delayCount = 0 , autoExitCount = 0, holidayCount = 0, consumedParentalLeave = 0
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
     * Ottiene lo stato attuale di tutti i dipendenti dell'azienda.
     * @param date la data di riferimento
     * @return una mappa di coppie (id, status)
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Map<String, WorkerStatus> getWorkersStatus(LocalDate date) throws DBMSException {
        try (
                var strikeSt = connection.prepareStatement("""
                SELECT strikeparticipation.refWorkerID
                FROM strikeparticipation
                WHERE refStrikeDate = ?
                """);
                var abstSt = connection.prepareStatement("""
                SELECT refWorkerID, startDate, endDate, type
                FROM abstention
                WHERE endDate > ?
                """);
                var presSt = connection.prepareStatement("""
                SELECT refShiftID
                FROM presence
                WHERE refShiftDate = ? AND exitTime IS NULL
                """)
        ) {
            var workers = getWorkersList();

            strikeSt.setDate(1, Date.valueOf(date));
            abstSt.setDate(1, Date.valueOf(date));
            presSt.setDate(1, Date.valueOf(date));

            var strikeResultSet = strikeSt.executeQuery();
            var abstResultSet = abstSt.executeQuery();
            var presResultSet = presSt.executeQuery();

            var strikeMaps = extractResults(strikeResultSet);
            var abstentionMaps = extractResults(abstResultSet);
            var presenceMaps = extractResults(presResultSet);

            Map<String, WorkerStatus> statusMap = new HashMap<>(workers.size());

            /* Imposta lo stato in sciopero per chi ha aderito a scioperi oggi */
            for (var strikeMap : strikeMaps)
                statusMap.put(strikeMap.get("refWorkerID"), STRIKING);

            for (var abstentionMap : abstentionMaps) {
                /* Estrai la data di inizio e di fine del periodo di astensione */
                var start = LocalDate.parse(abstentionMap.get("startDate"));
                var end = LocalDate.parse(abstentionMap.get("endDate"));

                /* Se la data di oggi ricade in questo periodo */
                if (Period.comprehends(start, end, date)) {
                    /* Imposta lo stato in base al tipo di astensione */
                    WorkerStatus status = switch (abstentionMap.get("type")) {
                        case "Holiday", "Leave" -> ON_HOLIDAY;
                        case "ParentalLeave" -> PARENTAL_LEAVE;
                        case "Illness" -> ILL;
                        default -> throw new AssertionError("Errore nel tipo di astensione del db.");
                    };
                    /* Ogni impiegato dovrebbe essere in astensione solo in un modo */
                    assert !statusMap.containsKey(abstentionMap.get("refWorkerID"));
                    statusMap.put(abstentionMap.get("refWorkerID"), status);
                }
            }

            for (var presenceMap : presenceMaps) {
                assert !statusMap.containsKey(presenceMap.get("refShiftID"));
                statusMap.put(presenceMap.get("refShiftID"), WORKING);
            }

            /* Se non sono in nessuna delle precedenti, sono liberi */
            for (var worker : workers) {
                if (!statusMap.containsKey(worker.getId()))
                    statusMap.put(worker.getId(), FREE);
            }

            assert statusMap.size() == workers.size();

            return statusMap;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene i dati dell'account memorizzati nel database del dipendente specificato.
     * @param id la matricola del dipendente
     * @return una mappa del tipo {("ID", string), ("workerName", string), ("workerSurname", string),
     * ("telNumber", string), ("email", string), ("IBAN", string), ("delayCount", int), ("autoExitCount", int),
     * ("holidayCount", int), ("availabilityParentalLeave", int)}
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote essendo la mappa {@code <String, String>} gli <i>int</i> ai valori della mappa corrispondono a stringhe
     * contenenti interi, da castare con {@link Integer#parseInt(String)}
     */
    public Map<String, String> getAccountData(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select W.ID, W.workerName, W.workerSurname, W.telNumber, W.email, W.IBAN,
                C.delayCount, C.autoExitCount, C.holidayCount, C.availabilityParentalLeave
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
                insert into counters(refWorkerID, availabilityParentalLeave)
                values(?, ?)
                on duplicate key update availabilityParentalLeave = ?
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
     * Ottiene la lista degli scioperi autorizzati presenti nel database per il livello specificato, a cui il
     * dipendente specificato non ha già prestato adesione.
     * @param id la matricola del dipendente
     * @param rank il livello di cui ottenere gli scioperi
     * @return una lista di mappe, ognuna rappresentante un singolo sciopero. Ogni mappa è formata da coppie
     * (nome_attributo, valore_attributo)
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<HashMap<String, String>> getAuthorizedStrikes(String id, char rank) throws DBMSException {
        var queryStr = """
                select strikeName, strikeDate, descriptionStrike
                from Strike
                """;
        queryStr = queryStr + "where Strike." + rank + " = true " +
                "and (strikeName, strikeDate) NOT IN (SELECT refStrikeName, refStrikeDate\n" +
                "                                       FROM strikeparticipation\n" +
                "                                       WHERE refWorkerID = ?)";
        try (
                var st = connection.prepareStatement(queryStr)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

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
                select availabilityParentalLeave
                from counters
                where refWorkerID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Ci dovrebbe essere un solo conteggio per dipendente */

            var map = maps.get(0);
            var counter = Integer.parseInt(map.get("availabilityParentalLeave"));
            return counter >= dayCount;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /** Ottiene la lista di turni in un periodo. */
    private List<Shift> getShiftsList(String id, LocalDate startDate, LocalDate endDate) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select W.ID, W.workerName, W.workerSurname, W.workerRank, W.telNumber, W.email, W.IBAN,
                S.shiftRank, S.shiftDate, S.shiftStart, S.shiftEnd
                from Worker W join Shift S on (W.ID = S.refWorkerID)
                where refWorkerID = ? AND shiftDate BETWEEN ? AND ?
                """)
        ) {
            st.setString(1, id);
            st.setDate(2, Date.valueOf(startDate));
            st.setDate(3, Date.valueOf(endDate));

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
                var upSt1 = connection.prepareStatement("""
                update Counters
                set availabilityParentalLeave = availabilityParentalLeave - ?
                where refWorkerID = ?
                """);
                var upSt2 = connection.prepareStatement("""
                update Counters
                set consumedParentalLeave = counters.consumedParentalLeave + ?
                where refWorkerID = ?
                """)
        ) {
            /* Riempi l'insert */
            inSt.setString(1, id);
            inSt.setDate(2, Date.valueOf(startDate));
            inSt.setDate(3, Date.valueOf(endDate));

            /* Calcola le ore di congedo parentale */
            var shifts = getShiftsList(id, startDate, endDate);

            var hourCount = 0;
            for (var shift : shifts) {
                hourCount += shift.getHours();
            }

            /* Riempi gli update */
            upSt1.setInt(1, hourCount);
            upSt1.setString(2, id);
            upSt2.setInt(1, hourCount);
            upSt2.setString(2, id);

            inSt.execute();
            upSt1.execute();
            upSt2.execute();
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
         /*  Ottiene i giorni di ferie */
        var dayCount = Period.dayCount(startDate, endDate);

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

            /* Calcola i giorni di ferie */
            var dayCount = Period.dayCount(startDate, endDate);

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
     * Ottiene la lista dei turni assegnati al dipendente specificato.
     * @param id la matricola del dipendente
     * @return la lista dei turni relativi al dipendente
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<Shift> getShiftsList(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                select W.ID, W.workerName, W.workerSurname, W.workerRank, W.telNumber, W.email, W.IBAN,
                S.shiftRank, S.shiftDate, S.shiftStart, S.shiftEnd
                from Worker W join Shift S on ( W.ID = S.refWorkerID )
                where ID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);

            // TODO: duplicato!
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
     * Ottiene dal database i dati necessari al calcolo dello stipendio di tutti i dipendenti con riferimento
     * al periodo (mese) specificato.
     * @param referencePeriod il periodo corrente su cui calcolare lo stipendio
     * @return una mappa di coppie ({@link Worker}, {@link HoursRecap})
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Map<Worker, HoursRecap> getWorkersData(Period referencePeriod) throws DBMSException {
        var workers = getWorkersList(); /* Ottieni la lista di tutti i dipendenti */

        /* Ottieni le ore divise per categoria*/
        Map<String, Integer> ordinaryHours = new HashMap<>();
        Map<String, Integer> overtimeHours = new HashMap<>();
        Map<String, Integer> parentalLeaveHours = new HashMap<>();
        try (
                var st = connection.prepareStatement("""
                SELECT ordinary_hours_table.ID, ordinary_hours,
                COALESCE(overtime_hours, 0) AS overtime_hours,
                consumedParentalLeave AS parentalLeave_hours
                FROM
                    (SELECT ID,
                            SUM(TIMESTAMPDIFF(HOUR, entryTime, exitTime)) AS ordinary_hours
                     FROM presence JOIN shift s ON s.refWorkerID = presence.refShiftID AND
                                                   s.shiftDate = presence.refshiftDate AND
                                                   s.shiftStart = presence.refshiftStart
                                   JOIN worker w ON w.ID = s.refWorkerID
                     WHERE overTimeFlag = FALSE AND
                         shiftDate BETWEEN ? AND ?
                     GROUP BY ID) AS ordinary_hours_table
                LEFT OUTER JOIN
                    (SELECT ID,
                            SUM(TIMESTAMPDIFF(HOUR, entryTime, exitTime)) AS overtime_hours
                    FROM presence JOIN shift s ON s.refWorkerID = presence.refShiftID AND
                                                   s.shiftDate = presence.refshiftDate AND
                                                   s.shiftStart = presence.refshiftStart
                        JOIN worker w ON w.ID = s.refWorkerID
                    WHERE overTimeFlag = TRUE AND
                        shiftDate BETWEEN ? AND ?
                    GROUP BY ID) AS overtime_hours_table
                ON ordinary_hours_table.ID = overtime_hours_table.ID
                JOIN
                counters
                ON refWorkerID = ordinary_hours_table.ID;
                """)
        ) {
            st.setDate(1, Date.valueOf(referencePeriod.start()));
            st.setDate(2, Date.valueOf(referencePeriod.end()));
            st.setDate(3, Date.valueOf(referencePeriod.start()));
            st.setDate(4, Date.valueOf(referencePeriod.end()));
            var resultSet = st.executeQuery();
            var maps = extractResults(resultSet);

            /* Riduci la lista di mappe a mappe di coppie (ID, tot_ore) */
            for (var map : maps) {
                ordinaryHours.put(map.get("ID"), Integer.parseInt(map.get("ordinary_hours")));
                overtimeHours.put(map.get("ID"), Integer.parseInt(map.get("overtime_hours")));
                parentalLeaveHours.put(map.get("ID"), Integer.parseInt(map.get("parentalLeave_hours")));
            }

            /* Assembla gli oggetti di tipo HoursRecap */
            Map<Worker, HoursRecap> data = new HashMap<>();
            for (var worker : workers) {
                var id = worker.getId();
                try {
                    data.put(worker, new HoursRecap(
                            ordinaryHours.get(id),
                            overtimeHours.get(id),
                            parentalLeaveHours.get(id)
                    ));
                } catch (NullPointerException e) {
                    System.err.println("Cannot compute salary for id: " + id);
                }
            }

            return data;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene dal databse i flag che contrassegnano il turno specificato come straordinario o sostituzione.
     * @param shift il turno di cui ottenere i flag
     * @return una copia del turno specificato con i flag settati dal database
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Shift getShiftFlags(Shift shift) throws DBMSException {
        try (
            var st = connection.prepareStatement("""
            SELECT overTimeFlag, subFlag
            FROM shift
            WHERE refWorkerID = ? AND shiftDate = ? AND shiftStart = ?
            """)
        ) {
            st.setString(1, shift.getOwner().getId());
            st.setDate(2, Date.valueOf(shift.getDate()));
            st.setTime(3, Time.valueOf(shift.getStartTime()));
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo turno con questi dati */

            var map = maps.get(0);
            var overtimeFlag = map.get("overTimeFlag").equals("1");
            var substitutionFlag = map.get("subFlag").equals("1");
            shift.setFlags(overtimeFlag, substitutionFlag);

            return shift;
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene la data di caricamento dell'ultimo stipendio.
     */
    private LocalDate getLastSalaryDate(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT MAX(s.salaryDate) AS salary_date
                FROM salary s
                WHERE s.refWorkerID = ?
                """)
        ) {
            st.setString(1, id);
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per matricola */
            System.out.println(LocalDate.parse(maps.get(0).get("salary_date")));
            return LocalDate.parse(maps.get(0).get("salary_date"));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene dal database i dati utilizzati dal sistema per il calcolo dell'ultimo stipendio
     * del dipendente specificato.
     * @param id la matricola del dipendente
     * @return una mappa con una coppia ({@link HoursRecap}, stipendio)
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Map<HoursRecap, Double> getWorkerSalaryData(String id) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT amount, ordinary_hours, overtime_hours, parentalLeave_hours
                FROM
                    (SELECT refWorkerID, amount
                     FROM salary s
                     WHERE refWorkerID = ? AND salaryDate = ?
                    ) AS amount_table
                    JOIN
                    (SELECT ordinary_hours_table.ID, ordinary_hours,
                COALESCE(overtime_hours, 0) AS overtime_hours,
                consumedParentalLeave AS parentalLeave_hours
                FROM
                    (SELECT ID,
                            SUM(TIMESTAMPDIFF(HOUR, entryTime, exitTime)) AS ordinary_hours
                     FROM presence JOIN shift s ON s.refWorkerID = presence.refShiftID AND
                                                   s.shiftDate = presence.refshiftDate AND
                                                   s.shiftStart = presence.refshiftStart
                                   JOIN worker w ON w.ID = s.refWorkerID
                     WHERE overTimeFlag = FALSE AND
                         shiftDate BETWEEN ? AND ?
                     GROUP BY ID) AS ordinary_hours_table
                LEFT OUTER JOIN
                    (SELECT ID,
                            SUM(TIMESTAMPDIFF(HOUR, entryTime, exitTime)) AS overtime_hours
                    FROM presence JOIN shift s ON s.refWorkerID = presence.refShiftID AND
                                                   s.shiftDate = presence.refshiftDate AND
                                                   s.shiftStart = presence.refshiftStart
                        JOIN worker w ON w.ID = s.refWorkerID
                    WHERE overTimeFlag = TRUE AND
                        shiftDate BETWEEN ? AND ?
                    GROUP BY ID) AS overtime_hours_table
                ON ordinary_hours_table.ID = overtime_hours_table.ID
                JOIN
                counters
                ON refWorkerID = ordinary_hours_table.ID ) AS counters_table
                ON counters_table.ID = amount_table.refWorkerID
                """)
        ) {
            /* Ottieni la data dell'ultimo stipendio e calcola il suo periodo di riferimento.
            Questo periodo andrà da un mese prima alla data ottenuta alla data ottenuta stessa. */
            var endDate = getLastSalaryDate(id); // TODo: forse un giorno indietro...
            var startDate = endDate.minusMonths(1);

            st.setString(1, id);
            st.setDate(2, Date.valueOf(endDate));
            st.setDate(3, Date.valueOf(startDate));
            st.setDate(4, Date.valueOf(endDate));
            st.setDate(5, Date.valueOf(startDate));
            st.setDate(6, Date.valueOf(endDate));

            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo dipendente per matricola */

            /* Assembla i risultati */
            var map = maps.get(0);
            var ordinaryHours = Double.parseDouble(map.get("ordinary_hours"));
            var overtimeHours = Double.parseDouble(map.get("overtime_hours"));
            var parentalLeaveHours = Double.parseDouble(map.get("parentalLeave_hours"));
            HoursRecap recap = new HoursRecap(ordinaryHours, overtimeHours, parentalLeaveHours);

            return Map.of(recap, Double.parseDouble(map.get("amount")));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Memorizza lo stipendio relativo al mese specificato per il dipendente specificato.
     * @param id la matricola del dipendente
     * @param date la data di caricamento dello stipendio
     * @param salary l'importo dello stipendio
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void setSalary(String id, LocalDate date, Double salary) throws DBMSException {
        try (
                var inSt = connection.prepareStatement("""
                INSERT INTO Salary(refWorkerID, salaryDate, amount)
                VALUES (?, ?, ?)
                """);
                /* Query per salvare il contatore usato per il calcolo per usi futuri */
                var upSt = connection.prepareStatement("""
                UPDATE counters
                SET lastParentalLeaveRequest = consumedParentalLeave
                WHERE refWorkerID = ?
                """)
        ) {
            inSt.setString(1, id);
            inSt.setDate(2, Date.valueOf(date));
            inSt.setDouble(3, salary);
            upSt.setString(1, id);

            inSt.execute();
            upSt.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene la lista dei turni del giorno specificato che risultano terminati da più di mezz'ora rispetto
     * all'ora specificata, ma per cui non risulta un'uscita registrata.
     * @param date la data di riferimento dei turni
     * @param time l'ora rispetto a cui sarà eseguito il controllo
     * @return la lista dei turni terminati da più di mezz'ora che non hanno un'uscita registrata
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<Shift> getExitMissingShifts(LocalDate date, LocalTime time) throws DBMSException {
        /* Otterremo i turni finiti da più di mezz'ora senza uscita */
        time = time.minus(30, ChronoUnit.MINUTES);

        try (
                var st = connection.prepareStatement("""
                SELECT W.ID, W.workerName, W.workerSurname, W.workerRank, W.telNumber, W.email, W.IBAN,
                S.shiftRank, S.shiftDate, S.shiftStart, S.shiftEnd
                FROM Shift S join Presence P
                    on (S.refWorkerID = P.refShiftID and S.shiftDate = P.refShiftDate
                    and S.shiftStart = P.refShiftStart)
                    JOIN worker w ON w.ID = S.refWorkerID
                WHERE P.refShiftDate = ? and P.exitTime IS NULL and S.shiftEnd <= ?;
                """)
        ) {
            st.setDate(1, Date.valueOf(date));
            st.setTime(2, Time.valueOf(time));
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);

            if (maps.isEmpty()) {
                return Collections.emptyList();
            } else {
                List<Shift> shifts = new ArrayList<>();

                for (var map : maps) {
                    var shiftOwner = new Worker(
                            map.get("ID"),
                            map.get("workerName"),
                            map.get("workerSurname"),
                            map.get("workerRank").charAt(0),
                            map.get("telNumber"),
                            map.get("email"),
                            map.get("IBAN")
                    );
                    shifts.add(new Shift(
                            shiftOwner,
                            map.get("shiftRank").charAt(0),
                            LocalDate.parse(map.get("shiftDate")),
                            LocalTime.parse(map.get("shiftStart")),
                            LocalTime.parse(map.get("shiftEnd"))
                    ));
                }

                return shifts;
            }
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Registra l'ingresso al lavoro per il dipendente specificato, in una data e ad un'ora specifici.
     * @param id la matricola del dipendente
     * @param date la data di ingresso
     * @param time l'ora di ingresso
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote l'ingresso viene registrato nel database relativamente al turno che inizia prima di tutti gli altri
     */
    public void recordEntrance(String id, LocalDate date, LocalTime time) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                INSERT INTO Presence(refShiftID, refShiftDate, refShiftStart, entryTime)
                VALUES (?, ?, ?, ?)
                """)
        ) {
            /* Ottieni l'orario di inizio del primo turno */
            Optional<LocalTime> startTimeOpt = getShiftStartTime(id, date);
            assert startTimeOpt.isPresent();
            var startTime = startTimeOpt.get();

            st.setString(1, id);
            st.setDate(2, Date.valueOf(date));
            st.setTime(3, Time.valueOf(startTime));
            st.setTime(4, Time.valueOf(time));
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Registra l'ingresso in ritardo per il dipendente specificato, in una data e ad un'ora specifici.
     * @param id la matricola del dipendente
     * @param date la data di ingresso in ritardo
     * @param time l'orario di ingresso
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void recordDelay(String id, LocalDate date, LocalTime time) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                UPDATE counters
                SET delayCount = delayCount + 1
                WHERE refWorkerID = ?
                """)
        ) {
            st.setString(1, id);

            /* Registra l'ingresso */
            recordEntrance(id, date, time);

            /* Registra il ritardo */
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Registra automaticamente l'uscita per i turni specificati, settandola all'orario di fine del turno
     * stesso.
     * @param shifts i turni di cui registrare l'uscita
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void recordAutoExit(List<Shift> shifts) throws DBMSException {
        for (var shift : shifts)
            recordExit(shift.getOwner().getId(), shift.getDate(), shift.getStartTime(), shift.getEndTime());
    }

    /**
     * Registra l'uscita dal lavoro per il dipendente specificato, in una data e ad un'ora specifici.
     * @param id la matricola del dipendente
     * @param date la data del turno
     * @param exitTime l'ora di uscita da memorizzare
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void recordExit(String id, LocalDate date, LocalTime exitTime) throws DBMSException {
        recordExit(id, date, getLastMissingExitShiftStart(id, date), exitTime);
    }

    /**
     * Registra l'uscita nel database.
     */
    private void recordExit(String id, LocalDate date, LocalTime shiftStartTime, LocalTime exitTime)
            throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                UPDATE presence
                SET exitTime = ?
                WHERE refShiftID = ? and refShiftDate = ? and refShiftStart = ?
                """)
        ) {
            st.setTime(1, Time.valueOf(exitTime));
            st.setString(2, id);
            st.setDate(3, Date.valueOf(date));
            st.setTime(4, Time.valueOf(shiftStartTime));
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ritorna l'orario di inizio del turno più recente senza uscita.
     */
    private LocalTime getLastMissingExitShiftStart(String id, LocalDate date) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT MIN(p2.refShiftStart) AS shiftStart
                FROM presence p2
                WHERE p2.refShiftID = ?
                AND p2.refShiftDate = ?
                AND p2.exitTime IS NULL
                """)
        ) {
            st.setString(1, id);
            st.setDate(2, Date.valueOf(date));
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);
            assert maps.size() == 1; /* Dovrebbe esserci un solo minimo */

            return LocalTime.parse(maps.get(0).get("shiftStart"));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene dal database l'orario di inizio del primo turno del dipendente specificato che non presenta registrato
     * un ingresso nella data specificata.
     * @param id la matricola del dipendente
     * @param date la data di riferimento
     * @return un {@link Optional} contenente l'orario di inizio del primo turno senza ingresso del dipendente, se esso
     * esiste, altrimenti un {@link Optional} vuoto
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Optional<LocalTime> getShiftStartTime(String id, LocalDate date) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT MIN(shiftStart) AS shiftStart
                FROM shift
                WHERE refWorkerID = ?
                AND shiftDate = ? AND NOT EXISTS(
                                                 SELECT *
                                                 FROM presence
                                                 WHERE refShiftID = refWorkerID AND
                                                 refShiftDate = shiftDate AND
                                                 refShiftStart = shiftStart
                                                 )
                """)
        ) {
            st.setString(1, id);
            st.setDate(2, Date.valueOf(date));
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);
            assert maps.size() <= 1; /* Dovrebbe esserci al più un solo minimo */

            if (maps.isEmpty())
                return Optional.empty();
            else
                return Optional.ofNullable(LocalTime.parse(maps.get(0).get("shiftStart")));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene dal database l'orario di fine del primo turno del dipendente specificato che presenta registrato un
     * ingresso ma non un'uscita nella data specificata.
     * @param id la matricola del dipendente
     * @param date la data di riferimento
     * @return un {@link Optional} contenente l'orario di fine del primo turno senza uscita del dipendente, se esso
     * esiste, altrimenti un {@link Optional} vuoto
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public Optional<LocalTime> getShiftEndTime(String id, LocalDate date) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT S.shiftEnd
                FROM Shift S join Presence P
                    on (S.refWorkerID = P.refShiftID and S.shiftDate = P.refShiftDate
                    and S.shiftStart = P.refShiftStart)
                    JOIN worker w ON w.ID = S.refWorkerID
                WHERE S.refWorkerID = ? AND P.refShiftDate = ? AND P.exitTime IS NULL;
                """)
        ) {
            st.setString(1, id);
            st.setDate(2, Date.valueOf(date));
            var resultSet = st.executeQuery();

            var maps = extractResults(resultSet);
            assert maps.size() <= 1; /* In teoria non dovrebbero esserci più di un turno senza uscita... */

            if (maps.isEmpty())
                return Optional.empty();
            else
                return Optional.ofNullable(LocalTime.parse(maps.get(0).get("shiftEnd")));
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Registra la presenza al lavoro per il dipendente specificato nella data specificata.
     * @param id la matricola del dipendente
     * @param date la data in cui il dipendente risulterà presente
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote la presenza verrà registrata sul primo turno disponibile nella giornata
     */
    public void recordPresence(String id, LocalDate date) throws DBMSException {
       try (
               var st = connection.prepareStatement("""
               INSERT INTO Presence(refShiftID, refShiftDate, refShiftStart, entryTime, exitTime)
               VALUES (?, ?, ?, ( -- query per ottenere l'orario di inizio turno
                                    SELECT shiftStart
                                    FROM shift
                                    WHERE refWorkerID = ? AND shiftDate = ? AND shiftStart = ?
                                ), ( -- query per ottenere l'orario di fine turno
                                    SELECT shiftEnd
                                    FROM shift
                                    WHERE refWorkerID = ? AND shiftDate = ? AND shiftStart = ?)
                     )
               """)
       ) {
           Optional<LocalTime> shiftStartOpt = getShiftStartTime(id, date);
           assert shiftStartOpt.isPresent();
           var shiftStart = shiftStartOpt.get();

           st.setString(1, id);
           st.setDate(2, Date.valueOf(date));
           st.setTime(3, Time.valueOf(shiftStart));
           st.setString(4, id);
           st.setDate(5, Date.valueOf(date));
           st.setTime(6, Time.valueOf(shiftStart));
           st.setString(7, id);
           st.setDate(8, Date.valueOf(date));
           st.setTime(9, Time.valueOf(shiftStart));
           st.execute();
       } catch (SQLException e) {
           throw new DBMSException(e);
       }
    }

    /**
     * Ottiene dal database la lista dei lavoratori presenti al lavoro (i.e. entrati ma non usciti)
     * nella data specificata.
     * @param currentDate la data di riferimento
     * @return una mappa del tipo {("ID", string), ("workerName", string), ("workerSurname", string),
     * "shiftRank", char), ("entryTime", time)}
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     * @apiNote essendo la mappa {@code <String, String>} i <i>char</i> e <i>time</i> ai valori della mappa
     * sono le loro rappresentazioni in forma di stringa, opportunamente da castare al bisogno.
     */
    public List<HashMap<String, String>> getPresencesList(LocalDate currentDate) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT W.ID, W.workerName, W.workerSurname, S.shiftRank, P.entryTime
                FROM Worker W join Shift S on ( W.ID = S.refWorkerID) join Presence P on ( S.refWorkerID= P.refShiftID
                and S.shiftDate = P.refShiftDate and S.shiftStart = P.refShiftStart)
                WHERE P.refShiftDate = ? AND P.exitTime IS NULL;
                """)
        ) {
            st.setDate(1, Date.valueOf(currentDate));
            var resultSet = st.executeQuery();

            return extractResults(resultSet);
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * Ottiene la lista dei dipendenti assenti per astensione nella data specificata.
     * @param date la data di riferimento
     * @return la lista dei dipendenti assenti
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public List<Worker> getAbsentWorkersList(LocalDate date) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                SELECT DISTINCT W.ID, W.workerName, W.workerSurname, W.telNumber, W.email, W.IBAN, W.workerRank
                FROM Worker W join Shift S1 on (W.ID = S1.refWorkerID)
                WHERE S1.shiftDate = ? AND NOT EXISTS(
                                                     SELECT *
                                                     FROM presence
                                                     WHERE refShiftID = S1.refWorkerID AND
                                                     refShiftStart = S1.shiftStart AND
                                                     refShiftDate = S1.shiftDate)
                """)
        ) {
            st.setDate(1, Date.valueOf(date));
            var resultSet = st.executeQuery();

            List<HashMap<String, String>> maps = extractResults(resultSet);

            List<Worker> workers = new ArrayList<>(maps.size());
            for (var map : maps) {
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
     * Memorizza nel database la sostituzione dei proprietari dei due turni specificati.
     * @param absent il turno ricaduto nel periodo di astensione
     * @param substitute il turno del sostituto
     * @throws DBMSException se si verifica un errore di qualunque tipo, in relazione al database
     */
    public void setSubstitution(Shift absent, Shift substitute) throws DBMSException {
        try (
                var st1 = connection.prepareStatement("""
                UPDATE shift
                SET refWorkerID = ?, subFlag = TRUE
                WHERE refWorkerID = ? AND shiftDate = ? AND shiftStart = ?
                """);
                var st2 = connection.prepareStatement("""
                UPDATE shift
                SET refWorkerID = ?, subFlag = TRUE
                WHERE refWorkerID = ? AND shiftDate = ? AND shiftStart = ?
                """)
        ) {
            st1.setString(1, substitute.getOwner().getId());
            st1.setString(2, absent.getOwner().getId());
            st1.setDate(3, Date.valueOf(absent.getDate()));
            st1.setTime(4, Time.valueOf(absent.getStartTime()));
            st2.setString(1, absent.getOwner().getId());
            st2.setString(2, substitute.getOwner().getId());
            st2.setDate(3, Date.valueOf(substitute.getDate()));
            st2.setTime(4, Time.valueOf(substitute.getStartTime()));

            st1.execute();
            st2.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * @implNote <b> - Solo per uso interno- </b>
     */
    public void setOvertime(Shift toRemove, Shift ...toInsert) throws DBMSException {
        if (toInsert.length >= 2) throw new AssertionError("Troppi turni da inserire!");

        for (var shift : toInsert) {
            try (
                    var st = connection.prepareStatement("""
                    INSERT INTO shift(refWorkerID, shiftRank, shiftDate, shiftStart, shiftEnd, overTimeFlag)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """)
            ) {
                st.setString(1, shift.getOwner().getId());
                st.setString(2, String.valueOf(shift.getRank()));
                st.setDate(3, Date.valueOf(shift.getDate()));
                st.setTime(4, Time.valueOf(shift.getStartTime()));
                st.setTime(5, Time.valueOf(shift.getEndTime()));
                st.setInt(6, 1);
                st.execute();

                removeShift(toRemove);
            } catch (SQLException e) {
                throw new DBMSException(e);
            }
        }
    }

    /**
     * @implNote <b> - Solo per uso interno- </b>
     */
    public void removeShift(Shift toRemove) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                DELETE FROM shift
                WHERE refWorkerID = ? AND shiftDate = ? AND shiftStart = ?
                """)
        ) {
            st.setString(1, toRemove.getOwner().getId());
            st.setDate(2, Date.valueOf(toRemove.getDate()));
            st.setTime(3, Time.valueOf(toRemove.getStartTime()));
            st.execute();
        } catch (SQLException e) {
            throw new DBMSException(e);
        }
    }

    /**
     * @implNote <b> - Solo per uso interno- </b>
     */
    public void insertShift(Shift toInsert) throws DBMSException {
        try (
                var st = connection.prepareStatement("""
                INSERT INTO shift(refWorkerID, shiftRank, shiftDate, shiftStart, shiftEnd)
                VALUES (?, ?, ?, ?, ?)
                """)
        ) {
            st.setString(1, toInsert.getOwner().getId());
            st.setString(2, String.valueOf(toInsert.getRank()));
            st.setDate(3, Date.valueOf(toInsert.getDate()));
            st.setTime(4, Time.valueOf(toInsert.getStartTime()));
            st.setTime(5, Time.valueOf(toInsert.getEndTime()));
            st.execute();
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
