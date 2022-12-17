package ssn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ssn.SSNComputer.*;

class SSNComputerTest {

    static String surnArr1 = "LMB";
    static String surnArr2 = "CNT";
    static String surnArr3 = "BRG";
    static String surnArr4 = "LAA";
    static String surnArr5 = "BOX";
    static String surnArr6 = "REX";
    static String surnArr7 = "LRO";

    static String nameArr1 = "GRL";
    static String nameArr2 = "NNN";
    static String nameArr3 = "LSN";
    static String nameArr4 = "NNA";
    static String nameArr5 = "DAA";
    static String nameArr6 = "MXA";
    static String nameArr7 = "GUO";

    static String date1 = "01E27";
    static String date2 = "00E54";
    static String date3 = "75D45";
    static String date4 = "18T04";
    static String date5 = "91C43";
    static String date6 = "15E54";

    static String pCode1 = "B602"; // Canicatti'
    static String pCode2 = "G291"; // Palo
    static String pCode3 = "D784"; // Frasso Telesino


    @ParameterizedTest
    @DisplayName("Test consonanti cognome")
    @MethodSource("provider_getSurnameConsonantsTest")
    void getSurnameConsonantsTest(String right, String testStr) {
        assertEquals(right, getSurnameConsonants(testStr));
    }

    @ParameterizedTest
    @DisplayName("Test consonanti nome")
    @MethodSource("provider_getNameConsonantsTest")
    void getNameConsonantsTest(String right, String testStr) {
        assertEquals(right, getNameConsonants(testStr));
    }

    @ParameterizedTest
    @DisplayName("Test data di nascita con dati esatti")
    @MethodSource("provider_parseBirthDateTest_rightData")
    void parseBirthDateTest_rightData(String right, String testStr, char testSex) {
        assertDoesNotThrow(() -> assertEquals(right, parseBirthDate(testStr, testSex)));
    }

    @ParameterizedTest
    @DisplayName("Test codici catastali")
    @MethodSource("provider_getPlaceCodeTest")
    void getPlaceCodeTest(String right, String testStr) {
        assertDoesNotThrow(() -> assertEquals(right, getPlaceCode(testStr)));
    }

    @ParameterizedTest
    @DisplayName("Test COMPLETO codice fiscale")
    @MethodSource("provider_computeSSNTest")
    void computeSSNTest(String right, String name, String surname, String date, char sex, String place) {
        assertEquals(right, computeSSN(name, surname, date, sex, place));
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

    private static Stream<Arguments> provider_parseBirthDateTest_rightData() {
        return Stream.of(
                Arguments.of(date1, "27-05-2001", 'M'),
                Arguments.of(date2, "14-05-2000", 'F'),
                Arguments.of(date3, "05-04-1975", 'F'),
                Arguments.of(date4, "04-12-2018", 'M'),
                Arguments.of(date5, "03-03-1991", 'F'),
                Arguments.of(date6, "14-05-2015", 'F')
        );
    }

    private static Stream<Arguments> provider_getPlaceCodeTest() {
        return Stream.of(
               Arguments.of("B602", "caNicatti'"),
               Arguments.of("G291", "PALO"),
               Arguments.of("D784", "Frasso TeLESINO")
        );
    }

    private static Stream<Arguments> provider_computeSSNTest() {
        return Stream.of(
                Arguments.of(
                        "LMBGRL01E27B602O", "Gabriele", "Lombardo", "27-05-2001", 'M', "Canicatti'"
                ),
                Arguments.of(
                        "LMBGHN66P13B602B", "Gioachino", "Lombardo", "13-09-1966", 'M', "Canicatti'"
                ),
                Arguments.of(
                        "NSTLRA03P70G273H", "Laura", "Nasta", "30-09-2003", 'F', "Palermo"
                ),
                Arguments.of(
                        "RSSMRA85T10A562S", "Mario", "Rossi", "10-12-1985", 'M', "San Giuliano Terme"
                )
        );
    }
}