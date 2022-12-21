package entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Shift {
    private Worker owner;
    private char rank;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
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
}
