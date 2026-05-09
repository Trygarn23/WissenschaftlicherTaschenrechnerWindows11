import common.state.RechnerZustand;
import common.state.SpeicherState;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RechnerZustandUndSpeicherStateTest
{
    @Test
    void rechnerZustand_ShouldExposeDefaultValues_WhenCreated()
    {
        // Arrange
        RechnerZustand zustand = new RechnerZustand();

        // Act
        WinkelModus winkelModus = zustand.getWinkelModus();
        boolean gleichGedrueckt = zustand.isGleichGedrueckt();
        String ausdruck = zustand.getAusdruckText();
        String verlauf = zustand.getVerlaufText();
        double letzteAntwort = zustand.getLetzteAntwort();

        // Assert
        assertEquals(WinkelModus.DEG, winkelModus);
        assertFalse(gleichGedrueckt);
        assertEquals("", ausdruck);
        assertEquals("", verlauf);
        assertEquals(0.0, letzteAntwort, 1e-10);
    }

    @Test
    void rechnerZustand_ShouldToggleAngleMode_WhenWinkelModusUmschaltenIsCalled()
    {
        // Arrange
        RechnerZustand zustand = new RechnerZustand();

        // Act
        zustand.winkelModusUmschalten();
        WinkelModus afterFirstToggle = zustand.getWinkelModus();
        zustand.winkelModusUmschalten();
        WinkelModus afterSecondToggle = zustand.getWinkelModus();

        // Assert
        assertEquals(WinkelModus.RAD, afterFirstToggle);
        assertEquals(WinkelModus.DEG, afterSecondToggle);
    }

    @Test
    void rechnerZustand_ShouldSetAndClearExpressionAndHistory_WhenTextMethodsAreUsed()
    {
        // Arrange
        RechnerZustand zustand = new RechnerZustand();

        // Act
        zustand.setAusdruckText("2+3");
        zustand.appendAusdruck("*4");
        zustand.setVerlaufText("2+3 = 5");
        String ausdruckBeforeClear = zustand.getAusdruckText();
        String verlaufBeforeClear = zustand.getVerlaufText();
        zustand.clearAusdruck();
        zustand.clearVerlauf();

        // Assert
        assertEquals("2+3*4", ausdruckBeforeClear);
        assertEquals("2+3 = 5", verlaufBeforeClear);
        assertEquals("", zustand.getAusdruckText());
        assertEquals("", zustand.getVerlaufText());
    }

    @Test
    void speicherState_ShouldAddSubtractAndClearValue_WhenMethodsAreUsed()
    {
        // Arrange
        SpeicherState speicherState = new SpeicherState();

        // Act
        speicherState.setWert(10.0);
        speicherState.addiere(5.0);
        speicherState.subtrahiere(3.0);
        double beforeClear = speicherState.getWert();
        speicherState.loeschen();

        // Assert
        assertEquals(12.0, beforeClear, 1e-10);
        assertEquals(0.0, speicherState.getWert(), 1e-10);
    }
}
