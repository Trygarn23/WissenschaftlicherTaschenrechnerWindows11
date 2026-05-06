import common.formatting.ZahlenFormatter;
import common.logic.AusdruckEditor;
import common.logic.BerechnungsErgebnis;
import common.logic.BerechnungsFehler;
import common.logic.BerechnungsService;
import common.logic.SpeicherService;
import common.state.RechnerZustand;
import common.state.SpeicherState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BerechnungsUndSpeicherServiceTest
{
    private RechnerZustand zustand;
    private SpeicherState speicherState;
    private AusdruckEditor ausdruckEditor;
    private BerechnungsService berechnungsService;
    private SpeicherService speicherService;

    @BeforeEach
    void setUp()
    {
        ZahlenFormatter zahlenFormatter = new ZahlenFormatter();
        zustand = new RechnerZustand();
        speicherState = new SpeicherState();
        ausdruckEditor = new AusdruckEditor(zustand, zahlenFormatter);
        berechnungsService = new BerechnungsService(zustand, zahlenFormatter);
        speicherService = new SpeicherService(speicherState, berechnungsService, ausdruckEditor, zahlenFormatter);
    }

    @Test
    void berechne_ShouldUpdateStateHistoryAndLastAnswer_WhenExpressionIsValid()
    {
        // Arrange
        zustand.setAusdruckText("2+3");

        // Act
        String result = berechnungsService.berechne();

        // Assert
        assertEquals("5", result);
        assertEquals("5", zustand.getAusdruckText());
        assertEquals("2+3 = 5", zustand.getVerlaufText());
        assertEquals(5.0, zustand.getLetzteAntwort(), 1e-10);
        assertTrue(zustand.isGleichGedrueckt());
    }

    @Test
    void aktuellerWertOder0_ShouldReturnZero_WhenExpressionEndsWithOperator()
    {
        // Arrange
        zustand.setAusdruckText("2+");

        // Act
        double currentValue = berechnungsService.aktuellerWertOder0();

        // Assert
        assertEquals(0.0, currentValue, 1e-10);
    }

    @Test
    void berechne_ShouldReturnFehlerAndClearState_WhenExpressionIsInvalid()
    {
        // Arrange
        zustand.setAusdruckText("sqrt(-1)");
        zustand.setVerlaufText("old history");

        // Act
        String result = berechnungsService.berechne();

        // Assert
        assertEquals("Fehler", result);
        assertEquals("", zustand.getAusdruckText());
        assertEquals("", zustand.getVerlaufText());
        assertTrue(zustand.isGleichGedrueckt());
    }

    @Test
    void berechneDetailliert_ShouldExposeErrorType_WhenDivisionByZeroIsUsed()
    {
        // Arrange
        zustand.setAusdruckText("1/0");

        // Act
        BerechnungsErgebnis result = berechnungsService.berechneDetailliert();

        // Assert
        assertFalse(result.isErfolgreich());
        assertEquals("Fehler", result.getAnzeigeText());
        assertEquals(BerechnungsFehler.DIVISION_DURCH_NULL, result.getFehler());
        assertEquals("Division durch 0 ist nicht definiert.", result.getFehlerMeldung());
    }

    @Test
    void speicherService_ShouldAddRecallSubtractAndClearMemory_WhenMemoryOperationsAreUsed()
    {
        // Arrange
        zustand.setAusdruckText("10");

        // Act
        String afterAdd = speicherService.speicherAddieren();
        ausdruckEditor.allesLoeschen();
        String afterRecall = speicherService.speicherAbrufen();
        zustand.setAusdruckText("3");
        String afterSubtract = speicherService.speicherSubtrahieren();
        String afterClear = speicherService.speicherLoeschen();

        // Assert
        assertEquals("10", afterAdd);
        assertEquals("10", afterRecall);
        assertEquals("7", afterSubtract);
        assertEquals("0", afterClear);
        assertEquals(0.0, speicherState.getWert(), 1e-10);
    }
}
