package entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Shift {
    private final Worker owner;
    private final char rank;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private boolean isOvertime;
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

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isOvertime() {
        return isOvertime;
    }

    public boolean isSubstitution() {
        return isSubstitution;
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
