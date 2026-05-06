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

    @Test
    void byteSigned_ShouldInterpretHexFFAsMinusOne()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.setBasis(Basis.HEX);

        // Act
        logik.digitEingeben("F");
        logik.digitEingeben("F");

        // Assert
        assertEquals("-1", logik.getAnzeige(Basis.DEC));
        assertEquals("FF", logik.getAnzeige(Basis.HEX));
    }

    @Test
    void byteUnsigned_ShouldInterpretHexFFAs255()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.setUnsigned(true);
        logik.setBasis(Basis.HEX);

        // Act
        logik.digitEingeben("F");
        logik.digitEingeben("F");

        // Assert
        assertEquals("255", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void wordAndDwordSigned_ShouldInterpretAllBitsSetAsMinusOne()
    {
        // Arrange
        ProgrammiererLogik word = new ProgrammiererLogik();
        ProgrammiererLogik dword = new ProgrammiererLogik();

        // Act
        word.setWortbreite(Wortbreite.WORD);
        word.setBasis(Basis.HEX);
        for (int i = 0; i < 4; i++) word.digitEingeben("F");

        dword.setWortbreite(Wortbreite.DWORD);
        dword.setBasis(Basis.HEX);
        for (int i = 0; i < 8; i++) dword.digitEingeben("F");

        // Assert
        assertEquals("-1", word.getAnzeige(Basis.DEC));
        assertEquals("-1", dword.getAnzeige(Basis.DEC));
    }

    @Test
    void qwordUnsigned_ShouldSupportUnsignedLongMaxValue()
    {
        // Arrange
        logik.setUnsigned(true);
        logik.setBasis(Basis.HEX);

        // Act
        for (int i = 0; i < 16; i++) logik.digitEingeben("F");

        // Assert
        assertEquals("18446744073709551615", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void shiftRightLogical_ShouldClearSignBit_WhenValueIsNegativeInByteMode()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.setBasis(Basis.HEX);
        logik.digitEingeben("8");
        logik.digitEingeben("0");

        // Act
        logik.shiftRightLogical();

        // Assert
        assertEquals("40", logik.getAnzeige(Basis.HEX));
        assertEquals("64", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void shiftLeft_ShouldMaskResult_WhenByteOverflows()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.setBasis(Basis.HEX);
        logik.digitEingeben("8");
        logik.digitEingeben("0");

        // Act
        logik.shiftLeft();

        // Assert
        assertEquals("0", logik.getAnzeige(Basis.HEX));
        assertEquals("0", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void not_ShouldRespectEveryWordWidth_WhenAllBitsAreZero()
    {
        // Arrange
        ProgrammiererLogik byteLogik = new ProgrammiererLogik();
        ProgrammiererLogik wordLogik = new ProgrammiererLogik();
        ProgrammiererLogik dwordLogik = new ProgrammiererLogik();
        ProgrammiererLogik qwordLogik = new ProgrammiererLogik();

        // Act
        byteLogik.setWortbreite(Wortbreite.BYTE);
        byteLogik.not();

        wordLogik.setWortbreite(Wortbreite.WORD);
        wordLogik.not();

        dwordLogik.setWortbreite(Wortbreite.DWORD);
        dwordLogik.not();

        qwordLogik.setWortbreite(Wortbreite.QWORD);
        qwordLogik.not();

        // Assert
        assertEquals("FF", byteLogik.getAnzeige(Basis.HEX));
        assertEquals("FFFF", wordLogik.getAnzeige(Basis.HEX));
        assertEquals("FFFFFFFF", dwordLogik.getAnzeige(Basis.HEX));
        assertEquals("FFFFFFFFFFFFFFFF", qwordLogik.getAnzeige(Basis.HEX));
    }

    @Test
    void operations_ShouldRemainStableAcrossBaseChanges()
    {
        // Arrange
        logik.setBasis(Basis.HEX);
        logik.digitEingeben("F");
        logik.and();
        logik.setBasis(Basis.BIN);
        logik.digitEingeben("1");
        logik.digitEingeben("0");
        logik.digitEingeben("1");
        logik.digitEingeben("0");

        // Act
        logik.berechne();

        // Assert
        assertEquals("A", logik.getAnzeige(Basis.HEX));
    }

    @Test
    void changingWordWidthAndSignedMode_ShouldReMaskPendingValue()
    {
        // Arrange
        logik.setBasis(Basis.HEX);
        logik.digitEingeben("F");
        logik.digitEingeben("F");

        // Act
        logik.setWortbreite(Wortbreite.BYTE);
        String signed = logik.getAnzeige(Basis.DEC);
        logik.setUnsigned(true);
        String unsigned = logik.getAnzeige(Basis.DEC);

        // Assert
        assertEquals("-1", signed);
        assertEquals("255", unsigned);
    }

    @Test
    void backspace_ShouldClearResult_WhenPressedAfterCalculation()
    {
        // Arrange
        logik.digitEingeben("2");
        logik.plus();
        logik.digitEingeben("3");
        logik.berechne();

        // Act
        logik.backspace();

        // Assert
        assertEquals("0", logik.getAktuelleEingabe());
        assertEquals("0", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void backspace_ShouldWorkAfterBaseChange()
    {
        // Arrange
        logik.digitEingeben("1");
        logik.digitEingeben("5");
        logik.setBasis(Basis.HEX);

        // Act
        logik.backspace();

        // Assert
        assertEquals("0", logik.getAktuelleEingabe());
        assertEquals("0", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void wordWidthChangeAfterOperation_ShouldMaskCalculatedResult()
    {
        // Arrange
        logik.digitEingeben("2");
        logik.digitEingeben("5");
        logik.digitEingeben("5");
        logik.plus();
        logik.digitEingeben("1");
        logik.berechne();

        // Act
        logik.setWortbreite(Wortbreite.BYTE);

        // Assert
        assertEquals("0", logik.getAnzeige(Basis.DEC));
        assertEquals("0", logik.getAnzeige(Basis.HEX));
    }

    @Test
    void signedUnsignedChangeAfterOperation_ShouldReinterpretCalculatedResult()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.digitEingeben("1");
        logik.digitEingeben("2");
        logik.digitEingeben("7");
        logik.plus();
        logik.digitEingeben("1");
        logik.berechne();

        // Act
        String signed = logik.getAnzeige(Basis.DEC);
        logik.setUnsigned(true);
        String unsigned = logik.getAnzeige(Basis.DEC);

        // Assert
        assertEquals("-128", signed);
        assertEquals("128", unsigned);
    }

    @Test
    void clear_ShouldResetPendingOperation()
    {
        // Arrange
        logik.digitEingeben("5");
        logik.and();

        // Act
        logik.clear();

        // Assert
        assertEquals("", logik.getPendingOperationText());
        assertEquals("0", logik.getAnzeige(Basis.DEC));
    }

    @Test
    void berechne_ShouldDoNothing_WhenNoOperationIsPending()
    {
        // Arrange
        logik.digitEingeben("7");

        // Act
        logik.berechne();

        // Assert
        assertEquals("7", logik.getAnzeige(Basis.DEC));
        assertEquals("", logik.getPendingOperationText());
    }

    @Test
    void berechne_ShouldEvaluateChainedOperationsFromLeftToRight()
    {
        // Arrange
        logik.setBasis(Basis.HEX);
        logik.digitEingeben("A");
        logik.and();
        logik.digitEingeben("F");
        logik.or();
        logik.digitEingeben("1");

        // Act
        logik.berechne();

        // Assert
        assertEquals("B", logik.getAnzeige(Basis.HEX));
    }

    @Test
    void digitEingeben_ShouldLimitInputLength_WhenByteHexIsFull()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.setBasis(Basis.HEX);

        // Act
        logik.digitEingeben("A");
        logik.digitEingeben("B");
        logik.digitEingeben("C");

        // Assert
        assertEquals("AB", logik.getAktuelleEingabe());
    }

    @Test
    void digitEingeben_ShouldLimitInputLength_WhenByteBinaryIsFull()
    {
        // Arrange
        logik.setWortbreite(Wortbreite.BYTE);
        logik.setBasis(Basis.BIN);

        // Act
        for (int i = 0; i < 12; i++)
        {
            logik.digitEingeben("1");
        }

        // Assert
        assertEquals("11111111", logik.getAktuelleEingabe());
    }
}
