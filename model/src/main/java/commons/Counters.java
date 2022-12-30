package commons;

/**
 * Questo record raccoglie insieme i conteggi da mostrare al dipendente su sua richiesta.
 * @param autoExit il conteggio di uscire registrate in automatico dal sistema
 * @param delay il conteggio di ritardi in ingresso
 * @param holiday il conteggio dei giorni di ferie disponibili
 * @param parentalLeave il conteggio delle ore di congedo parentale disponibili
 */
public record Counters(int autoExit, int delay, int holiday, int parentalLeave) {
}
