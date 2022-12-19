package mail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

/**
 * Questa classe di metodi statici si occupa semplicemente della stesura del corpo di tutte le notifiche
 * inviate dal sistema, sottoforma di e-mail.
 */
public class MailWriter {
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
     * La formula con cui saranno chiuse tutte le e-mail.
     */
    public static final String SALUTE = "Buon lavoro!";

    /* Costruttore privato per impedire l'instanziazione di questa classe. */
    private MailWriter() {

    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente neoassunto per notificargli l'assunzione
     * e comunicargli la sua prima password.
     */
    static String hiringNotice(String name, String surname, String password) {
        return "Congratulazioni,\n" +
                name + " " + surname + "!\n" +
                "Sei stato assunto presso la nostra azienda! Abbiamo creato per te " +
                "un account all'interno del nostro sistema.\n" +
                "La tua prima password è: " + password + "\n" +
                "Ti informiamo che al tuo primo accesso ti sarà chiesto di impostare " +
                "una domanda di sicurezza, nel caso in cui dovessi dimenticare la password.\n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente richiedente il reset della password.
     */
    static String passwordRetrievalEmail(String name, String surname, String password) {
        return "Ciao, " +
                name + " " + surname + ".\n" +
                "Come da te richiesto, la tua nuova password è: " + password + ".\n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli la registrazione
     * automatica di un'uscita per un suo turno.
     */
    static String autoExitRecordedNotice(String name, String surname) {
        return composeHeader(name, surname) +
                "Ti informiamo che è stata registrata automaticamente un'uscita per il tuo ultimo turno, " +
                "in quanto risultava mancante di registrazione uscita da più di mezz'ora rispetto all'orario di " +
                "fine previsto.\n" +
                "Sei pregato di ricordarti di registrare in tempo le uscite. \n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata all'amministrativo per comunicargli il superamento del limite
     * di uscite automatiche da parte del dipendente specificato.
     */
    static String autoExitLimitReachedAlert(String name, String surname) {
        return "Buongiorno.\n" +
                "Ti scriviamo per notificarti che l'impiegato " + name + " " + surname +
                " ha superato il limite di uscite registrate in automatico per questo trimestre.\n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli il superamento del suo limite
     * di uscite automatiche.
     */
    static String autoExitLimitReachedNotice(String name, String surname) {
        return composeHeader(name, surname) +
                "Ti comunichiamo che il sistema ha automaticamente avvisato un impiegato del settore " +
                "amministrativo, in quanto hai superato il limite di uscite registrate in automatico " +
                "per questo trimestre.\n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata all'amministrativo per comunicargli il superamento
     * del limite di ritardi da parte del dipendente specificato.
     */
    static String delayLimitReachedAlert(String name, String surname) {
        return "Buongiorno.\n" +
                "Ti scriviamo per notificarti che l'impiegato " + name + " " + surname +
                " ha superato il limite di ritardi per questo trimestre.\n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli il superamento
     * del suo limite di ritardi.
     */
    static String delayLimitReachedNotice(String name, String surname) {
        return composeHeader(name, surname) +
                "Ti comunichiamo che il sistema ha automaticamente avvisato un impiegato del settore " +
                "amministrativo, in quanto hai superato il limite di ritardi in ingresso " +
                "per questo trimestre.\n" +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli la nuova
     * proposta di turnazione relativa al successivo trimestre.
     */
    static String newShiftProposalNotice() {
        return """
                Ciao.
                Ti scriviamo per notificarti che è disponibile la nuova turnazione per il trimestre successivo. \
                Ti preghiamo di prenderne visione e comunicare in anticipo, se possibile, le tue richieste  \
                di astensione.
                Buon lavoro!
                """;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente per comunicargli il calcolo del suo stipendio
     * relativo al mese corrente.
     */
    static String newSalaryNotice(String name, String surname, Double salary) {
        return composeHeader(name, surname) +
                "Ti scriviamo per notificarti che è disponibile il nuovo stipendio " +
                "calcolato per il mese corrente, che ammonta a €" + salary +
                "netti.\n" +
                "Per i dettagli, ti preghiamo di accedere al tuo portale." +
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente {@code recipient} per comunicargli l'assegnazione a suo carico
     * di sostituire il dipendente {@code absentWorker} per il turno specificato.
     */
    static String substitutionAlert(
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
                SALUTE;
    }

    /**
     * Compone l'e-mail che sarà inviata al dipendente specificato per comunicargli che
     * il sistema è riuscito a coprire il turno specificato, ricadente in un periodo di astensione da lui richiesto.
     */
    static String coverageNotice(
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
                SALUTE;
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

    /**
     * Compone l'intestazione della mail con il saluto.
     */
    private static String composeHeader(String name, String surname) {
        return "Ciao, \n" +
                name + " " + surname + ".\n";
    }

    // TODO: overtimeAlert come funziona?
}
