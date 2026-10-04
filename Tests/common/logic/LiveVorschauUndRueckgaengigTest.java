package common.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LiveVorschauUndRueckgaengigTest
{
    private RechnerService rechner;

    @BeforeEach
    void setUp()
    {
        rechner = new RechnerService();
    }

    @Test
    void liveVorschau_ShouldShowResultWhileTyping()
    {
        // Act
        tippe("12+30");

        // Assert
        assertEquals(Optional.of("= 42"), rechner.liveVorschau());
        assertEquals("= 42", rechner.zweiteZeile());
    }

    @Test
    void liveVorschau_ShouldStayEmpty_WhenOnlyANumberOrAnOperatorAtTheEnd()
    {
        tippe("42");
        assertEquals(Optional.empty(), rechner.liveVorschau());

        tippe("+");
        assertEquals(Optional.empty(), rechner.liveVorschau());
    }

    @Test
    void liveVorschau_ShouldShowZero_WhenResultReallyIsZero()
    {
        // Act
        tippe("5-5");

        // Assert
        assertEquals(Optional.of("= 0"), rechner.liveVorschau());
    }

    @Test
    void liveVorschau_ShouldStayEmpty_WhenExpressionIsNotComputable()
    {
        // Act
        tippe("5÷0");

        // Assert
        assertEquals(Optional.empty(), rechner.liveVorschau());
    }

    @Test
    void liveVorschau_ShouldCloseOpenBracketsInThought_AndCountThem()
    {
        // Act
        tippe("2×(3+(1");

        // Assert
        assertEquals(2, rechner.offeneKlammern());
        assertEquals("= 8  ·  2 offen", rechner.zweiteZeile());
    }

    @Test
    void zweiteZeile_ShouldShowLastCalculation_AfterEquals()
    {
        // Act
        tippe("2+3");
        rechner.berechne();

        // Assert
        assertEquals(Optional.empty(), rechner.liveVorschau());
        assertEquals("2+3 = 5", rechner.zweiteZeile());
    }

    @Test
    void rueckgaengig_ShouldRestoreExpression_AfterClear()
    {
        // Arrange
        tippe("12×(3+4)");

        // Act
        rechner.allesLoeschen();
        rechner.allesLoeschen(); // zweimal C darf den Ausdruck nicht verlieren
        String ergebnis = rechner.rueckgaengig();

        // Assert
        assertEquals("12*(3+4)", ergebnis);
        assertEquals("12*(3+4)", rechner.getAusdruckText());
    }

    @Test
    void rueckgaengig_ShouldWorkOnlyOnce_AndNotOverwriteNewInput()
    {
        // Arrange
        tippe("7+8");
        rechner.ce();

        // Act
        tippe("9");
        rechner.rueckgaengig();

        // Assert
        assertEquals("9", rechner.getAusdruckText());
    }

    @Test
    void speicherAddieren_ShouldStillUseZero_WhenExpressionEndsWithOperator()
    {
        // Act
        tippe("5+");
        rechner.speicherAddieren();

        // Assert
        assertEquals(0.0, rechner.getSpeicherWert());
    }

    private void tippe(String eingabe)
    {
        for (char zeichen : eingabe.toCharArray())
        {
            switch (zeichen)
            {
                case '(' -> rechner.klammerAuf();
                case ')' -> rechner.klammerZu();
                case '+', '-', '×', '÷' -> rechner.operatorSetzen(String.valueOf(zeichen));
                default -> rechner.eingabeZahl(String.valueOf(zeichen));
            }
        }
    }
}
