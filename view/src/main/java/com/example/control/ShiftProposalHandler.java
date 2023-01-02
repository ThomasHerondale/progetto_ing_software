package com.example.control;

import commons.Period;
import entities.Shift;
import entities.Worker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ShiftProposalHandler {
    List<Worker> workers;
    Map<Worker, List<Period>> holidays = Collections.emptyMap();


    LocalDate firstDayOfQuarter = LocalDate.of(2023, 1, 9);

    public List<Shift> shiftProposal = new ArrayList<>();

    Map<LocalDate, Boolean[]> generalAvailability = new HashMap<>();

    public ShiftProposalHandler(List<Worker> workers, Map<Worker, List<Period>> holidays) {
        this.holidays = holidays;
        this.workers = workers;
        for (var i = 0; i < 6; i++) {
            Boolean[] booleans = new Boolean[14];
            Arrays.fill(booleans, true);
            generalAvailability.put(firstDayOfQuarter.plusDays(i), booleans);
        }
    }

    public void computeNewShiftsProposal() {
        var rng = new Random(42);

        for (var worker : workers) {
            System.out.println("----------------------" + worker.getId());
            int lastShiftEnd = -1;
            int startTime = 8;
            int minShiftDuration = 4;
            int maxShiftDuration = 6;
            var availability = new WorkerAvailability(worker, holidays.get(worker),
                    LocalDate.of(2023, 1, 9));


            var currentDay = firstDayOfQuarter;
            while (availability.totalHours < 18) {
                int shiftDuration = rng.nextInt(maxShiftDuration - minShiftDuration + 1) + minShiftDuration;
                System.out.println(currentDay);
                System.out.println("----- TOTHOURS " + availability.totalHours);
                System.out.println("----- STARTIME " + startTime);
                int endTime = Math.min(startTime + shiftDuration, 22);
                System.out.println("_____ENDTIME" + endTime);

                if (currentDay.equals(firstDayOfQuarter.plusDays(5))) {
                    System.out.println("End of week, retrying with smaller shifts");
                    currentDay = firstDayOfQuarter;
                    minShiftDuration = minShiftDuration / 2;
                    continue;
                }

                if (startTime < lastShiftEnd + minShiftDuration) {
                    System.out.println(startTime + "<" + (lastShiftEnd + minShiftDuration) + " Too near -> going later");
                    startTime++;
                    continue;
                }

                if (startTime >= 22 - minShiftDuration) { // Forse senza l'uguale...
                    System.out.println("Too short -> next day");
                    currentDay = currentDay.plusDays(1);
                    lastShiftEnd = -1;
                    startTime = 8;
                    continue;
                }


                if (!availability.isAvailable(currentDay, startTime, endTime)) {
                    System.out.println("Unavailable");
                    startTime++;
                    continue;
                }

                if (!isGenerallyAvailable(currentDay, startTime, endTime)) {
                    System.out.println("Already taken");
                    startTime++;
                    continue;
                }

                if (availability.totalHours + shiftDuration > 18) {
                    System.out.println("Too late -> next day");
                    currentDay = currentDay.plusDays(1);
                    lastShiftEnd = -1;
                    startTime = 8;
                    continue;
                }

                if (availability.totalHours + shiftDuration < minShiftDuration) {
                    System.out.println("Too little space remaining -> next day");
                    currentDay = currentDay.plusDays(1);
                    lastShiftEnd = -1;
                    startTime = 8;
                    continue;
                }

                if (startTime == endTime) {
                    System.out.println("Start time can't be equal to end time");
                    currentDay = currentDay.plusDays(1);
                    lastShiftEnd = -1;
                    startTime = 8;
                    continue;
                }

                var shift = new Shift(worker,
                        worker.getRank(),
                        currentDay,
                        LocalTime.of(startTime, 0),
                        LocalTime.of(endTime, 0));
                shiftProposal.add(shift);
                setGeneralAvailability(currentDay, startTime, endTime);
                availability.setAvailability(currentDay, startTime, endTime);
                availability.totalHours += (endTime - startTime);
                lastShiftEnd = endTime;
                startTime = lastShiftEnd;
                System.out.println("Tot. hours:" + availability.totalHours);
                for (var s : shiftProposal) {
                    System.out.println(s.getOwner().getId() + " : " + s.getDate() + "  " + s.getStartTime() + "   " +
                            s.getEndTime() + "  r: " + s.getRank());
                }
            }
        }
    }

        public void setGeneralAvailability(LocalDate date, int startTime, int endTime) {
            Boolean[] dayPlan = generalAvailability.get(date);
            for (var i = startTime; i < endTime; i++) {
                var idx = i - 8;
                dayPlan[idx] = false;
            }
            //generalAvailability.forEach((e, v) -> System.out.println(Arrays.toString(v)));
        }

    public boolean isGenerallyAvailable(LocalDate date, int startTime, int endTime) {

        // Check if the worker has already been assigned a shift at the specified time
        Boolean[] dayPlan = generalAvailability.get(date);
        for (var i = startTime; i < endTime; i++) {
            /* Normalizza l'indice */
            int idx = i - 8;
            if (Boolean.FALSE.equals(dayPlan[idx]))
                return false;
        }

        // If the worker has not requested a leave and is not already assigned a shift, return true
        return true;
    }

        public static void main(String[] args) {
/*        var weekStart = LocalDate.of(2023, 1, 2);
        var date = LocalDate.of(2023, 1, 5);
        var d = new WorkerAvailability(w, List.of(
                new Period(LocalDate.of(2023, 1, 9),
                        LocalDate.of(2023, 1, 13))),
                LocalDate.of(2023, 1, 9));
        System.out.println(d.isAvailable(LocalDate.of(2023, 1, 13), 8, 12));*/
            var w = new Worker("098", "", "", 'A', "", "", "");
            var x = new Worker("678", "", "", 'A', "", "", "");
            var sh = new ShiftProposalHandler(List.of(w, x), Map.of(x, List.<Period>of(
                    new Period(LocalDate.of(2023, 1, 9),
                            LocalDate.of(2023, 1, 9))), w, List.<Period>of())
            );
        sh.computeNewShiftsProposal();
        }

    private static class WorkerAvailability {
        Worker worker;
        Map<LocalDate, Boolean[]> availability;
        int totalHours;
        List<Period> holidays;

        LocalDate weekStart;

        // startDate è la data di inizio della settimana, inclusa i.e. la data del lunedì
        public WorkerAvailability(Worker worker, List<Period> holidays, LocalDate startDate) {
            this.worker = worker;
            this.availability = new HashMap<>();
            this.totalHours = 0;
            this.holidays = holidays;
            this.weekStart = startDate;

            for (var i = 0; i < 6; i++) {
                Boolean[] booleans = new Boolean[14];
                Arrays.fill(booleans, true);
                availability.put(startDate.plusDays(i), booleans);
            }
        }



            public boolean isAvailable(LocalDate date, int startTime, int endTime) {
            if (startTime > 24)
                System.exit(1);
                System.out.println("Checking availability of " + date + "--" + startTime + " " + endTime);
                for (var holidayPeriod : holidays) {
                    if (holidayPeriod.comprehends(date))
                        return false;
                }

                // Check if the worker has already been assigned a shift at the specified time
                Boolean[] dayPlan = availability.get(date);
                //System.out.println("Getting ----" + date);
                for (var i = startTime; i < endTime; i++) {
                    /* Normalizza l'indice */
                    int idx = i - 8;
                    if (Boolean.FALSE.equals(dayPlan[idx]))
                        return false;
                }

                // If the worker has not requested a leave and is not already assigned a shift, return true
                return true;
            }

            public void setAvailability(LocalDate date, int startTime, int endTime) {
                Boolean[] dayPlan = availability.get(date);
                for (var i = startTime; i < endTime; i++) {
                    var idx = i - 8;
                    dayPlan[idx] = false;
                }
            }
        }
    }