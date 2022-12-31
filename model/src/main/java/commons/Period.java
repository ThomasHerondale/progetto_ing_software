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
     * Verifica che la data specificata si trovi nel periodo specificato, estremi inclusi.
     * @param startDate la data di inizio del periodo
     * @param endDate la data di fine del periodo
     * @param other la data oggetto della verifica
     * @return true se {@code other} si trova in mezzo a {@code start} e {@code end}, false altrimenti
     */
    public static boolean comprehends(LocalDate startDate, LocalDate endDate, LocalDate other) {
        return startDate.equals(other) || endDate.equals(other) ||
                (startDate.isBefore(other) && endDate.isAfter(other));
    }

    /**
     * Ritorna il numero di giorni compreso in questo periodo.
     * @return il numero di giorni compresi tra la data di inizio e quella di fine di questo periodo,
     * estremi inclusi
     */
    public int dayCount() {
        return dayCount(start, end);
    }

    /**
     * Verifica che la data specificata si trovi in questo periodo.
     * @param date la data oggetto della verifica
     * @return true se la data si trova in questo periodo, false altrimenti
     */
    public boolean comprehends(LocalDate date) {
        return comprehends(start, end, date);
    }
}
