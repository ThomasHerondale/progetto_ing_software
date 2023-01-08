package shifts;

import commons.Period;
import control.ShiftProposalHandler;
import database.DBMSDaemon;
import database.DBMSException;
import entities.Shift;
import entities.Worker;
import mail.MailManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShiftEditingHandler {
    private final List<Shift> shiftProposal;
    private final AbstentionData requestedAbstention;
    private final List<Character> checkOrder;

    public ShiftEditingHandler(List<Shift> shiftProposal, AbstentionData requestedAbstention) {
        this.shiftProposal = shiftProposal;
        this.requestedAbstention = requestedAbstention;
        this.checkOrder = getCheckOrder(requestedAbstention.worker().getRank());
    }

    private void editShiftProposal() {
        var abstentionShifts = shiftProposal
                .stream()
                .filter(shift -> requestedAbstention.period().comprehends(shift.getDate()))
                .filter(shift -> requestedAbstention.worker().equals(shift.getOwner()))
                .toList();
        System.out.println("TURNI IN PERIODO: " + abstentionShifts);
        for (var shift : abstentionShifts) {
            var solutionFlag = false;
            for (var rank : checkOrder) {
                /* Calcola l'eventuale sostituzione */
                var substituonOpt = computeSubstitution(shift, rank);
                /* Se il calcolo è andato a buon fine */
                if (substituonOpt.isPresent()) {
                    setSubstitution(shift, substituonOpt.get());
                    solutionFlag = true;
                    break;
                }
                /* Altrimenti calcola l'eventuale straordinario */
                if (computeOvertime(shift, rank)) {
                    solutionFlag = true;
                    break;
                }
            }
            if (!solutionFlag)
                /* Non si è trovata soluzione */
                System.err.println("Il turno " + shift + "non è stato coperto.");
        }
    }

    /* Ritorna il turno con cui far cambio */
    private Optional<Shift> computeSubstitution(Shift shift, char rank) {
        /* Trova i turni fuori dal periodo di astensione di altri dipendenti */
        var shifts = shiftProposal
                .stream()
                .filter(sh -> !requestedAbstention.period().comprehends(sh.getDate()))
                .filter(sh -> sh.getHours() == shift.getHours())
                .filter(sh -> sh.getRank() == rank)
                .filter(sh -> !sh.getOwner().equals(shift.getOwner()))
                .filter(sh -> !sh.isSubstitution() && !sh.isOvertime()) // Non devono essere già struppiati!
                .toList();
        for (var sh : shifts) {
            // System.out.println("Cheking " + sh);
            /* Controlla che il proprietario del turno sia disponibile nell'arco del turno dell'assente */
            if (!checkAvailability(sh.getOwner(), shift.getDate(), shift.getStartTime(), shift.getEndTime())) {
                //System.out.println("Sostituente non disponibile");
                continue;
            }
            /* Controlla che l'assente sia disponibile nell'arco del turno di chi lo sostituirà */
            if (!checkAvailability(shift.getOwner(), sh.getDate(), sh.getStartTime(), sh.getEndTime())) {
                //System.out.println("Sostituito non disponibile");
                continue;
            }
            /* Giunti qui, abbiamo trovato il sostituto */
            //System.out.println("Disponibilità");
            return Optional.of(sh);
        }
        return Optional.empty();
    }

    private boolean computeOvertime(Shift shift, char rank) {
        var shifts = shiftProposal
                .stream()
                .filter(sh -> !sh.getOwner().equals(shift.getOwner()))
                .filter(sh -> sh.getRank() == rank)
                .filter(sh -> !sh.isSubstitution() && !sh.isOvertime())
                .toList();

        /* Ottieni i turni adiacenti a questo */
        Shift previousSh = null;
        Shift nextSh = null;
        for (var sh : shifts) {
            if (sh.getEndTime().equals(shift.getStartTime()))
                previousSh = sh;
            if (sh.getStartTime().equals(shift.getEndTime()))
                nextSh = sh;
        }

        if (previousSh != null) {
            /* Controlla se il dipendente precedente potrebbe coprirlo intero */
            if (previousSh.getHours() + shift.getHours() <= 10) {
                setOvertime(shift, previousSh.getOwner());
                return true;
            } else if (nextSh != null && /* Controlla se potrebbe coprirne metà lui e metà quello di dopo */
                    previousSh.getHours() + (shift.getHours() / 2) <= 10 &&
                    nextSh.getHours() + (shift.getHours() / 2) <= 10) {
                setSplitOvertime(shift, previousSh.getOwner(), nextSh.getOwner());
                return true;
            }
        }
        return false;
    }

    private void setSubstitution(Shift absentShift, Shift substituteShift) {
        try {
            System.err.println("Prova " + absentShift + " ------ " + substituteShift);
            DBMSDaemon.getInstance().setSubstitution(absentShift, substituteShift);
            var absent = absentShift.getOwner();
            var substitute = substituteShift.getOwner();
            var newShift1 = absentShift.copyWithSubstitution(substitute);
            var newShift2 = substituteShift.copyWithSubstitution(absent);
            shiftProposal.remove(absentShift);
            shiftProposal.remove(substituteShift);
            shiftProposal.add(newShift1);
            shiftProposal.add(newShift2);
            MailManager.getInstance().notifySubstitution(substituteShift.getOwner(), absentShift.getOwner(),
                    absentShift);
        } catch (DBMSException e) {
            System.err.println("Problem in shift editing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void setOvertime(Shift shift, Worker w) {
        var newShift = new Shift(w,
                shift.getRank(),
                shift.getDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                true,
                false);
        shiftProposal.remove(shift);
        shiftProposal.add(newShift);
        try {
            DBMSDaemon.getInstance().setOvertime(shift, newShift);
        } catch (DBMSException e) {
            System.err.println("Problem in shift editing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void setSplitOvertime(Shift shift, Worker w1, Worker w2) {
        Shift newShift1;
        Shift newShift2;
        if (shift.getHours() % 2 == 0) {
            var middleTime = shift.getStartTime().plusHours(shift.getHours() / 2);
            newShift1 = new Shift(w1,
                    shift.getRank(),
                    shift.getDate(),
                    shift.getStartTime(),
                    middleTime);
            newShift2 = new Shift(w2,
                    shift.getRank(),
                    shift.getDate(),
                    middleTime,
                    shift.getEndTime());
            shiftProposal.remove(shift);
            shiftProposal.add(newShift1);
            shiftProposal.add(newShift2);
        } else {
            int duration1 = shift.getHours() - shift.getHours() / 2;
            newShift1 = new Shift(w1,
                    shift.getRank(),
                    shift.getDate(),
                    shift.getStartTime(),
                    shift.getStartTime().plusHours(duration1));
            newShift2 = new Shift(w2,
                    shift.getRank(),
                    shift.getDate(),
                    newShift1.getEndTime(),
                    shift.getEndTime());
            shiftProposal.remove(shift);
            shiftProposal.add(newShift1);
            shiftProposal.add(newShift2);
        }
        try {
            DBMSDaemon.getInstance().setOvertime(shift, newShift1, newShift2);
        } catch (DBMSException e) {
            System.err.println("Problem in shift editing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws DBMSException {
       var shH = new ShiftProposalHandler(
               LocalDate.of(2023, 1 , 2),
               DBMSDaemon.getInstance().getWorkersList(), DBMSDaemon.getInstance().getRequestedHolidays(
                       LocalDate.of(2023, 1, 2)));
       shH.computeNewShiftsProposal();
       var shiftProposal = DBMSDaemon.getInstance().getShiftsList();
       var w = new Worker("0123456", "", "", 'H', "", "", "");
       var editor = new ShiftEditingHandler(shiftProposal, new AbstentionData(w, new Period(
               LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 2)
       ), false));
        editor.editShiftProposal();
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

            /* Verifica se gli orari si sovrappongono */
            if (start.isBefore(shift.getEndTime()) && end.isAfter(shift.getStartTime()))
                return false;

            /* Verifica se gli orari sono comunque più vicini di tre ore */
            if (start.isBefore(shiftEndOffset) && end.isAfter(shiftStartOffset))
                return false;
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

record AbstentionData(Worker worker, Period period, boolean isLeave) {

}
