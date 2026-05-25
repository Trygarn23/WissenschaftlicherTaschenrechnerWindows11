import common.formatting.ZahlenFormatter;
import common.logic.BerechnungsErgebnis;
import common.logic.BerechnungsFehler;
import common.logic.BerechnungsService;
import common.state.RechnerZustand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BerechnungsServiceRegressionTest
{
    private RechnerZustand zustand;
    private BerechnungsService service;

    @BeforeEach
    void setUp()
    {
        zustand = new RechnerZustand();
        service = new BerechnungsService(zustand, new ZahlenFormatter());
    }

    @Test
    void berechneDetailliert_ShouldReturnSuccessWithDisplayAndHistory()
    {
        zustand.setAusdruckText("2*(3+4)");

        BerechnungsErgebnis ergebnis = service.berechneDetailliert();

        assertTrue(ergebnis.isErfolgreich());
        assertEquals(BerechnungsFehler.KEINER, ergebnis.getFehler());
        assertEquals("14", ergebnis.getAnzeigeText());
        assertEquals("2*(3+4) = 14", ergebnis.getVerlaufText());
        assertEquals("14", zustand.getAusdruckText());
        assertEquals("2*(3+4) = 14", zustand.getVerlaufText());
        assertEquals(14.0, zustand.getLetzteAntwort(), 1e-10);
        assertTrue(zustand.isGleichGedrueckt());
    }

    @Test
    void berechneDetailliert_ShouldExposeDivisionByZeroError()
    {
        zustand.setAusdruckText("1/0");

        BerechnungsErgebnis ergebnis = service.berechneDetailliert();

        assertFailure(ergebnis, BerechnungsFehler.DIVISION_DURCH_NULL);
    }

    @Test
    void berechneDetailliert_ShouldExposeFunctionDomainError()
    {
        zustand.setAusdruckText("sqrt(-1)");

        BerechnungsErgebnis ergebnis = service.berechneDetailliert();

        assertFailure(ergebnis, BerechnungsFehler.UNGUELTIGER_FUNKTIONSBEREICH);
    }

    @Test
    void berechneDetailliert_ShouldExposeUnbalancedParenthesesError()
    {
        zustand.setAusdruckText("(1+2");

        BerechnungsErgebnis ergebnis = service.berechneDetailliert();

        assertFailure(ergebnis, BerechnungsFehler.KLAMMERN_UNAUSGEGLICHEN);
    }

    @Test
    void berechneDetailliert_ShouldClearExpressionAndHistoryButKeepLastAnswer_WhenErrorHappens()
    {
        zustand.setLetzteAntwort(42.0);
        zustand.setAusdruckText("ln(0)");
        zustand.setVerlaufText("alter Verlauf");
        zustand.setGleichGedrueckt(false);

        BerechnungsErgebnis ergebnis = service.berechneDetailliert();

        assertFailure(ergebnis, BerechnungsFehler.UNGUELTIGER_FUNKTIONSBEREICH);
        assertEquals("", zustand.getAusdruckText());
        assertEquals("", zustand.getVerlaufText());
        assertEquals(42.0, zustand.getLetzteAntwort(), 1e-10);
        assertTrue(zustand.isGleichGedrueckt());
    }

    private void assertFailure(BerechnungsErgebnis ergebnis, BerechnungsFehler erwarteterFehler)
    {
        assertFalse(ergebnis.isErfolgreich());
        assertEquals("Fehler", ergebnis.getAnzeigeText());
        assertEquals("", ergebnis.getVerlaufText());
        assertEquals(erwarteterFehler, ergebnis.getFehler());
        assertEquals(erwarteterFehler.getMeldung(), ergebnis.getFehlerMeldung());
    }
}
