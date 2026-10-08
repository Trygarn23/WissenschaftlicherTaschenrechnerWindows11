package modes.logik.logic;

import modes.logik.model.KvDiagramm;
import modes.logik.model.Wahrheitstabelle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TabellenAuswertungTest
{
    private final WahrheitstabellenRechner rechner = new WahrheitstabellenRechner();
    private final TabellenAuswertung auswertung = new TabellenAuswertung();

    @Test
    void dnf_ShouldJoinMintermsOfTrueRows()
    {
        // Arrange
        Wahrheitstabelle tabelle = rechner.erstelle("A ⊕ B");

        // Act & Assert
        assertEquals("(¬A ∧ B) ∨ (A ∧ ¬B)", auswertung.dnf(tabelle));
        assertEquals("(A ∨ B) ∧ (¬A ∨ ¬B)", auswertung.knf(tabelle));
    }

    @Test
    void dnfAndKnf_ShouldBeEquivalentToOriginal()
    {
        // Arrange
        Wahrheitstabelle original = rechner.erstelle("A ∧ (B ∨ ¬C) → D");

        // Act
        Wahrheitstabelle ausDnf = rechner.erstelle(auswertung.dnf(original));
        Wahrheitstabelle ausKnf = rechner.erstelle(auswertung.knf(original));

        // Assert
        assertEquals(original, ausDnf);
        assertEquals(original, ausKnf);
    }

    @Test
    void normalformen_ShouldUseConstantsForTautologyAndContradiction()
    {
        // Act & Assert
        assertEquals("1", auswertung.knf(rechner.erstelle("A ∨ ¬A")));
        assertEquals("0", auswertung.dnf(rechner.erstelle("A ∧ ¬A")));
        assertEquals("1", auswertung.dnf(rechner.erstelle("1")));
        assertEquals("0", auswertung.knf(rechner.erstelle("0")));
        assertEquals("A", auswertung.dnf(rechner.erstelle("A")));
    }

    @Test
    void kvDiagramm_ShouldUseGrayCodeOrderForFourVariables()
    {
        // Arrange
        Wahrheitstabelle tabelle = rechner.erstelle("A ∧ B ∧ C ∧ ¬D");

        // Act
        KvDiagramm kv = auswertung.kvDiagramm(tabelle);

        // Assert
        assertEquals("AB", kv.zeilenVariablen());
        assertEquals("CD", kv.spaltenVariablen());
        assertEquals(List.of("00", "01", "11", "10"), kv.zeilenKoepfe());
        assertEquals(List.of("00", "01", "11", "10"), kv.spaltenKoepfe());
        // AB = 11 ist Zeile 2, CD = 10 ist Spalte 3
        assertTrue(kv.wert(2, 3));
        long wahr = kv.werte().stream().flatMap(List::stream).filter(b -> b).count();
        assertEquals(1, wahr);
    }

    @Test
    void kvDiagramm_ShouldSplitThreeVariablesIntoOneRowAndTwoColumnBits()
    {
        // Arrange
        Wahrheitstabelle tabelle = rechner.erstelle("A ∧ (B ∨ ¬C)");

        // Act
        KvDiagramm kv = auswertung.kvDiagramm(tabelle);

        // Assert
        assertEquals("A", kv.zeilenVariablen());
        assertEquals("BC", kv.spaltenVariablen());
        assertEquals(List.of("0", "1"), kv.zeilenKoepfe());
        assertEquals(List.of(false, false, false, false), kv.werte().get(0));
        // BC = 00, 01, 11, 10 -> A ∧ (B ∨ ¬C) = 1, 0, 1, 1
        assertEquals(List.of(true, false, true, true), kv.werte().get(1));
    }

    @Test
    void kvDiagramm_ShouldRejectTooFewOrTooManyVariables()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> auswertung.kvDiagramm(rechner.erstelle("A")));
        assertThrows(IllegalArgumentException.class, () -> auswertung.kvDiagramm(rechner.erstelle("A ∧ B ∧ C ∧ D ∧ E")));
    }
}
