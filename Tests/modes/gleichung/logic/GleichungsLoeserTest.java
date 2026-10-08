package modes.gleichung.logic;

import modes.gleichung.model.GleichungsErgebnis;
import modes.gleichung.model.GleichungsErgebnis.Art;
import modes.komplex.model.KomplexeZahl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GleichungsLoeserTest
{
    private static final double DELTA = 1e-9;
    private final GleichungsLoeser loeser = new GleichungsLoeser();

    @Test
    void loese_ShouldSolveLinearEquationWhenAIsZero()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese(0, 2, -6);

        // Assert
        assertEquals(Art.REELL, ergebnis.art());
        assertEquals(1, ergebnis.loesungen().size());
        assertEquals(3.0, ergebnis.loesungen().get(0).getReal(), DELTA);
        assertEquals("x = 3", ergebnis.anzeige());
    }

    @Test
    void loese_ShouldReportNoSolutionForContradiction()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese(0, 0, 5);

        // Assert
        assertEquals(Art.KEINE_LOESUNG, ergebnis.art());
        assertTrue(ergebnis.loesungen().isEmpty());
        assertEquals("Keine Lösung", ergebnis.anzeige());
    }

    @Test
    void loese_ShouldReportInfinitelyManySolutionsForZeroEqualsZero()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese(0, 0, 0);

        // Assert
        assertEquals(Art.UNENDLICH, ergebnis.art());
        assertEquals("Unendlich viele Lösungen", ergebnis.anzeige());
    }

    @Test
    void loese_ShouldReturnTwoSortedRealSolutionsWithShortSteps()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese(1, -5, 6);

        // Assert
        assertEquals(Art.REELL, ergebnis.art());
        assertEquals(2.0, ergebnis.loesungen().get(0).getReal(), DELTA);
        assertEquals(3.0, ergebnis.loesungen().get(1).getReal(), DELTA);
        assertEquals("D = b² − 4ac = 1", ergebnis.rechenweg().get(0));
        assertTrue(ergebnis.rechenweg().size() <= 4);
    }

    @Test
    void loese_ShouldReturnDoubleRootWhenDiscriminantIsOnlyRoundingNoise()
    {
        // Arrange – 0,1x² + 0,2x + 0,1: b² − 4ac ist in double nicht exakt 0
        // Act
        GleichungsErgebnis ergebnis = loeser.loese(0.1, 0.2, 0.1);

        // Assert
        assertEquals(Art.REELL, ergebnis.art());
        assertEquals(1, ergebnis.loesungen().size());
        assertEquals(-1.0, ergebnis.loesungen().get(0).getReal(), DELTA);
    }

    @Test
    void loese_ShouldReturnConjugateComplexSolutionsForNegativeDiscriminant()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese(1, -2, 5);

        // Assert
        assertEquals(Art.KOMPLEX, ergebnis.art());
        KomplexeZahl x1 = ergebnis.loesungen().get(0);
        KomplexeZahl x2 = ergebnis.loesungen().get(1);
        assertEquals(1.0, x1.getReal(), DELTA);
        assertEquals(2.0, x1.getImaginaer(), DELTA);
        assertEquals(-2.0, x2.getImaginaer(), DELTA);
        assertEquals("x₁ = 1 + 2i,  x₂ = 1 - 2i", ergebnis.anzeige());
    }

    @Test
    void loese_ShouldStayAccurateWhenBIsMuchLargerThanAC()
    {
        // Act – naive Formel verliert bei der kleinen Lösung fast alle Stellen
        GleichungsErgebnis ergebnis = loeser.loese(1, 1e8, 1);

        // Assert
        assertEquals(-1e-8, ergebnis.loesungen().get(1).getReal(), 1e-20);
    }

    @Test
    void loese_ShouldRejectNonFiniteCoefficients()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loeser.loese(Double.NaN, 1, 1));
    }

    @Test
    void loeseText_ShouldSolveLinearEquationFromBothSides()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese("2x + 3 = 7");

        // Assert
        assertEquals(Art.REELL, ergebnis.art());
        assertEquals(2.0, ergebnis.loesungen().get(0).getReal(), DELTA);
        assertEquals("Umgeformt: 2x − 4 = 0", ergebnis.rechenweg().get(0));
    }

    @Test
    void loeseText_ShouldSolveQuadraticEquationWithParentheses()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese("x(x - 1) = 2");

        // Assert
        assertEquals(-1.0, ergebnis.loesungen().get(0).getReal(), DELTA);
        assertEquals(2.0, ergebnis.loesungen().get(1).getReal(), DELTA);
        assertEquals("Umgeformt: x² − x − 2 = 0", ergebnis.rechenweg().get(0));
    }

    @Test
    void loeseText_ShouldTreatMissingEqualsSignAsEqualsZero()
    {
        // Act
        GleichungsErgebnis ergebnis = loeser.loese("x^2 + 1");

        // Assert
        assertEquals(Art.KOMPLEX, ergebnis.art());
    }

    @Test
    void loeseText_ShouldDetectIdentityAndContradiction()
    {
        // Act & Assert
        assertEquals(Art.UNENDLICH, loeser.loese("2(x + 1) = 2x + 2").art());
        assertEquals(Art.KEINE_LOESUNG, loeser.loese("x + 1 = x").art());
    }

    @Test
    void loeseText_ShouldRejectHigherDegreeAndNonPolynomials()
    {
        // Act
        IllegalArgumentException kubisch = assertThrows(IllegalArgumentException.class, () -> loeser.loese("x^3 = 1"));
        IllegalArgumentException sinus = assertThrows(IllegalArgumentException.class, () -> loeser.loese("sin(pi*x) = 0"));
        IllegalArgumentException bruch = assertThrows(IllegalArgumentException.class, () -> loeser.loese("1/x = 2"));

        // Assert
        assertEquals(GleichungsLoeser.NUR_GRAD_ZWEI, kubisch.getMessage());
        assertEquals(GleichungsLoeser.NUR_GRAD_ZWEI, sinus.getMessage());
        assertEquals(GleichungsLoeser.NUR_GRAD_ZWEI, bruch.getMessage());
    }

    @Test
    void loeseText_ShouldExplainInvalidInputInGerman()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class, () -> loeser.loese("  "));
        IllegalArgumentException zweiGleich = assertThrows(IllegalArgumentException.class, () -> loeser.loese("x = 1 = 2"));
        IllegalArgumentException syntax = assertThrows(IllegalArgumentException.class, () -> loeser.loese("2x + = 3"));
        IllegalArgumentException variable = assertThrows(IllegalArgumentException.class, () -> loeser.loese("y = 3"));

        // Assert
        assertEquals("Bitte eine Gleichung eingeben.", leer.getMessage());
        assertEquals("Bitte genau ein „=“ mit zwei Seiten verwenden.", zweiGleich.getMessage());
        assertEquals("Die Gleichung ist ungültig – bitte Schreibweise prüfen.", syntax.getMessage());
        assertEquals("Unbekannter Name – als Variable ist nur x erlaubt.", variable.getMessage());
    }
}
