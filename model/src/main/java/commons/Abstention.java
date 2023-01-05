package commons;

import entities.Worker;

public record Abstention(Worker worker, Period period, boolean isLeave) {
}
