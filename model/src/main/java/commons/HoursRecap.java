package commons;

/**
 * Questo record contiene il riepilogo della situazione lavorativa dell'impiegato (i.e. le sue ore lavorative
 * effettuate) ai fini del calcolo del suo stipendio.
 * @apiNote le ore di ferie sono incluse in quelle ordinarie, in quanto pagate alla stessa maniera
 */
public record HoursRecap(double ordinaryHours, double overtimeHours, double parentalLeaveHours) {
}
