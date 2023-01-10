package mail;

import entities.Shift;
import entities.Worker;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static mail.MailWriter.*;

/**
 * Questa classe gestisce l'invio delle e-mail di notifica dell'azienda. La classe implementa il pattern
 * di design <i>Singleton</i>, in modo da avere un'unica gestione centralizzata per tutte le e-mail.
 * Le e-mail saranno inviate tutte da un'unico indirizzo aziendale ai diversi destinatari.
 */
public class MailManager {
    /**
     * L'indirizzo da cui saranno inviate tutte le e-mail.
     */
    private static final String COMPANY_ADDRESS = "thomasherondale@gmail.com";
    /**
     * L'istanza di questa classe, secondo il pattern <i>Singleton</i>.
     */
    private static MailManager instance;
    /**
     * La sessione di SMTP che la classe useerà per inviare le mail.
     */
    private final Session session;

    /* Costruttore privato per impedire l'istanziazione di questa classe. */
    private MailManager() {
        /* Imposta le variabili relative al protocollo SMTP */
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        /* Crea una nuova sessione del protocollo SMTP */
        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        COMPANY_ADDRESS, "cobapheqjhhlcsij"
                );
            }
        });

        //TODO: session.setDebug(true);
    }

    /**
     * Ritorna l'unica istanza possibile di {@link MailManager}, creandola se questa non è mai stata creata.
     * @return l'istanza di {@link MailManager}
     */
    public static MailManager getInstance() {
        /* Se instance è null ritorna una nuova instanza, altrimenti ritorna instance */
        return Objects.requireNonNullElseGet(instance, MailManager::new);
    }

    /**
     * Notifica il dipendente specificato della sua assunzione, e gli comunica la password
     * generata per lui dal sistema.
     * @param mail la mail del nuovo dipendente destinatario
     * @param fullName nome e cognome del dipendente destinario
     * @param password la password generata per il nuovo dipendente
     */
    public void notifyHiring(String mail, String fullName, String password) {
        sendEmail(mail, "Sei assunto!", hiringNotice(fullName, password));
    }

    /**
     * Invia la nuova password generata al dipendente specificato, in risposta a una sua richiesta di reset
     * della password.
     * @param mail la mail del dipendente destinatario
     * @param fullName nome e cognome del dipendente destinario
     * @param password la nuova password generata per il dipendente
     */
    public void notifyNewPassword(String mail, String fullName, String password) {
        sendEmail(mail, "Reset password", passwordRetrievalEmail(fullName, password));
    }

    /**
     * Notifica il dipendente della registrazione automatica di un'uscita a suo nome.
     * @param mail la mail del dipendente destinatario
     * @param fullName nome e cognome del dipendente destinario
     */
    public void notifyAutoExitRecord(String mail, String fullName) {
        sendEmail(mail, "Uscita automatica registrata", autoExitRecordedNotice(fullName));
    }

    /**
     * Notifica l'ufficio amministrativo del superamento del limite di uscite automatiche per il
     * dipendente specificato, e notifica il dipendente del superamento e dell'avvertimento
     * all'amministrativo.
     * @param adminMail la mail dell'amministrativo destinatario
     * @param workerMail la mail del dipendente destinatario
     * @param workerFullname nome e cognome del dipendente destinario
     * @apiNote invia due email, una all'amministrativo e una al dipendente
     */
    public void notifyAutoExitLimitReached(String adminMail,
                                           String workerMail,
                                           String workerFullname) {
        var subject = workerFullname + " - Limite uscite auto superato";
        sendEmail(adminMail, subject, autoExitLimitReachedAlert(workerFullname));
        sendEmail(workerMail, "Limite uscite auto superato",
                autoExitLimitReachedNotice(workerFullname));
    }

    /**
     * Notifica l'ufficio amministrativo del superamento del limite di ritardi per il dipendente
     * specificato, e notifica il dipendente del superamento e dell'avvertimento all'amministrativo.
     * @param adminMail la mail dell'amministrativo destinatario
     * @param workerMail la mail del dipendente destinatario
     * @param workerFullName nome e cognome del dipendente destinario
     * @apiNote invia due email, una all'amministrativo e una al dipendente
     */
    public void notifyDelayLimitReached(String adminMail,
                                        String workerMail,
                                        String workerFullName) {
        var subject = workerFullName + " - Limite ritardi superato";
        sendEmail(adminMail, subject, delayLimitReachedAlert(workerFullName));
        sendEmail(workerMail, "Limite ritardi superato",
                delayLimitReachedNotice(workerFullName));
    }

    /**
     * Notifica tutti i dipendenti specificati della nuova proposta di turnazione trimestrale.
     * @param mails la lista delle mail di tutti i dipendenti
     */
    public void notifyNewShiftProposal(List<String> mails) {
        for (var mail : mails) {
            sendEmail(mail, "Nuova turnazione", newShiftProposalNotice());
        }
    }

    /**
     * Notifica tutti i dipendenti specificati del calcolo del nuovo stipendio del mese corrente, e del suo
     * importo.
     * @param workersMap una mappa contenente coppie (dipendente, stipendio)
     */
    public void notifyNewSalary(Map<Worker, Double> workersMap) {
        for (var entry : workersMap.entrySet()) {
            /* Una entry workerData è una coppia (mail, stipendio) */
            sendEmail(entry.getKey().getEmail(), "Nuovo stipendio",
                    newSalaryNotice(entry.getKey().getFullName(), entry.getValue()));
        }
    }

    /**
     * Notifica il dipendente a cui è stata assegnata una sostituzione a favore di un altro dipendente, e
     * il dipendente in questione dell'avvenuta copertura di un turno.
     * @param substitute il dipendente assegnatario della sostituzione
     * @param absent il dipendente da sostituire
     * @param shift il turno cui fa riferimento la sostituzione
     * @apiNote invia due email, una al sostituto e una al richiedente astensione
     */
    public void notifySubstitution(Worker substitute, Worker absent, Shift shift) {
        sendEmail(substitute.getEmail(), "Sostituzione assegnata",
                substitutionAlert(
                        substitute.getFullName(), absent.getFullName(),
                        shift.getDate(), shift.getStartTime(), shift.getEndTime(), shift.getRank()
                ));
        sendCoverageMail(absent, shift);
    }

    /**
     * Notifica il dipendente a cui è stato assegnato uno straordinario a favore di un altro dipendente.
     * @param overtimer il dipendente assegnatario dello straordinario
     * @param absent il dipendente da coprire
     * @param shift il turno cui fa riferimento lo straordinario
     * @apiNote invia due email, una all'assegnatario dello straordinario e una al richiedente astensione
     */
    public void notifyOvertime(Worker overtimer, Worker absent, Shift shift) {
        sendEmail(overtimer.getEmail(), "Straordinario assegnato",
                overtimeAlert(
                        overtimer.getFullName(), absent.getFullName(),
                        shift.getDate(), shift.getStartTime(), shift.getEndTime(), shift.getRank()
                ));
        sendCoverageMail(absent, shift);
    }

    /**
     * Notifica il dipendente che ha richiesto un permesso del rifiuto dell'inserimento dello stesso da parte del
     * sistema.
     * @param absent il dipendente richiedente il permesso
     * @param leaveDate la data del permesso
     * @param leaveStart l'ora di inizio del permesso
     * @param leaveEnd l'ora di fine del permesso
     */
    public void notifyLeaveDenial(Worker absent, LocalDate leaveDate, LocalTime leaveStart, LocalTime leaveEnd) {
        sendEmail(absent.getEmail(), "Permesso rifiutato",
                leaveDenialNotice(absent.getFullName(), leaveDate, leaveStart, leaveEnd, absent.getRank()));
    }

    /**
     * Invia l'e-mail con l'oggetto e il corpo specificati, al destinatario specificato.
     */
    private void sendEmail(String recipient, String subject, String body) {
        try {
            /* Crea il messaggio */
            var message = new MimeMessage(this.session);

            /* Compila la mail */
            message.setFrom(new InternetAddress(COMPANY_ADDRESS));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);

            /* Invia il messaggio */
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Intesta e invia la mail di conferma copertura di un turno.
     */
    private void sendCoverageMail(Worker absent, Shift shift) {
        sendEmail(absent.getEmail(), "Assenza coperta",
                coverageNotice(
                        absent.getFullName(),
                        shift.getDate(), shift.getStartTime(), shift.getEndTime(), shift.getRank()
                ));
    }
}
