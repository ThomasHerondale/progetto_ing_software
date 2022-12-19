package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

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
     * L'oggetto che si occupa di convertire un'istanza di {@link LocalTime} in una stringa in formato
     * HH-MM.
     */
    private static final DateTimeFormatter TIME_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.HOUR_OF_DAY)
                    .appendLiteral(':')
                    .appendValue(ChronoField.MINUTE_OF_HOUR).toFormatter();
    /**
     * L'oggetto che si occupa di convertire un'istanza di {@link LocalDate} in una stringa
     * in formato "GG-MM-AAAA".
     */
    private static final DateTimeFormatter DATE_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.DAY_OF_MONTH)
                    .appendLiteral('-')
                    .appendValue(ChronoField.MONTH_OF_YEAR)
                    .appendLiteral('-')
                    .appendValue(ChronoField.YEAR).toFormatter();
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
     * Compone l'e-mail che sarà inviata al dipendente neoassunto per notificargli l'assunzione
     * e comunicargli la sua prima password.
     */
    private static String composeHiringNoticeEmail(String name, String surname, String password) {
        return "Congratulazioni,\n" +
               name + " " + surname + "!\n" +
               "Sei stato assunto presso la nostra azienda! Abbiamo creato per te " +
               "un account all'interno del nostro sistema.\n" +
               "La tua prima password è: " + password + "\n" +
               "Ti informiamo che al tuo primo accesso ti sarà chiesto di impostare " +
               "una domanda di sicurezza, nel caso in cui dovessi dimenticare la password.\n" +
               "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente richiedente il reset della password.
     */
    private static String composePasswordRetrievalEmail(String name, String surname, String password) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Come da te richiesto, la tua nuova password è: " + password + ".\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli la registrazione
     * automatica di un'uscita per un suo turno.
     */
    private static String composeAutoExitRecordedNotice(String name, String surname) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Ti informiamo che è stata registrata automaticamente un'uscita per il tuo ultimo turno, " +
                "in quanto risultava mancante di registrazione uscita da più di mezz'ora rispetto all'orario di " +
                "fine previsto.\n" +
                "Sei pregato di ricordarti di registrare in tempo le uscite. \n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata all'amministrativo per comunicargli il superamento del limite
     * di uscite automatiche da parte del dipendente specificato.
     */
    private static String composeAutoExitLimitReachedAlert(String name, String surname) {
        return "Buongiorno.\n" +
                "Ti scriviamo per notificarti che l'impiegato " + name + " " + surname +
                " ha superato il limite di uscite registrate in automatico per questo trimestre.\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli il superamento del suo limite
     * di uscite automatiche.
     */
    private static String composeAutoExitLimitReachedNotice(String name, String surname) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Ti comunichiamo che il sistema ha automaticamente avvisato un impiegato del settore " +
                "amministrativo, in quanto hai superato il limite di uscite registrate in automatico " +
                "per questo trimestre.\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata all'amministrativo per comunicargli il superamento
     * del limite di ritardi da parte del dipendente specificato.
     */
    private static String composeDelayLimitReachedAlert(String name, String surname) {
        return "Buongiorno.\n" +
                "Ti scriviamo per notificarti che l'impiegato " + name + " " + surname +
                " ha superato il limite di ritardi per questo trimestre.\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli il superamento
     * del suo limite di ritardi.
     */
    private static String composeDelayLimitReachedNotice(String name, String surname) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Ti comunichiamo che il sistema ha automaticamente avvisato un impiegato del settore " +
                "amministrativo, in quanto hai superato il limite di ritardi in ingresso " +
                "per questo trimestre.\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli la nuova
     * proposta di turnazione relativa al successivo trimestre.
     */
    private static String newShiftProposalNotice(String name, String surname) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Ti scriviamo per notificarti che è disponibile la nuova turnazione per il " +
                "trimestre successivo. Ti preghiamo di prenderne visione e comunicare in anticipo, " +
                "se possibile, le tue richieste di astensione.\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli il calcolo del suo stipendio
     * relativo al mese corrente.
     */
    private static String newSalaryAlert(String name, String surname) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Ti scriviamo per notificarti che è disponibile il nuovo stipendio " +
                "calcolato per il mese corrente. Ti preghiamo di prenderne visione.\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente {@code recipient} per comunicargli l'assegnazione a suo carico
     * di sostituire il dipendente {@code absentWorker} per il turno specificato.
     */
    private static String substitutionAlert(
            String recipientName,
            String recipientSurname,
            String absentWorkerName,
            String absentWorkerSurname,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            char rank
            ) {
        return "Ciao, " +
                recipientName + " " + recipientSurname + ".\n" +
                "Ti scriviamo per comunicarti che ti è stata assegnata una sostituzione presso l'ufficio " +
                parseRankChar(rank) + ", per il turno in data " + date.format(DATE_FORMATTER) + ", dalle "
                + startTime.format(TIME_FORMATTER) + " alle " + endTime.format(TIME_FORMATTER) + ".\n" +
                "----------------- \n" +
                "Ecco un riepilogo della sostituzione: \n" +
                "Impiegato richiedente l'astensione: " + absentWorkerName + " " + absentWorkerSurname + "\n" +
                "Livello di servizio dell'impiegato: " + parseRankChar(rank) + "\n" +
                "Data e ora sostituzione: " + date.format(DATE_FORMATTER) + " " +
                startTime.format(TIME_FORMATTER) + "-" + endTime.format(TIME_FORMATTER) + ".\n" +
                "Buon lavoro!";
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente specificato per comunicargli che
     * il sistema è riuscito a coprire il turno specificato, ricadente in un periodo di astensione da lui richiesto.
     */
    private static String coverageNotice(
            String name,
            String surname,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            char rank
    ) {
        return "Ciao,\n" +
                name + " " + surname + ".\n" +
                "Ti scriviamo per comunicarti che il turno in data " + date.format(DATE_FORMATTER) + ", dalle "
                + startTime.format(TIME_FORMATTER) + " alle " + endTime.format(TIME_FORMATTER) +
                " presso l'ufficio " + parseRankChar(rank) + ", ricadente in un periodo di astensione " +
                "da te richiesto, è stato adeguatamente coperto.\n" +
                "Buon lavoro!";
    }

    /**
     * Converte il carattere che rappresenta il livello dell'impiegato in una stringa. Usato per scrivere
     * "amministrativo" al posto della semplice "H".
     */
    private static String parseRankChar(char c) {
        assert List.of('A', 'B', 'C', 'D', 'H').contains(c);

        /* Minuscolo perché verra dopo la parola "ufficio" */
        return c == 'H' ? "amministrativo" : String.valueOf(c);
    }

    // TODO: overtimeAlert come funziona?


}
