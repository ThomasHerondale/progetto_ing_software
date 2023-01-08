package entities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.util.Objects;

/**
 * Questa classe modella l'oggetto entity {@code <<Shift>>}, il singolo turno di un dipendente specifico,
 * in un ufficio specifico, in un giorno e con un orario specifico.
 */
public class Shift {
    /**
     * Il dipendente assegnatario del turno.
     */
    private final Worker owner;
    /**
     * Il livello dell'ufficio a cui fa riferimento il turno.
     */
    private final char rank;
    /**
     * La data del turno.
     */
    private final LocalDate date;
    /**
     * L'ora di inizio del turno.
     */
    private final LocalTime startTime;
    /**
     * L'ora di fine del turno.
     */
    private final LocalTime endTime;
    /**
     * Contrassegna i turni che costituiscono straordinari.
     */
    private boolean isOvertime;
    /**
     * Contrassegna i turni che sono stati riassegnati.
     */
    private boolean isSubstitution;

    public Shift(Worker owner, char rank, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.owner = owner;
        this.rank = rank;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Shift(Worker owner,
                 char rank,
                 LocalDate date,
                 LocalTime startTime,
                 LocalTime endTime,
                 boolean isOvertime,
                 boolean isSubstitution) {
        this(owner, rank, date, startTime, endTime);
        this.isOvertime = isOvertime;
        this.isSubstitution = isSubstitution;
    }

    public Shift copyWithSubstitution(Worker newOwner) {
        return new Shift(
                newOwner,
                this.rank,
                this.date,
                this.startTime,
                this.endTime,
                false,
                true);
    }

    public Worker getOwner() {
        return owner;
    }

    public char getRank() {
        return rank;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getIntStartTime() {
        return startTime.getHour();
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntEndTime() {
        return endTime.getHour();
    }

    public boolean isOvertime() {
        return isOvertime;
    }

    public boolean isSubstitution() {
        return isSubstitution;
    }

    /**
     * Ritorna il giorno della settimana in cui si svolge il turno.
     * @return una costante dell enum {@link DayOfWeek} rappresentante il giorno della settimana
     */
    public DayOfWeek dayOfWeek() {
        return date.getDayOfWeek();
    }

    /**
     * Ritorna la durata in ore di questo turno.
     * @return la durata in ore di questo turno
     */
    public int getHours() {
        return endTime.minusHours(startTime.get(ChronoField.HOUR_OF_DAY)).get(ChronoField.HOUR_OF_DAY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return rank == shift.rank && Objects.equals(owner, shift.owner)
                && Objects.equals(date, shift.date) && Objects.equals(startTime, shift.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, rank, date, startTime);
    }

    /**
     * Imposta i flag che contrassegnano il turno come sostituzione o straordinario.
     * @param overtimeFlag il nuovo valore del flag di straordinario
     * @param substitutionFlag il nuovo valore del flag di sostituzione
     */
    public void setFlags(boolean overtimeFlag, boolean substitutionFlag) {
        this.isOvertime = overtimeFlag;
        this.isSubstitution = substitutionFlag;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "owner=" + owner +
                ", rank=" + rank +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isOvertime=" + isOvertime +
                ", isSubstitution=" + isSubstitution +
                '}';
    }
}
