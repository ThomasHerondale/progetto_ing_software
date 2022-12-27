package commons;

import java.time.LocalDate;

import static java.time.Period.*;

public record Period(LocalDate start, LocalDate end) {
    public static int dayCount(LocalDate start, LocalDate end) {
        return between(start, end.plusDays(1)).getDays();
    }

    public int dayCount() {
        return between(start, end.plusDays(1)).getDays();
    }
}
