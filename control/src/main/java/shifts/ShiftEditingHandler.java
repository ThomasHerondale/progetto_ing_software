package shifts;

import commons.Abstention;
import control.ShiftProposalHandler;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import entities.Worker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShiftEditingHandler {
    private List<Shift> shiftProposal;
    private final Abstention requestedAbstention;
    private final List<Character> checkOrder;

    public ShiftEditingHandler(List<Shift> shiftProposal, Abstention requestedAbstention) {
        this.shiftProposal = shiftProposal;
        this.requestedAbstention = requestedAbstention;
        this.checkOrder = getCheckOrder(requestedAbstention.worker().getRank());
    }

    private void editShiftProposal() {
        var abstentionShifts = shiftProposal.stream().filter(
                shift -> requestedAbstention.period().comprehends(shift.getDate())).toList();
        for (var shift : abstentionShifts) {
            for (var rank : checkOrder) {

            }
        }
    }

 /*   private boolean computeSubstitution(Shift shift) {
        *//* Trova i turni fuori dal periodo di astensione *//*
        var shifts = shiftProposal
                .stream()
                .filter(sh -> !requestedAbstention.period().comprehends(sh.getDate()))
                .filter(sh -> sh.getHours() == shift.getHours())
                .toList();
        for (var sh : shifts) {
            *//* Controlla che il proprietario del turno sia disponibile nell'arco del turno di riferimento *//*
            if (checkAvailability(sh.getOwner(), shift.getDate(), shift.getStartTime(), shift.getEndTime()))
                continue;
            *//* Controlla che l'assente sia disponibile nell'arco del turno di chi lo sostituirà *//*
            if (checkAvailability(shift.getOwner(), sh.getDate(), sh.getStartTime(), sh.getEndTime()))
                continue;

        }
    }*/

    public static void main(String[] args) throws DBMSException {
       var shH = new ShiftProposalHandler(
               LocalDate.of(2023, 1 , 2),
               DBMSDaemon.getInstance().getWorkersList(), DBMSDaemon.getInstance().getRequestedHolidays(
                       LocalDate.of(2023, 1, 2)));
       shH.computeNewShiftsProposal();
       var w = new Worker("0123456", "", "", ' ', "", "", "");
    }



    private boolean checkAvailability(Worker worker, LocalDate date, LocalTime start, LocalTime end) {
        var shifts = shiftProposal
                .stream()
                .filter(shift -> shift.getOwner().equals(worker))
                .filter(shift -> shift.getDate().equals(date))
                .toList();

        /* Controlla tutti i turni del dipendente in quel giorno */
        for (var shift : shifts) {
            var shiftStartOffset = shift.getStartTime().minusHours(3);
            var shiftEndOffset = shift.getEndTime().plusHours(3);

            /* Verifica se gli orari si sovrappongono o sono comunque più vicini di tre ore */
            if ((start.isBefore(shiftEndOffset) || start.equals(shiftEndOffset)) &&
                    (end.isAfter(shiftStartOffset) || end.equals(shiftStartOffset))) {
                return false;
            }
        }

        return true;
    }

    private List<Character> getCheckOrder(Character startRank) {
        List<Character> rankList = new ArrayList<>(List.of('H', 'A', 'B', 'C', 'D'));

        List<Character> order = new ArrayList<>(5);
        var it = rankList.listIterator(rankList.indexOf(startRank));
        it.forEachRemaining(order::add);
        Collections.reverse(rankList);
        it = rankList.listIterator(rankList.indexOf(startRank) + 1); /* Per non rimettere lo stesso rango */
        it.forEachRemaining(order::add);

        System.out.println(order);
        return order;
    }
}
