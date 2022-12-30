package commons;

import java.time.LocalDate;

import static java.time.Period.*;

/**
 * Questo record rappresenta il periodo di tempo (i.e. l'insieme di giorni) compreso tra due date, e gestisce in
 * generale, anche per mezzo di metodi statici, gli intervalli di tempo fra date.
 * @param start la data di inizio di questo periodo
 * @param end la data di fine di questo periodo
 */
public record Period(LocalDate start, LocalDate end) {
    /**
     * Ritorna il numero di giorni compreso nel periodo specificato.
     * @param start la data di inizio del periodo
     * @param end la data di fine del periodo
     * @return il numero di giorni compresi tra la data di inizio e la data di fine, estremi inclusi
     */
    public static int dayCount(LocalDate start, LocalDate end) {
        return between(start, end.plusDays(1)).getDays();
    }

    /**
     * Ritorna il numero di giorni compreso in questo periodo.
     * @return il numero di giorni compresi tra la data di inizio e quella di fine di questo periodo,
     * estremi inclusi
     */
    public int dayCount() {
        return between(start, end.plusDays(1)).getDays();
    }
}
