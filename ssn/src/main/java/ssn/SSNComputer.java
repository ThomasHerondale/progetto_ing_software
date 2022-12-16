package ssn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSNComputer {

    static Character[] getSurnameConsonants(String surname) {
        surname = preprocess(surname);

        var selections = new ArrayList<Character>(3);

        for (var letter : surname.toCharArray()) {
            if (isConsonant(letter))
                selections.add(letter);
            if (selections.size() == 3)
                break;
        }

        /* Prendi le vocali se le consonanti non bastano */
        for (var letter : surname.toCharArray()) {
            if (!isConsonant(letter))
                selections.add(letter);
            if (selections.size() == 3)
                break;
        }

        /* Se non siamo riusciti a ottenere ancora tre lettere, usiamo le X */
        for (var i = selections.size(); i < 3; i++)
            selections.add('X');

        return selections.toArray(new Character[0]);
    }

    public static Character[] getNameConsonants(String name) {
        name = preprocess(name);

        var selections = new ArrayList<Character>(3);

        /* Estrai tutte le consonanti del nome */
        var consonants = new ArrayList<Character>();
        for (var letter : name.toCharArray()) {
            if (isConsonant(letter))
                consonants.add(letter);
        }
        if (consonants.size() >= 4) { /* Se ci sono più di 3 consonanti, prendi la prima, la terza e la quarta */
            selections.add(consonants.get(0));
            selections.add(consonants.get(2));
            selections.add(consonants.get(3));
        } else if (consonants.size() == 3) { /* Se ci sono tre consonanti, prendile tutte */
            selections.addAll(consonants);
        } else {
            selections.addAll(consonants);
            /* Prendi le vocali se le consonanti non bastano */
            for (var letter : name.toCharArray()) {
                if (!isConsonant(letter))
                    selections.add(letter);
                if (selections.size() == 3)
                    break;
            }
        }

        return selections.toArray(new Character[0]);
    }

    static char[] parseBirthDate(String birthDate, char sex) {
        assert sex == 'M' || sex == 'F';

        /* Data in formato GG-MM-AAAA */
        String[] comps = birthDate.split("-");
        var strBuilder = new StringBuilder();

        /* Inserisci le ultime due cifre dell'anno */
        strBuilder.append(comps[2].substring(2));

        /* Inserisci la lettera del mese */
        strBuilder.append(parseMonth(comps[1]));

        if (sex == 'M') /* Se uomo il giorno rimane invariato */
            strBuilder.append(comps[0]);
        else /* Se donna aggiungi 40 */
            strBuilder.append(Integer.parseInt(comps[0]) + 40);

        return strBuilder.toString().toCharArray();
    }

    private static String preprocess(String str) {
        str = str.toUpperCase();
        str = str.trim();
        str = str.replace(" ", "");
        return str;
    }

    private static boolean isConsonant(Character c) {
        return switch (c) {
            case 'A', 'E', 'I', 'O', 'U' -> false;
            default -> true;
        };
    }

    private static char parseMonth(String monthStr) {
        assert List.of(
                "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
                .contains(monthStr);

        return switch (monthStr) {
            case "01" -> 'A';
            case "02" -> 'B';
            case "03" -> 'C';
            case "04" -> 'D';
            case "05" -> 'E';
            case "06" -> 'H';
            case "07" -> 'L';
            case "08" -> 'M';
            case "09" -> 'P';
            case "10" -> 'R';
            case "11" -> 'S';
            case "12" -> 'T';
            default -> '!'; /* Non dovrebbe mai ritornare questo  */
        };
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(getNameConsonants("Antonino")));
    }
}


