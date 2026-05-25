package common.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AusdruckTermFinderTest
{
    @Test
    void startLetzterTerm_ShouldFindPlainNumberFunctionAndParenthesizedTerm()
    {
        assertEquals(2, AusdruckTermFinder.startLetzterTerm("1+90"));
        assertEquals(0, AusdruckTermFinder.startLetzterTerm("sin(90)"));
        assertEquals(2, AusdruckTermFinder.startLetzterTerm("2+(3+4)"));
    }

    @Test
    void startLetzterTerm_ShouldIncludeUnaryMinus()
    {
        assertEquals(0, AusdruckTermFinder.startLetzterTerm("-5"));
        assertEquals(2, AusdruckTermFinder.startLetzterTerm("2+-3"));
        assertEquals(2, AusdruckTermFinder.startLetzterTerm("2*(-3)"));
    }

    @Test
    void startLetzteZahl_ShouldFindNumberWithUnaryMinus()
    {
        assertEquals(0, AusdruckTermFinder.startLetzteZahl("-5"));
        assertEquals(2, AusdruckTermFinder.startLetzteZahl("2+-3"));
        assertEquals(2, AusdruckTermFinder.startLetzteZahl("2+12,5"));
    }

    @Test
    void zaehleOffeneKlammern_ShouldCountBalance()
    {
        assertEquals(0, AusdruckTermFinder.zaehleOffeneKlammern("(1+2)"));
        assertEquals(1, AusdruckTermFinder.zaehleOffeneKlammern("((1+2)"));
        assertEquals(-1, AusdruckTermFinder.zaehleOffeneKlammern("1+2)"));
    }

    @Test
    void istOperatorZeichen_ShouldRecognizeCalculatorOperators()
    {
        assertTrue(AusdruckTermFinder.istOperatorZeichen('+'));
        assertTrue(AusdruckTermFinder.istOperatorZeichen('%'));
        assertFalse(AusdruckTermFinder.istOperatorZeichen(','));
    }
}
