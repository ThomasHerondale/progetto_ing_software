package mail;

import entities.Worker;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

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
    MailManager instance;
    /**
     * Le variabili di sistema e del protoccolo SMTP.
     */
    Properties properties;
    /**
     * La sessione di SMTP che la classe useerà per inviare le mail.
     */
    Session session;

    /* Costruttore privato per impedire l'istanziazione di questa classe. */
    private MailManager() {
        /* Imposta le variabili relative al protocollo SMTP */
        this.properties = System.getProperties();
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

        session.setDebug(true);
    }

    /**
     * Ritorna l'unica istanza possibile di {@link MailManager}, creandola se questa non è mai stata creata.
     * @return l'istanza di {@link MailManager}
     */
    public MailManager getInstance() {
        /* Se instance è null ritorna una nuova instanza, altrimenti ritorna instance */
        return Objects.requireNonNullElseGet(instance, MailManager::new);
    }

    /**
     * Notifica il dipendente specificato della sua assunzione, e gli comunica la password
     * generata per lui dal sistema.
     * @param mail la mail del nuovo dipendente destinatario
     * @param name il nome del nuovo dipendente
     * @param surname il cognome del nuovo dipendente
     * @param password la password generata per il nuovo dipendente
     */
    public void notifyHiring(String mail, String name, String surname, String password) {
        sendEmail(mail, "Sei assunto!", hiringNotice(name, surname, password));
    }

    /**
     * Invia la nuova password generata al dipendente specificato, in risposta a una sua richiesta di reset
     * della password.
     * @param mail la mail del dipendente destinatario
     * @param name il nome del dipendente
     * @param surname il cognome del dipendente
     * @param password la nuova password generata per il dipendente
     */
    public void notifyNewPassword(String mail, String name, String surname, String password) {
        sendEmail(mail, "Reset password", passwordRetrievalEmail(name, surname, password));
    }

    /**
     * Notifica il dipendente della registrazione automatica di un'uscita a suo nome.
     * @param mail la mail del dipendente destinatario
     * @param name il nome del dipendente
     * @param surname il cognome del dipendente
     */
    public void notifyAutoExitRecord(String mail, String name, String surname) {
        sendEmail(mail, "Uscita automatica registrata", autoExitRecordedNotice(name, surname));
    }

    /**
     * Notifica l'ufficio amministrativo del superamento del limite di uscite automatiche per il
     * dipendente specificato, e notifica il dipendente del superamento e dell'avvertimento
     * all'amministrativo.
     * @param adminMail la mail dell'amministrativo destinatario
     * @param workerMail la mail del dipendente destinatario
     * @param workerName il nome del dipendente
     * @param workerSurname il cognome del dipendente
     * @apiNote invia due email, una all'amministrativo e una al dipendente
     */
    public void notifyAutoExitLimitReached(String adminMail,
                                           String workerMail,
                                           String workerName,
                                           String workerSurname) {
        var subject = workerName + " " +  workerSurname + " - Limite uscite auto superato";
        sendEmail(adminMail, subject, autoExitLimitReachedAlert(workerName, workerSurname));
        sendEmail(workerMail, "Limite uscite auto superato",
                autoExitLimitReachedNotice(workerName, workerSurname));
    }

    /**
     * Notifica l'ufficio amministrativo del superamento del limite di ritardi per il dipendente
     * specificato, e notifica il dipendente del superamento e dell'avvertimento all'amministrativo.
     * @param adminMail la mail dell'amministrativo destinatario
     * @param workerMail la mail del dipendente destinatario
     * @param workerName il nome del dipendente
     * @param workerSurname il cognome del dipendente
     * @apiNote invia due email, una all'amministrativo e una al dipendente
     */
    public void notifyDelayLimitReached(String adminMail,
                                        String workerMail,
                                        String workerName,
                                        String workerSurname) {
        var subject = workerName + " " +  workerSurname + " - Limite ritardi superato";
        sendEmail(adminMail, subject, delayLimitReachedAlert(workerName, workerSurname));
        sendEmail(workerMail, "Limite ritardi superato",
                delayLimitReachedNotice(workerName, workerSurname));
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
                    newSalaryNotice(entry.getKey().getFullName(), "", entry.getValue()));
        }
    }

    public void notifySubstitution() {
        // TODO: Sostituzione model da inserire al posto di tutto
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

    // TODO: overtime alert!

}
