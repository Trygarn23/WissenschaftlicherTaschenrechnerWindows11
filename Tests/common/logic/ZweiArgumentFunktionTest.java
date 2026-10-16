package common.logic;

import common.formatting.ZahlenFormatter;
import common.parser.AusdruckParser;
import common.state.RechnerZustand;
import common.state.WinkelModus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZweiArgumentFunktionTest
{
    private RechnerZustand zustand;
    private AusdruckEditor editor;

    @BeforeEach
    void setUp()
    {
        zustand = new RechnerZustand();
        editor = new AusdruckEditor(zustand, new ZahlenFormatter());
    }

    @Test
    void zweiArgumentFunktion_ShouldUseLastNumberAsFirstArgument()
    {
        // Arrange
        editor.eingabeZahl("5");

        // Act
        editor.zweiArgumentFunktion("nCr");
        editor.eingabeZahl("2");
        editor.klammerZu();

        // Assert
        assertEquals("nCr(5;2)", zustand.getAusdruckText());
        assertEquals(10.0, AusdruckParser.auswerten(zustand.getAusdruckText(), 0.0, WinkelModus.DEG), 1e-12);
    }

    @Test
    void zweiArgumentFunktion_ShouldOpenEmptyCall_WhenNothingTypedYet()
    {
        // Act
        editor.zweiArgumentFunktion("ggT");
        editor.eingabeZahl("12");
        editor.trennzeichen();
        editor.eingabeZahl("18");
        editor.klammerZu();

        // Assert
        assertEquals("ggT(12;18)", zustand.getAusdruckText());
        assertEquals(6.0, AusdruckParser.auswerten(zustand.getAusdruckText(), 0.0, WinkelModus.DEG), 1e-12);
    }

    @Test
    void trennzeichen_ShouldBeIgnored_OutsideBracketsOrAfterOperator()
    {
        // Arrange
        editor.eingabeZahl("4");

        // Act & Assert
        assertEquals("4", editor.trennzeichen());
        editor.zweiArgumentFunktion("kgV");
        assertEquals("kgV(4;", editor.trennzeichen());
    }

    @Test
    void operatorSetzen_ShouldAllowNegativeSecondArgument()
    {
        // Arrange
        editor.zweiArgumentFunktion("ggT");
        editor.eingabeZahl("8");
        editor.trennzeichen();

        // Act
        editor.operatorSetzen("-");
        editor.eingabeZahl("12");

        // Assert
        assertEquals("ggT(8;-12", zustand.getAusdruckText());
    }
}
