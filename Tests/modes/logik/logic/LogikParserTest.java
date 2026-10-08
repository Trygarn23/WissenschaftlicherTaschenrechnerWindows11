package modes.logik.logic;

import modes.logik.model.Ausdruck;
import modes.logik.model.Operator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LogikParserTest
{
    private final LogikParser parser = new LogikParser();

    private boolean werte(String ausdruck, Map<Character, Boolean> belegung)
    {
        return parser.parse(ausdruck).werteAus(belegung);
    }

    @Test
    void parse_ShouldBuildTreeForSymbolNotation()
    {
        // Act
        Ausdruck ausdruck = parser.parse("A ∧ (B ∨ ¬C)");

        // Assert
        Ausdruck erwartet = new Ausdruck.Verknuepfung(Operator.UND,
                new Ausdruck.Variable('A'),
                new Ausdruck.Verknuepfung(Operator.ODER,
                        new Ausdruck.Variable('B'),
                        new Ausdruck.Nicht(new Ausdruck.Variable('C'))));
        assertEquals(erwartet, ausdruck);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "A ∧ (B ∨ ¬C)",
            "A && (B || !C)",
            "A & (B | ~C)",
            "A AND (B OR NOT C)",
            "A and (B Or nOt C)",
            "A∧(B∨¬C)"
    })
    void parse_ShouldAcceptAlternativeNotations(String eingabe)
    {
        // Act & Assert
        assertEquals(parser.parse("A ∧ (B ∨ ¬C)"), parser.parse(eingabe));
    }

    @Test
    void parse_ShouldAcceptXorImplicationAndEquivalenceVariants()
    {
        // Act & Assert
        assertEquals(parser.parse("A ⊕ B"), parser.parse("A ^ B"));
        assertEquals(parser.parse("A ⊕ B"), parser.parse("A xor B"));
        assertEquals(parser.parse("A → B"), parser.parse("A -> B"));
        assertEquals(parser.parse("A → B"), parser.parse("A => B"));
        assertEquals(parser.parse("A ↔ B"), parser.parse("A <-> B"));
        assertEquals(parser.parse("A ↔ B"), parser.parse("A <=> B"));
    }

    @Test
    void parse_ShouldRespectPrecedenceNotBeforeAndBeforeXorBeforeOr()
    {
        // Arrange
        Map<Character, Boolean> belegung = Map.of('A', true, 'B', false, 'C', true);

        // Act & Assert
        // ¬A ∧ B ist (¬A) ∧ B, nicht ¬(A ∧ B)
        assertFalse(werte("¬A ∧ B", Map.of('A', true, 'B', false)));
        // A ∨ B ∧ C ist A ∨ (B ∧ C)
        assertTrue(werte("A ∨ B ∧ C", Map.of('A', true, 'B', false, 'C', false)));
        // A ⊕ B ∨ C ist (A ⊕ B) ∨ C
        assertEquals(parser.parse("(A ⊕ B) ∨ C"), parser.parse("A ⊕ B ∨ C"));
        // A ∧ B ⊕ C ist (A ∧ B) ⊕ C
        assertEquals(parser.parse("(A ∧ B) ⊕ C"), parser.parse("A ∧ B ⊕ C"));
        assertTrue(werte("A ∧ B ⊕ C", belegung));
    }

    @Test
    void parse_ShouldBindImplicationWeakerThanOrAndStrongerThanEquivalence()
    {
        // Act & Assert
        assertEquals(parser.parse("(A ∨ B) → C"), parser.parse("A ∨ B → C"));
        assertEquals(parser.parse("A ↔ (B → C)"), parser.parse("A ↔ B → C"));
        assertEquals(parser.parse("(A → B) ↔ C"), parser.parse("A → B ↔ C"));
    }

    @Test
    void parse_ShouldTreatImplicationAsRightAssociative()
    {
        // Act & Assert
        assertEquals(parser.parse("A → (B → C)"), parser.parse("A → B → C"));
        // bei Linksassoziativität käme hier falsch heraus
        assertTrue(werte("A → B → C", Map.of('A', false, 'B', true, 'C', false)));
    }

    @Test
    void parse_ShouldTreatEquivalenceAndOrAsLeftAssociative()
    {
        // Act & Assert
        assertEquals(parser.parse("(A ↔ B) ↔ C"), parser.parse("A ↔ B ↔ C"));
        assertEquals(parser.parse("(A ∨ B) ∨ C"), parser.parse("A ∨ B ∨ C"));
    }

    @Test
    void parse_ShouldHandleNestedParenthesesAndDoubleNegation()
    {
        // Act & Assert
        assertEquals(parser.parse("A"), parser.parse("((A))"));
        assertTrue(werte("¬¬A", Map.of('A', true)));
        assertTrue(werte("!(A && B) || (C && D)", Map.of('A', true, 'B', true, 'C', true, 'D', true)));
        assertFalse(werte("!(A && B) || (C && D)", Map.of('A', true, 'B', true, 'C', false, 'D', true)));
    }

    @Test
    void parse_ShouldAcceptConstants()
    {
        // Act & Assert
        assertTrue(werte("1", Map.of()));
        assertFalse(werte("0", Map.of()));
        assertTrue(werte("A ∨ 1", Map.of('A', false)));
    }

    @Test
    void parse_ShouldReportUnexpectedCharacterWithPosition()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> parser.parse("A ∧ %"));

        // Assert
        assertEquals("Unerwartetes Zeichen „%“ an Stelle 5.", fehler.getMessage());
    }

    @Test
    void parse_ShouldReportMissingClosingParenthesis()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> parser.parse("A ∧ (B ∨ C"));

        // Assert
        assertEquals("Schließende Klammer fehlt zur „(“ an Stelle 5.", fehler.getMessage());
    }

    @Test
    void parse_ShouldReportSurplusTokensAndMissingOperands()
    {
        // Act
        IllegalArgumentException zuViel = assertThrows(IllegalArgumentException.class, () -> parser.parse("A B"));
        IllegalArgumentException klammer = assertThrows(IllegalArgumentException.class, () -> parser.parse("A)"));
        IllegalArgumentException ende = assertThrows(IllegalArgumentException.class, () -> parser.parse("A ∧"));
        IllegalArgumentException doppelt = assertThrows(IllegalArgumentException.class, () -> parser.parse("A ∧ ∨ B"));
        IllegalArgumentException leereKlammer = assertThrows(IllegalArgumentException.class, () -> parser.parse("()"));

        // Assert
        assertEquals("Unerwartetes „B“ an Stelle 3.", zuViel.getMessage());
        assertEquals("Unerwartetes „)“ an Stelle 2.", klammer.getMessage());
        assertEquals("Der Ausdruck ist unvollständig – am Ende fehlt ein Operand.", ende.getMessage());
        assertEquals("Unerwartetes „∨“ an Stelle 5.", doppelt.getMessage());
        assertEquals("Unerwartetes „)“ an Stelle 2.", leereKlammer.getMessage());
    }

    @Test
    void parse_ShouldRejectEmptyInputUnknownWordsAndLowercaseVariables()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class, () -> parser.parse("  "));
        IllegalArgumentException wort = assertThrows(IllegalArgumentException.class, () -> parser.parse("AB"));
        IllegalArgumentException klein = assertThrows(IllegalArgumentException.class, () -> parser.parse("A ∧ b"));
        IllegalArgumentException zahl = assertThrows(IllegalArgumentException.class, () -> parser.parse("2"));

        // Assert
        assertEquals("Bitte einen Ausdruck eingeben.", leer.getMessage());
        assertEquals("Unbekanntes Wort „AB“ an Stelle 1 – Variablen sind einzelne Großbuchstaben A–Z.", wort.getMessage());
        assertEquals("Variablen bitte groß schreiben: „b“ an Stelle 5.", klein.getMessage());
        assertEquals("Unerwartetes Zeichen „2“ an Stelle 1.", zahl.getMessage());
    }
}
