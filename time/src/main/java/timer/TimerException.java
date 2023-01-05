package timer;

/**
 * Questa eccezione viene lanciata ogni volta che un'azione programmata dalla classe {@link TimerManager} lancia
 * un qualunque tipo di eccezione interna, o la classe stessa si trova in uno stato errato.
 */
public class TimerException extends RuntimeException {
    /**
     * Costruisce una {@link TimerException} col messaggio di errore specificato.
     * @param message il messaggio di errore
     */
    public TimerException(String message) {
        super("Errore timer connessione al DBMS: " + message);
    }
}
