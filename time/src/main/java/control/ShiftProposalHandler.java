package control;

import commons.Period;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import entities.Worker;
import timer.TimerException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ShiftProposalHandler {
    private final List<LocalDate> firstDaysOfWeeks;
    private final List<Worker> workers;
    private final Map<String, List<Period>> holidays;

    public final List<Shift> shiftProposal;

    private static final List<Character> rankList = List.of('A', 'B', 'C', 'D', 'H');

    public ShiftProposalHandler(LocalDate firstDayOfQuarter,
                                List<Worker> workers,
                                Map<String, List<Period>> holidays) {
        var lastDayOfQuarter = firstDayOfQuarter.plus(3, ChronoUnit.MONTHS).minusDays(1);
        /* Il trimestre finisce la domenica */
        while (lastDayOfQuarter.getDayOfWeek() != DayOfWeek.SUNDAY)
            lastDayOfQuarter = lastDayOfQuarter.plusDays(1);

        /* Conta il numero di settimane nel trimestre */
        var weekCount = 1;
        var dayIterator = firstDayOfQuarter;
        while (!dayIterator.equals(lastDayOfQuarter)) {
            if (dayIterator.getDayOfWeek() == DayOfWeek.SUNDAY)
                weekCount++;
            dayIterator = dayIterator.plusDays(1);
        }

        /* Ottieni i lunedì di ogni settimana del trimestre */
        firstDaysOfWeeks = new ArrayList<>(weekCount);
        for (var i = 0; i < weekCount; i++)
            firstDaysOfWeeks.add(firstDayOfQuarter.plusDays(i * 7L));

        this.workers = workers;
        this.holidays = holidays;
        this.shiftProposal = new ArrayList<>();
    }
    private void computeNewWeeklyShiftsProposal(LocalDate firstDayOfWeek,
                                                GeneralWeekAvailabilities weekAvailabilities) {

        Collections.shuffle(workers);
        var rng = new Random(42);

        for (var rank : rankList) {
            List<Worker> filteredWorkers = new ArrayList<>(
                    workers.stream().filter(worker -> worker.getRank() == rank).toList()
            );

            for (var worker : filteredWorkers) {
                //System.out.println("***" + worker.getId() + " - " + worker.getRank() + "***");

                int startTime = 8;
                int minShiftDuration = 4;
                int maxShiftDuration = 6;

                List<Period> workerHolidays = holidays.getOrDefault(worker.getId(), Collections.emptyList());

                var availability = new WorkerAvailability(
                        worker,
                        workerHolidays,
                        firstDayOfWeek
                );

                var currentDay = firstDayOfWeek;
                var lastDayOfWeek = firstDayOfWeek.plusDays(6);
                while (availability.totalHours < 18) {
                    //System.out.println("Checking " + currentDay);
                    int shiftDuration = rng.nextInt(maxShiftDuration - minShiftDuration + 1) + minShiftDuration;
                    int endTime = Math.min(startTime + shiftDuration, 22);

                    if (currentDay.equals(lastDayOfWeek)) {
                        //System.out.println("\t End of week -> retrying smaller shifts");
                        currentDay = firstDayOfWeek;
                        minShiftDuration = minShiftDuration / 2;
                        continue;
                    }

                    if (startTime >= 22 - minShiftDuration) {
                        //System.out.println("\t Too little time remaining -> retrying next day");
                        currentDay = currentDay.plusDays(1);
                        startTime = 8;
                        continue;
                    }

                    if (availability.isUnavailable(currentDay, startTime, endTime, minShiftDuration)) {
                        startTime++;
                        continue;
                    }

                    if (!weekAvailabilities.isAvailable(rank, currentDay, startTime, endTime)) {
                        startTime++;
                        continue;
                    }

                    if (availability.totalHours + shiftDuration > 18) {
                        //System.out.println("\tToo late to find a shift -> retrying next day");
                        currentDay = currentDay.plusDays(1);
                        startTime = 8;
                        continue;
                    }

                    if (availability.totalHours + shiftDuration < minShiftDuration) {
                        //System.out.println("\tToo little space remaining -> retrying next day");
                        currentDay = currentDay.plusDays(1);
                        startTime = 8;
                        continue;
                    }

                    if (startTime == endTime) {
                        //System.out.println("\t Start time can't be equal to end time -> retrying next" +
                                //" day");
                        currentDay = currentDay.plusDays(1);
                        startTime = 8;
                        continue;
                    }

                    var shift = new Shift(
                            worker,
                            worker.getRank(),
                            currentDay,
                            LocalTime.of(startTime, 0),
                            LocalTime.of(endTime, 0)
                    );

                    weekAvailabilities.setAvailability(rank, currentDay, startTime, endTime);
                    availability.setAvailability(currentDay, startTime, endTime);
                    availability.totalHours += (endTime - startTime);

                    shiftProposal.add(shift);

                    startTime = endTime;

                    /*for (var s : shiftProposal) {
                        System.out.println(s.getOwner().getId() + " : " + s.getDate() + "  " + s.getStartTime() + "   " +
                                s.getEndTime() + "  r: " + s.getRank());
                    }*/
                }
            }
        }
    }

    public void computeNewShiftsProposal() {
        for (var monday : firstDaysOfWeeks)
            computeNewWeeklyShiftsProposal(monday, new GeneralWeekAvailabilities(monday));

        try {
            DBMSDaemon.getInstance().uploadShiftProposal(shiftProposal);
        } catch (DBMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var w = new Worker("000", "", "", 'A', "", "", "");
        var x = new Worker("111", "", "", 'A', "", "", "");
        var y = new Worker("222", "", "", 'A', "", "", "");
        var z = new Worker("333", "", "", 'A', "", "", "");
        var t = new Worker("444", "", "", 'B', "", "", "");
        var u = new Worker("555", "", "", 'B', "", "", "");
        var a = new Worker("666", "", "", 'B', "", "", "");
        var b = new Worker("777", "", "", 'B', "", "", "");
        var sh = new ShiftProposalHandler(
                LocalDate.of(2023, 1, 2),
                new ArrayList<>(List.of(w, x, y, z, t, u, a, b)),
                Map.of(
                        w.getId(), List.of(),
                        x.getId(), List.of(),
                        y.getId(), List.of(new Period(
                                LocalDate.of(2023, 1, 2),
                                LocalDate.of(2023, 1, 4)
                        )),
                        z.getId(), List.of(),
                        t.getId(), List.of(),
                        u.getId(), List.of(),
                        a.getId(), List.of(),
                        b.getId(), List.of()
                )
        );
        sh.computeNewShiftsProposal();
    }

    private static class GeneralWeekAvailabilities {
        private final Map<LocalDate, Integer[]> a;
        private final Map<LocalDate, Integer[]> b;
        private final Map<LocalDate, Integer[]> c;
        private final Map<LocalDate, Integer[]> d;
        private final Map<LocalDate, Integer[]> h;
        private final LocalDate firstDayOfWeek;

        public GeneralWeekAvailabilities(LocalDate firstDayOfWeek) {
            this.firstDayOfWeek = firstDayOfWeek;
            a = initializeAvailabilities();
            b = initializeAvailabilities();
            c = initializeAvailabilities();
            d = initializeAvailabilities();
            h = initializeAvailabilities();
        }

        public boolean isAvailable(char rank, LocalDate date, int startTime, int endTime) {
            var availability = selectAvailability(rank);
            Integer[] dayPlan = availability.get(date);

            for (var i = startTime; i < endTime; i++) {
                /* Normalizza l'indice */
                int idx = i - 8;
                if (dayPlan[idx] >= 3)
                    return false;
            }

            return true;
        }

        public void setAvailability(char rank, LocalDate date, int startTime, int endTime) {
            Integer[] dayPlan = switch (rank) {
                case 'A' -> a.get(date);
                case 'B' -> b.get(date);
                case 'C' -> c.get(date);
                case 'D' -> d.get(date);
                case 'H' -> h.get(date);
                default -> throw new IllegalArgumentException();
            };
            for (var i = startTime; i < endTime; i++) {
                var idx = i - 8;
                dayPlan[idx]++;
            }
        }

        private Map<LocalDate, Integer[]> selectAvailability(char rank) {
            return switch (rank) {
                case 'A' -> a;
                case 'B' -> b;
                case 'C' -> c;
                case 'D' -> d;
                case 'H' -> h;
                default -> throw new IllegalArgumentException();
            };
        }

        private Map<LocalDate, Integer[]> initializeAvailabilities() {
            Map<LocalDate, Integer[]> availability = new HashMap<>();
            for (var i = 0; i < 6; i++) {
                Integer[] booleans = new Integer[14];
                Arrays.fill(booleans, 0);
                availability.put(firstDayOfWeek.plusDays(i), booleans);
            }
            return availability;
        }
    }

    private static class WorkerAvailability {
        private final Worker worker;
        private final List<Period> holidays;
        private final LocalDate firstDayOfWeek;
        private final Map<LocalDate, Boolean[]> availability;
        int totalHours;

        public WorkerAvailability(Worker worker, List<Period> holidays, LocalDate firstDayOfWeek) {
            this.worker = worker;
            this.holidays = holidays;
            this.firstDayOfWeek = firstDayOfWeek;
            this.availability = initializeAvailabilities();
            this.totalHours = 0;
        }

        public boolean isUnavailable(LocalDate date, int startTime, int endTime, int minimumGap) {
            //System.out.println("[" + worker.getId() + "] Checking for availability" +
                   // " of " + date + " (" + startTime + "-" + endTime + ")");
            for (var holidayPeriod : holidays) {
                if (holidayPeriod.comprehends(date)) {
                    //System.out.println("\t Holiday");
                    return true;
                }
            }

            Boolean[] dayPlan = availability.get(date);

            for (var i = startTime; i < endTime; i++) {
                /* Normalizza l'indice */
                int idx = i - 8;
                if (Boolean.FALSE.equals(dayPlan[idx])) {
                    //System.out.println("\t Already busy");
                    return true;
                }
            }

            for (var i = Math.max(8, startTime - minimumGap); i < startTime; i++) {
                int idx = i - 8;
                if (Boolean.FALSE.equals(dayPlan[idx])) {
                    //System.out.println("\t Too near to another shift");
                    return true;
                }
            }

            return false;
        }

        public void setAvailability(LocalDate date, int startTime, int endTime) {
            Boolean[] dayPlan = availability.get(date);
            for (var i = startTime; i < endTime; i++) {
                var idx = i - 8;
                dayPlan[idx] = false;
            }
        }

        private Map<LocalDate, Boolean[]> initializeAvailabilities() {
            Map<LocalDate, Boolean[]> newAvailability = new HashMap<>();
            for (var i = 0; i < 6; i++) {
                Boolean[] booleans = new Boolean[14];
                Arrays.fill(booleans, true);
                newAvailability.put(firstDayOfWeek.plusDays(i), booleans);
            }
            return newAvailability;
        }
    }
}