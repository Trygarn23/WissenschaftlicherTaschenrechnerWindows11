package common.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AusdruckCursorTest
{
    private RechnerService rechner;
    private AusdruckEditor editor;

    @BeforeEach
    void setUp()
    {
        rechner = new RechnerService();
        editor = rechner.getAusdruckEditor();
    }

    @Test
    void amCursor_ShouldAppend_WhenCursorIsAtEnd()
    {
        // Arrange
        rechner.setAusdruckText("12");

        // Act
        rechner.amCursor(() -> rechner.eingabeZahl("3"));

        // Assert
        assertEquals("123", rechner.getAusdruckText());
        assertEquals(-1, rechner.cursorAnzeigeIndex());
    }

    @Test
    void amCursor_ShouldInsertDigitAndMoveCursor_WhenCursorIsInsideExpression()
    {
        // Arrange
        rechner.setAusdruckText("12+3");
        editor.setzeCursor(2);

        // Act
        rechner.amCursor(() -> rechner.eingabeZahl("5"));

        // Assert
        assertEquals("125+3", rechner.getAusdruckText());
        assertEquals(3, editor.getCursor());
    }

    @Test
    void amCursor_ShouldInsertOperator_WhenCursorIsBetweenDigits()
    {
        // Arrange
        rechner.setAusdruckText("123");
        editor.setzeCursor(1);

        // Act
        rechner.amCursor(() -> rechner.operatorSetzen("+"));

        // Assert
        assertEquals("1+23", rechner.getAusdruckText());
        assertEquals(2, editor.getCursor());
    }

    @Test
    void amCursor_ShouldDeleteCharacterBeforeCursor_WhenBackspaceIsPressedInside()
    {
        // Arrange
        rechner.setAusdruckText("12+3");
        editor.setzeCursor(3);

        // Act
        rechner.amCursor(rechner::loeschen);

        // Assert
        assertEquals("123", rechner.getAusdruckText());
        assertEquals(2, editor.getCursor());
    }

    @Test
    void amCursor_ShouldDoNothing_WhenBackspaceIsPressedAtStart()
    {
        // Arrange
        rechner.setAusdruckText("12");
        editor.setzeCursor(0);

        // Act
        rechner.amCursor(rechner::loeschen);

        // Assert
        assertEquals("12", rechner.getAusdruckText());
        assertEquals(0, editor.getCursor());
    }

    @Test
    void amCursor_ShouldDropRestOfExpression_WhenActionEndsInError()
    {
        // Arrange
        rechner.setAusdruckText("1,5+2");
        editor.setzeCursor(3);

        // Act
        rechner.amCursor(editor::fakultaet);

        // Assert
        assertEquals("", rechner.getAusdruckText());
    }

    @Test
    void bewegeCursor_ShouldStayInsideExpression_WhenMovedPastTheEdges()
    {
        // Arrange
        rechner.setAusdruckText("12");

        // Act
        rechner.bewegeCursor(-5);
        int nachLinks = editor.getCursor();
        rechner.bewegeCursor(9);

        // Assert
        assertEquals(0, nachLinks);
        assertEquals(2, editor.getCursor());
        assertEquals(-1, rechner.cursorAnzeigeIndex());
    }

    @Test
    void setzeCursor_ShouldContinueResult_WhenClickedAfterEquals()
    {
        // Arrange
        rechner.setAusdruckText("12+30");
        rechner.berechne();

        // Act
        editor.setzeCursor(1);
        rechner.amCursor(() -> rechner.eingabeZahl("0"));

        // Assert
        assertEquals("402", rechner.getAusdruckText());
    }

    @Test
    void ce_ShouldPutCursorBackToEnd_WhenCursorWasInside()
    {
        // Arrange
        rechner.setAusdruckText("12+3");
        editor.setzeCursor(1);

        // Act
        rechner.ce();
        rechner.eingabeZahl("7");
        rechner.eingabeZahl("8");

        // Assert
        assertEquals("78", rechner.getAusdruckText());
    }

    @Test
    void setzeCursorAusAnzeige_ShouldSkipThousandsSeparator_WhenDisplayShowsGroupedNumber()
    {
        // Arrange
        rechner.setAusdruckText("12345+6");

        // Act: Anzeige „12.345+6“, Klick vor die „3“
        rechner.setzeCursorAusAnzeige(3);

        // Assert
        assertEquals(2, editor.getCursor());
        assertEquals(3, rechner.cursorAnzeigeIndex());
    }

    @Test
    void berechneDetailliert_ShouldReportErrorPosition_WhenParserKnowsIt()
    {
        // Arrange
        rechner.setAusdruckText("2+3)");

        // Act
        BerechnungsErgebnis ergebnis = rechner.berechneDetailliert();

        // Assert
        assertFalse(ergebnis.isErfolgreich());
        assertTrue(ergebnis.hatFehlerPosition());
        assertEquals("2+3)", ergebnis.getFehlerAusdruck());
        assertEquals(3, ergebnis.getFehlerPosition());
        assertEquals("", rechner.getAusdruckText());
    }

    @Test
    void fehlerMeldungMitStelle_ShouldCountLikeTheDisplay_WhenExpressionHasThousandsSeparator()
    {
        // Arrange: Anzeige „1.234)“ – die Klammer steht dort an Stelle 6, intern an Stelle 5.
        rechner.setAusdruckText("1234)");
        BerechnungsErgebnis ergebnis = rechner.berechneDetailliert();

        // Act
        String meldung = rechner.fehlerMeldungMitStelle(ergebnis);

        // Assert
        assertEquals(5, rechner.fehlerAnzeigeIndex(ergebnis));
        assertTrue(meldung.startsWith(ergebnis.getFehlerMeldung()));
        assertTrue(meldung.endsWith("(an Stelle 6)"), meldung);
    }

    @Test
    void fehlerMeldungMitStelle_ShouldOnlyGiveMessage_WhenPositionIsUnknown()
    {
        // Arrange
        BerechnungsErgebnis ergebnis = BerechnungsErgebnis.fehler(BerechnungsFehler.SYNTAX);

        // Act
        String meldung = rechner.fehlerMeldungMitStelle(ergebnis);

        // Assert
        assertEquals(ergebnis.getFehlerMeldung(), meldung);
        assertEquals(-1, rechner.fehlerAnzeigeIndex(ergebnis));
    }

    @Test
    void ausdruckZumKorrigieren_ShouldRestoreExpressionWithCursorAtError_WhenPositionIsKnown()
    {
        // Arrange
        rechner.setAusdruckText("2+3)");
        BerechnungsErgebnis ergebnis = rechner.berechneDetailliert();

        // Act
        rechner.ausdruckZumKorrigieren(ergebnis);

        // Assert
        assertEquals("2+3)", rechner.getAusdruckText());
        assertEquals(3, editor.getCursor());
        assertEquals(3, rechner.cursorAnzeigeIndex());
    }
}
