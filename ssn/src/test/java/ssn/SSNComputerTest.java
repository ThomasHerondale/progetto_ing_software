package ssn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ssn.SSNComputer.*;

class SSNComputerTest {

    static Character[] surnArr1 = new Character[] {'L', 'M', 'B'};
    static Character[] surnArr2 = new Character[] {'C', 'N', 'T'};
    static Character[] surnArr3 = new Character[] {'B', 'R', 'G'};
    static Character[] surnArr4 = new Character[] {'L', 'A', 'A'};
    static Character[] surnArr5 = new Character[] {'B', 'O', 'X'};
    static Character[] surnArr6 = new Character[] {'R', 'E', 'X'};
    static Character[] surnArr7 = new Character[] {'L', 'R', 'O'};

    static Character[] nameArr1 = new Character[] {'G', 'R', 'L'};
    static Character[] nameArr2 = new Character[] {'N', 'N', 'N'};
    static Character[] nameArr3 = new Character[] {'L', 'S', 'N'};
    static Character[] nameArr4 = new Character[] {'N', 'N', 'A'};
    static Character[] nameArr5 = new Character[] {'D', 'A', 'A'};
    static Character[] nameArr6 = new Character[] {'M', 'X', 'A'};
    static Character[] nameArr7 = new Character[] {'G', 'U', 'O'};

    static String date1 = "01E27";
    static String date2 = "00E54";
    static String date3 = "75D45";
    static String date4 = "18T04";
    static String date5 = "91C43";
    static String date6 = "15E54";


    @ParameterizedTest
    @DisplayName("Test consonanti cognome")
    @MethodSource("provider_getSurnameConsonantsTest")
    void getSurnameConsonantsTest(Character[] right, String testStr) {
        assertArrayEquals(right, getSurnameConsonants(testStr));
    }

    @ParameterizedTest
    @DisplayName("Test consonanti nome")
    @MethodSource("provider_getNameConsonantsTest")
    void getNameConsonantsTest(Character[] right, String testStr) {
        assertArrayEquals(right, getNameConsonants(testStr));
    }

    @ParameterizedTest
    @DisplayName("Test data di nascita con dati esatti")
    @MethodSource("provider_parseBirthDateTest_rightData")
    void parseBirthDateTest_rightData(String right, String testStr, char testSex) {
        assertDoesNotThrow(() -> assertArrayEquals(right.toCharArray(), parseBirthDate(testStr, testSex)));
    }

    private static Stream<Arguments> provider_getSurnameConsonantsTest() {
        return Stream.of(
                Arguments.of(surnArr1, "Lombardo"),
                Arguments.of(surnArr2, "Centonze"),
                Arguments.of(surnArr3, "Borgese"),
                Arguments.of(surnArr4, "Ala"),
                Arguments.of(surnArr5, "Bo"),
                Arguments.of(surnArr6, "Re"),
                Arguments.of(surnArr7, "Lo Re")
        );
    }

    private static Stream<Arguments> provider_getNameConsonantsTest() {
        return Stream.of(
                Arguments.of(nameArr1, "Gabriele"),
                Arguments.of(nameArr2, "Antonino"),
                Arguments.of(nameArr3, "Alessandro"),
                Arguments.of(nameArr4, "Anna"),
                Arguments.of(nameArr5, "Ada"),
                Arguments.of(nameArr6, "Max"),
                Arguments.of(nameArr7, "Ugo")
        );
    }

    public static Stream<Arguments> provider_parseBirthDateTest_rightData() {
        return Stream.of(
                Arguments.of(date1, "27-05-2001", 'M'),
                Arguments.of(date2, "14-05-2000", 'F'),
                Arguments.of(date3, "05-04-1975", 'F'),
                Arguments.of(date4, "04-12-2018", 'M'),
                Arguments.of(date5, "03-03-1991", 'F'),
                Arguments.of(date6, "14-05-2015", 'F')
        );
    }
}