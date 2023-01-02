package com.example.control;

import commons.Period;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import entities.Worker;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;

public class ShiftProposalHandler {
    List<Worker> workers;
    Map<Worker, List<Period>> holidays;

    /* 6 perchè la domenica non si lavora */
    Shift[][] shifts = new Shift[6][18];

    LocalDate firstDayOfQuarter;

    public void computeNewShiftsProposal() {
        for (var worker : workers) {
            int lastShiftLevel = -1;
            int startTime = 8;
            var availability = new WorkerAvailability(worker);

            while (availability.totalHours < 18) {
                int endTime = startTime + 1;

                if (startTime == lastShiftLevel + 1) {
                    startTime++;
                    continue;
                }

                if ()
            }
        }
    }


    private class WorkerAvailability {
        Worker worker;
        boolean[][] availability;
        int totalHours;
        List<Period> holidays;

        public WorkerAvailability(Worker worker, List<Period> holidays) {
            this.worker = worker;
            this.availability = new boolean[7][18];
            this.totalHours = 0;
            this.holidays = holidays;

            for (boolean[] booleans : availability) {
                Arrays.fill(booleans, true);
            }
        }

        public boolean isAvailable(int startTime, int endTime) {
            for (Period period : holidays) {
                if (period.getStartDate().getHours() <= startTime && period.getEndDate().getHours() >= endTime) {
                    return false; // If the worker has requested a leave, return false
                }
            }
        }
    }
}