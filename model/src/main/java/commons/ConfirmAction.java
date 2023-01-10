package commons;

/**
 * Questo tipo enumerativo è utile ai fini di distinguere le azioni da intraprendere in seguito alla pressione del
 * tasto conferma di un ConfirmPopup, nonché per determinare a tempo di esecuzione il contenuto stesso di questi
 * popup da mostrare a schermo.
 */
public enum ConfirmAction {
    // TODO: @link ConfirmPopup quando sarà possibile
    /**
     * Contrassegna il popup come <i>popup di conferma licenziamento</i>.
     */
    REMOVE("Conferma Rimozione", "Vuoi rimuovere l'impiegato?"),
    /**
     * Contrassegna il popup come <i>popup di conferma promozione</i>.
     */
    PROMOTE("Conferma Promozione", "Vuoi promuovere l'impiegato?"),
    /**
     * Contrassegna il popup come <i>popup di conferma inserimento presenza</i>.
     * @apiNote la variabile {@code descriptionString} associata a questa costante presenta due campi
     * <i>$worker</i> e <i>$date</i> che andranno sostituiti con il {@code fullName} del dipendente e la
     * data della presenza da inserire; una volta ottenuta la stringa, queste diciture andranno sostituite con
     * il metodo {@link String#replace(CharSequence, CharSequence)}, e.g.
     * {@code ConfirmAction.PRESENCE.getDescriptionString().replace("$worker", "Gabriele Lombardo")}
     */
    PRESENCE("Registrazione Presenza",
            "Il dipendente $worker risulterà presente il $date."),
    /**
     * Contrassegna il popup come <i>popup di conferma logout</i>.
     */
    LOGOUT("Conferma Logout", "Vuoi effettuare il logout?"),
    /**
     * Contrassegna il popup come <i>popup di autorizzazione congedo parentale</i>.
     * @apiNote la variabile {@code descriptionString} associata a questa costante presenta un
     * campo <i>$hours</i> da sostituire con il numero di ore concesse al dipendente. Vedi anche
     * {@link ConfirmAction#PRESENCE} per un esempio più dettagliato
     */
    ENABLE_PARENTAL_LEAVE("Autorizza Congedo Parentale",
            "Saranno concesse all'impiegato $hours ore di congedo parentale. Confermare?");

    /**
     * Il titolo del popup contrassegnato con questa azione.
     */
    private final String titleString;
    /**
     * La descrizione del popup contrassegnato con questa azione.
     */
    private final String descriptionString;

    ConfirmAction(String titleString, String descriptionString) {
        this.titleString = titleString;
        this.descriptionString = descriptionString;
    }

    public String getTitleString() {
        return titleString;
    }

    public String getDescriptionString() {
        return descriptionString;
    }
}
