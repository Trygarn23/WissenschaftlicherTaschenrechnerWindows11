import modes.programmierer.logic.ProgrammiererLogik;
import modes.programmierer.model.Basis;
import modes.programmierer.model.Wortbreite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProgrammiererLogikTest
{
    private ProgrammiererLogik logik;

    @BeforeEach
    void setUp()
    {
        logik = new ProgrammiererLogik();
    }

    @Test
    void digitEingeben_ShouldAcceptHexDigit_WhenBasisIsHex()
    {
        // Arrange
        logik.setBasis(Basis.HEX);

        // Act
        logik.digitEingeben("A");

        // Assert
        assertEquals("A", logik.getAktuelleEingabe());
        assertEquals("10", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void digitEingeben_ShouldIgnoreInvalidDigit_WhenBasisIsBinary()
    {
        // Arrange
        logik.setBasis(Basis.BIN);

        // Act
        logik.digitEingeben("2");

        // Assert
        assertEquals("0", logik.getAktuelleEingabe());
        assertEquals("0", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void berechne_ShouldAddValues_WhenPlusOperationIsPending()
    {
        // Arrange
        logik.digitEingeben("5");
        logik.plus();
        logik.digitEingeben("3");

        // Act
        logik.berechne();

        // Assert
        assertEquals("8", logik.getAnzeige(Basis.DEC));
        assertEquals("", logik.getPendingOperationText());
    }

    @Test
    void berechne_ShouldApplyBitwiseAndOrXor_WhenOperationsAreUsed()
    {
        // Arrange
        ProgrammiererLogik andLogik = new ProgrammiererLogik();
        ProgrammiererLogik orLogik = new ProgrammiererLogik();
        ProgrammiererLogik xorLogik = new ProgrammiererLogik();

        // Act
        andLogik.digitEingeben("1");
        andLogik.digitEingeben("2");
        andLogik.and();
        andLogik.digitEingeben("1");
        andLogik.digitEingeben("0");
        andLogik.berechne();

        orLogik.digitEingeben("1");
        orLogik.digitEingeben("2");
        orLogik.or();
        orLogik.digitEingeben("1");
        orLogik.digitEingeben("0");
        orLogik.berechne();

        xorLogik.digitEingeben("1");
        xorLogik.digitEingeben("2");
        xorLogik.xor();
        xorLogik.digitEingeben("1");
        xorLogik.digitEingeben("0");
        xorLogik.berechne();

        // Assert
        assertEquals("8", andLogik.getAnzeige(Basis.DEC));
        assertEquals("14", orLogik.getAnzeige(Basis.DEC));
        assertEquals("6", xorLogik.getAnzeige(Basis.DEC));
    }

    @Test
    void not_ShouldRespectWordWidth_WhenByteModeIsActive()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.digitEingeben("0");

        // Act
        logik.not();

        // Assert
        assertEquals("FF", logik.getAnzeige(Basis.HEX));
        assertEquals("-1", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void shiftLeft_ShouldShiftValueLeftByOneBit()
    {
        // Arrange
        logik.digitEingeben("2");

        // Act
        logik.shiftLeft();

        // Assert
        assertEquals("4", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void shiftRight_ShouldCurrentlyUseArithmeticShift_WhenValueIsNegativeInByteMode()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.digitEingeben("2");
        logik.digitEingeben("5");
        logik.digitEingeben("5");

        // Act
        logik.shiftRight();

        // Assert
        assertEquals("-1", logik.getAnzeige(Basis.DEC));
        assertEquals("FF", logik.getAnzeige(Basis.HEX));
    }

    @Test
    void setBasis_ShouldConvertCurrentValueToSelectedBase()
    {
        // Arrange
        logik.digitEingeben("1");
        logik.digitEingeben("5");

        // Act
        logik.setBasis(Basis.HEX);

        // Assert
        assertEquals("F", logik.getAktuelleEingabe());
        assertEquals("1111", logik.getAnzeige(Basis.BIN));
    }

    @Test
    void backspace_ShouldRemoveLastInputDigitAndUpdateValue()
    {
        // Arrange
        logik.digitEingeben("1");
        logik.digitEingeben("2");
        logik.digitEingeben("3");

        // Act
        logik.backspace();

        // Assert
        assertEquals("12", logik.getAktuelleEingabe());
        assertEquals("12", logik.getAnzeige(Basis.DEC));
    }
}
