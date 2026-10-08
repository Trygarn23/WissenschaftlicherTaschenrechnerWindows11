package modes.logik.logic;

import modes.logik.model.Wahrheitstabelle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WahrheitstabellenRechnerTest
{
    private final WahrheitstabellenRechner rechner = new WahrheitstabellenRechner();

    @Test
    void erstelle_ShouldListAllRowsWithFirstVariableAsHighestBit()
    {
        // Act
        Wahrheitstabelle tabelle = rechner.erstelle("A ∧ (B ∨ ¬C)");

        // Assert
        assertEquals(List.of('A', 'B', 'C'), tabelle.variablen());
        assertEquals(8, tabelle.anzahlZeilen());
        assertEquals(List.of(false, false, false, false, true, false, true, true), tabelle.ergebnisse());
        assertTrue(tabelle.wert(4, 0));
        assertFalse(tabelle.wert(4, 1));
        assertFalse(tabelle.wert(4, 2));
        assertEquals("erfüllbar (3 von 8 Zeilen wahr)", tabelle.beschreibung());
    }

    @Test
    void erstelle_ShouldSortVariablesAlphabeticallyAndCountEachOnce()
    {
        // Act
        Wahrheitstabelle tabelle = rechner.erstelle("D ∨ B ∧ D ∨ A ∨ C");

        // Assert
        assertEquals(List.of('A', 'B', 'C', 'D'), tabelle.variablen());
        assertEquals(16, tabelle.anzahlZeilen());
        // falsch nur bei A = C = D = 0, B beliebig
        assertEquals("erfüllbar (14 von 16 Zeilen wahr)", tabelle.beschreibung());
    }

    @Test
    void erstelle_ShouldDetectTautologyAndContradiction()
    {
        // Act & Assert
        assertEquals("Tautologie", rechner.erstelle("A ∨ ¬A").beschreibung());
        assertEquals("Tautologie", rechner.erstelle("(A → B) ↔ (¬A ∨ B)").beschreibung());
        assertEquals("Tautologie", rechner.erstelle("¬(A ∧ B) <-> !A || !B").beschreibung());
        assertEquals("Widerspruch", rechner.erstelle("A && !A").beschreibung());
        assertEquals("Widerspruch", rechner.erstelle("A ⊕ A").beschreibung());
    }

    @Test
    void erstelle_ShouldHandleExpressionWithoutVariables()
    {
        // Act
        Wahrheitstabelle tabelle = rechner.erstelle("1 ∧ 0");

        // Assert
        assertTrue(tabelle.variablen().isEmpty());
        assertEquals(1, tabelle.anzahlZeilen());
        assertEquals("Widerspruch", tabelle.beschreibung());
    }

    @Test
    void erstelle_ShouldComputeBasicOperatorTables()
    {
        // Act & Assert (Zeilen 00, 01, 10, 11)
        assertEquals(List.of(false, true, true, false), rechner.erstelle("A XOR B").ergebnisse());
        assertEquals(List.of(true, true, false, true), rechner.erstelle("A -> B").ergebnisse());
        assertEquals(List.of(true, false, false, true), rechner.erstelle("A <-> B").ergebnisse());
        assertEquals(List.of(false, true, true, true), rechner.erstelle("A | B").ergebnisse());
    }

    @Test
    void erstelle_ShouldAllowSixVariablesButRejectSeven()
    {
        // Act
        Wahrheitstabelle sechs = rechner.erstelle("A ∧ B ∧ C ∧ D ∧ E ∧ F");
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> rechner.erstelle("A ∧ B ∧ C ∧ D ∧ E ∧ F ∧ G"));

        // Assert
        assertEquals(64, sechs.anzahlZeilen());
        assertEquals(1, sechs.anzahlWahr());
        assertEquals("Maximal 6 Variablen erlaubt, gefunden: 7.", fehler.getMessage());
    }
}
