package modes.programmierer.logic;

import modes.programmierer.model.Basis;
import modes.programmierer.model.Wortbreite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ProgrammiererUeberlaufTest
{
    @ParameterizedTest
    @CsvSource({
            "BYTE, 7F, -128",
            "WORD, 7FFF, -32768",
            "DWORD, 7FFFFFFF, -2147483648",
            "QWORD, 7FFFFFFFFFFFFFFF, -9223372036854775808"
    })
    void plus_ShouldWrapToMin_WhenSignedMaxPlusOne(Wortbreite wortbreite, String maxHex, String erwartet)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, false, maxHex);

        // Act
        rechne(logik, logik::plus, "1");

        // Assert
        assertEquals(erwartet, logik.getAnzeige(Basis.DEC));
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, FF",
            "WORD, FFFF",
            "DWORD, FFFFFFFF",
            "QWORD, FFFFFFFFFFFFFFFF"
    })
    void plus_ShouldWrapToZero_WhenUnsignedMaxPlusOne(Wortbreite wortbreite, String maxHex)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, true, maxHex);

        // Act
        rechne(logik, logik::plus, "1");

        // Assert
        assertEquals("0", logik.getAnzeige(Basis.DEC));
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, 80, 127",
            "WORD, 8000, 32767",
            "DWORD, 80000000, 2147483647",
            "QWORD, 8000000000000000, 9223372036854775807"
    })
    void minus_ShouldWrapToMax_WhenSignedMinMinusOne(Wortbreite wortbreite, String minHex, String erwartet)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, false, minHex);

        // Act
        rechne(logik, logik::minus, "1");

        // Assert
        assertEquals(erwartet, logik.getAnzeige(Basis.DEC));
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, 255",
            "WORD, 65535",
            "DWORD, 4294967295",
            "QWORD, 18446744073709551615"
    })
    void minus_ShouldWrapToMax_WhenUnsignedZeroMinusOne(Wortbreite wortbreite, String erwartet)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, true, "0");

        // Act
        rechne(logik, logik::minus, "1");

        // Assert
        assertEquals(erwartet, logik.getAnzeige(Basis.DEC));
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, false, 10, 8, 80",
            "BYTE, true, 64, 3, 2C",
            "WORD, false, 100, 200, 0",
            "DWORD, true, 10000, 10000, 0",
            "QWORD, false, 100000000, 100000000, 0",
            "QWORD, true, FFFFFFFFFFFFFFFF, 2, FFFFFFFFFFFFFFFE"
    })
    void mal_ShouldKeepOnlyLowBits_WhenProductOverflows(Wortbreite wortbreite, boolean unsigned,
                                                       String linksHex, String rechtsHex, String erwartetHex)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, unsigned, linksHex);

        // Act
        rechne(logik, logik::mal, rechtsHex);

        // Assert
        assertEquals(erwartetHex, logik.getAnzeige(Basis.HEX));
    }

    @Test
    void mal_ShouldGiveMinusOneTwentyEight_WhenSignedByteProductIsOneTwentyEight()
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(Wortbreite.BYTE, false, "10");

        // Act
        rechne(logik, logik::mal, "8");

        // Assert
        assertEquals("-128", logik.getAnzeige(Basis.DEC));
    }

    @ParameterizedTest
    @CsvSource({ "BYTE", "WORD", "DWORD", "QWORD" })
    void shiftLeft_ShouldEndAtZero_WhenShiftedPastWordWidth(Wortbreite wortbreite)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, false, "1");

        // Act
        for (int i = 0; i < wortbreite.getBits() - 1; i++)
        {
            logik.shiftLeft();
        }
        String vorzeichenBit = logik.getAnzeige(Basis.HEX);
        logik.shiftLeft();

        // Assert
        assertEquals("8" + "0".repeat(wortbreite.getBits() / 4 - 1), vorzeichenBit);
        assertEquals("0", logik.getAnzeige(Basis.HEX));
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, FF, 7F",
            "WORD, FFFF, 7FFF",
            "DWORD, FFFFFFFF, 7FFFFFFF",
            "QWORD, FFFFFFFFFFFFFFFF, 7FFFFFFFFFFFFFFF"
    })
    void shiftRight_ShouldShiftInZero_WhenUnsigned(Wortbreite wortbreite, String maxHex, String erwartetHex)
    {
        // Arrange
        ProgrammiererLogik logik = mitHexWert(wortbreite, true, maxHex);

        // Act
        logik.shiftRightArithmetic();

        // Assert
        assertEquals(erwartetHex, logik.getAnzeige(Basis.HEX));
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, 128",
            "WORD, 32768",
            "DWORD, 2147483648",
            "QWORD, 9223372036854775808"
    })
    void digitEingeben_ShouldAcceptSignedMin_WhenTypedWithMinus(Wortbreite wortbreite, String betrag)
    {
        // Arrange
        ProgrammiererLogik logik = new ProgrammiererLogik();
        logik.setWortbreite(wortbreite);

        // Act
        logik.vorzeichenWechseln();
        for (char ziffer : betrag.toCharArray())
        {
            logik.digitEingeben(String.valueOf(ziffer));
        }

        // Assert
        assertEquals("-" + betrag, logik.getAnzeige(Basis.DEC));
    }

    @Test
    void digitEingeben_ShouldWrapLikeSmallerWidths_WhenQwordDecimalInputOverflows()
    {
        // Arrange
        ProgrammiererLogik logik = new ProgrammiererLogik();

        // Act
        for (int i = 0; i < 19; i++)
        {
            logik.digitEingeben("9");
        }

        // Assert
        assertEquals("-8446744073709551617", logik.getAnzeige(Basis.DEC));
    }

    private static ProgrammiererLogik mitHexWert(Wortbreite wortbreite, boolean unsigned, String hex)
    {
        ProgrammiererLogik logik = new ProgrammiererLogik();
        logik.setWortbreite(wortbreite);
        logik.setUnsigned(unsigned);
        logik.setBasis(Basis.HEX);
        tippe(logik, hex);
        return logik;
    }

    private static void rechne(ProgrammiererLogik logik, Runnable operation, String rechtsHex)
    {
        operation.run();
        tippe(logik, rechtsHex);
        logik.berechne();
    }

    private static void tippe(ProgrammiererLogik logik, String hex)
    {
        for (char ziffer : hex.toCharArray())
        {
            logik.digitEingeben(String.valueOf(ziffer));
        }
    }
}
