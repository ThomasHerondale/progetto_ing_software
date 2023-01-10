package commons;

/**
 * Questo tipo enumerativo è utile ai fini di distinguere le proprietà di un oggetto {@link entities.Worker}
 * da modificare in seguito alla pressione del tasto conferma in un EditPopup, nonché per determinare a tempo
 * di esecuzione la descrizione e i suggerimenti dei campi di testo per il popup stesso.
 */
public enum EditableProperty {
    // TODO: link a EditPopup
    /**
     * Contrassegna il popup come <i>popup di modifica telefono</i>.
     */
    PHONE("numero di telefono", "+39"),
    /**
     * Contrassegna il popup come <i>popup di modifica email</i>.
     */
    EMAIL("indirizzo email", "user@email.com"),
    /**
     * Contrassegna il popip come <i>popup di modifica IBAN</i>.
     */
    IBAN("IBAN", "IT01234567890123456789");
    /**
     * La descrizione della proprietà da modificare.
     */
    private final String stringValue;
    /**
     * Il testo da mostrare come suggerimento nei campi di testo del popup.
     */
    private final String promptText;

    EditableProperty(String stringValue, String promptText) {
        this.stringValue = stringValue;
        this.promptText = promptText;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getPromptText() {
        return promptText;
    }
}
