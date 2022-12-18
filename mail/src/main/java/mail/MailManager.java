package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
     * Compone l'e-mail che sarà inviata all'impiegato neoassunto per notificargli l'assunzione
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
}
