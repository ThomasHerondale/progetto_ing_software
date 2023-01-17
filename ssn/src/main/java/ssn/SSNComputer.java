package ssn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Questa classe di metodi statici si occupa semplicemente di calcolare il codice fiscale di una persona.
 */
public class SSNComputer {

    /* Costruttore privato per impedire l'instanziazione di questa classe. */
    private SSNComputer() {
    }

    /** Calcola il codice fiscale secondo i parametri specificati.
     * @param name il nome da usare
     * @param surname il cognome da usare
     * @param birthDate la data di nascita da usare
     * @param sex il sesso da usare
     * @param birthplace il luogo di nascita da usare
     * @return il codice fiscale calcolato
     */
    public static String computeSSN(
            String name,
            String surname,
            String birthDate,
            char sex,
            String birthplace) {
        var ssnStr = getSurnameConsonants(surname) + getNameConsonants(name) +
                parseBirthDate(birthDate, sex) + getPlaceCode(birthplace);
        return ssnStr + computeCtrlChar(ssnStr.toCharArray());
    }

    /**
     * Estrae le lettere necessarie per il calcolo del codice fiscale dal cognome specificato.
     */
    static String getSurnameConsonants(String surname) {
        surname = stripPreprocess(surname);

        var selections = new StringBuilder();

        for (var letter : surname.toCharArray()) {
            if (isConsonant(letter))
                selections.append(letter);
            if (selections.toString().length() == 3)
                break;
        }

        /* Prendi le vocali se le consonanti non bastano */
        for (var letter : surname.toCharArray()) {
            if (!isConsonant(letter))
                selections.append(letter);
            if (selections.toString().length() == 3)
                break;
        }

        /* Se non siamo riusciti a ottenere ancora tre lettere, usiamo le X */
        for (var i = selections.toString().length(); i < 3; i++)
            selections.append('X');

        return selections.toString();
    }

    /**
     * Estrae le lettere necessarie per il calcolo del codice fiscale dal nome specificato.
     */
    static String getNameConsonants(String name) {
        name = stripPreprocess(name);

        var selections = new StringBuilder();

        /* Estrai tutte le consonanti del nome */
        var consonants = new ArrayList<Character>();
        for (var letter : name.toCharArray()) {
            if (isConsonant(letter))
                consonants.add(letter);
        }
        if (consonants.size() >= 4) { /* Se ci sono più di 3 consonanti, prendi la prima, la terza e la quarta */
            selections.append(consonants.get(0));
            selections.append(consonants.get(2));
            selections.append(consonants.get(3));
        } else { /*Altrimenti prendile tutte */
            for (var letter : consonants)
                selections.append(letter);
        }

        /* Prendi le vocali se le consonanti non bastano */
        for (var letter : name.toCharArray()) {
            if (selections.toString().length() == 3)
                break;
            if (!isConsonant(letter))
                selections.append(letter);
        }

        return selections.toString();
    }


    /**
     * Calcola le lettere relative alla data di nascita per il calcolo del codice fiscale.
     */
    static String parseBirthDate(String birthDate, char sex) {
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

        return strBuilder.toString();
    }

    /**
     * Esegue il preprocessing delle stringhe su cui eseguire le elaborazioni, eliminando spazi
     * all'inizio e alla fine della parola, oltre che unendo tutte le parole in un'unica stringa.
     */
    private static String stripPreprocess(String str) {
        str = preprocess(str);
        return str.replace(" ", "");
    }

    /**
     * Esegue il preprocessing delle stringhe su cui eseguire le elaborazioni,
     * eliminando spazi all'inizio e alla fine della parola.
     */
    private static String preprocess(String str) {
        str = str.toUpperCase();
        str = str.trim();
        return str;
    }

    /**
     * Verifica che il carattere specificato sia una consonante-
     */
    private static boolean isConsonant(Character c) {
        return switch (c) {
            case 'A', 'E', 'I', 'O', 'U' -> false;
            default -> true;
        };
    }

    /**
     * Converte il mese specificato nella lettera corrispondente per il calcolo del codice fiscale.
     */
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

    /**
     * Ottiene il codice catastale relativo al comune di nascita specificato.
     */
    static String getPlaceCode(String birthPlace) {
        final var placeStr = preprocess(birthPlace);
        Optional<String> lineOpt;

        try (Stream<String> stream = Files.lines(Path.of("ssn/src/main/resources/data.txt"))) {
            lineOpt = stream.filter(line -> line.split("\\t")[1].equals(placeStr)).findFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert lineOpt.isPresent();
        /* orElse ritornerà sempre un valore, o l'asserzione avrebbe fallito */
        return lineOpt.orElse("").substring(0, 4);
    }

    /**
     * Calcola il carattere di controllo relativo al codice fiscale specificato.
     */
    private static char computeCtrlChar(char[] ssnStr) {
        int sum = 0;
        for (var i = 0; i < ssnStr.length; i++) {
            if ((i + 1) % 2 != 0)
                sum += convertOddChar(ssnStr[i]);
            else
                sum += convertEvenChar(ssnStr[i]);
        }

        return (char) (sum % 26 + 65);
    }

    /**
     * Converte un carattere di posto dispari nel codice fiscale nel suo valore relativo
     * al calcolo del carattere di controllo.
     */
    private static int convertOddChar(char c) {
        assert Character.isUpperCase(c) || Character.isDigit(c);

        return switch (c) {
            case 'A', '0' -> 1;
            case 'B', '1' -> 0;
            case 'C', '2' -> 5;
            case 'D', '3' -> 7;
            case 'E', '4' -> 9;
            case 'F', '5' -> 13;
            case 'G', '6' -> 15;
            case 'H', '7' -> 17;
            case 'I', '8' -> 19;
            case 'J', '9' -> 21;
            case 'K' -> 2;
            case 'L' -> 4;
            case 'M' -> 18;
            case 'N' -> 20;
            case 'O' -> 11;
            case 'P' -> 3;
            case 'Q' -> 6;
            case 'R' -> 8;
            case 'S' -> 12;
            case 'T' -> 14;
            case 'U' -> 16;
            case 'V' -> 10;
            case 'W' -> 22;
            case 'X' -> 25;
            case 'Y' -> 24;
            case 'Z' -> 23;
            default -> '!'; /* Non dovrebbe mai ritornare questo  */
        };
    }

    /**
     * Converte un carattere di posto pari nel codice fiscale nel suo valore
     * relativo al calcolo del carattere di controllo.
     */
    private static int convertEvenChar(char c) {
        if (Character.isDigit(c))
            return c - 48;
        else
            return c - 65;
    }

    public static void main(String[] args) {
        System.out.println(
                SSNComputer.computeSSN("Alessandro", "Borgese", "04-09-2001",
                        'M', "Palermo"));
    }
}


