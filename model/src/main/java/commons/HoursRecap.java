package commons;

/**
 * Questo record contiene il riepilogo della situazione lavorativa dell'impiegato (i.e. le sue ore lavorative
 * effettuate) ai fini del calcolo del suo stipendio.
 * @apiNote le ore di ferie sono incluse in quelle ordinarie, in quanto pagate alla stessa maniera
 * @param ordinaryHours le ore di lavoro ordinarie svolte
 * @param overtimeHours le ore di lavoro straordinarie svolte
 * @param parentalLeaveHours le ore di congedo parentale richieste
 */
public record HoursRecap(double ordinaryHours, double overtimeHours, double parentalLeaveHours) {
}
