package common.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OperatorRegistryTest
{
    @Test
    void istOperator_ShouldRecognizeKnownOperatorsOnly()
    {
        // Act & Assert
        assertTrue(OperatorRegistry.istOperator("+"));
        assertTrue(OperatorRegistry.istOperator("^"));
        assertTrue(OperatorRegistry.istOperator(OperatorRegistry.UNAERES_MINUS));
        assertFalse(OperatorRegistry.istOperator("?"));
        assertFalse(OperatorRegistry.istOperator(null));
    }

    @Test
    void prioritaet_ShouldRankPowerOverUnaryMinusOverMultiplicationOverAddition()
    {
        // Act & Assert
        assertTrue(OperatorRegistry.prioritaet("^") > OperatorRegistry.prioritaet(OperatorRegistry.UNAERES_MINUS));
        assertTrue(OperatorRegistry.prioritaet(OperatorRegistry.UNAERES_MINUS) > OperatorRegistry.prioritaet("*"));
        assertTrue(OperatorRegistry.prioritaet("*") > OperatorRegistry.prioritaet("+"));
        assertEquals(OperatorRegistry.prioritaet("*"), OperatorRegistry.prioritaet("/"));
    }

    @Test
    void assoziativitaetUndStelligkeit_ShouldMatchMathConventions()
    {
        // Act & Assert
        assertTrue(OperatorRegistry.istRechtsassoziativ("^"));
        assertFalse(OperatorRegistry.istRechtsassoziativ("-"));
        assertEquals(1, OperatorRegistry.stelligkeit(OperatorRegistry.UNAERES_MINUS));
        assertEquals(2, OperatorRegistry.stelligkeit("+"));
    }

    @Test
    void wendeAn_ShouldCalculateAllOperators()
    {
        // Act & Assert
        assertEquals(5.0, OperatorRegistry.wendeAn("+", 2, 3), 0.0);
        assertEquals(-1.0, OperatorRegistry.wendeAn("-", 2, 3), 0.0);
        assertEquals(6.0, OperatorRegistry.wendeAn("*", 2, 3), 0.0);
        assertEquals(2.5, OperatorRegistry.wendeAn("/", 5, 2), 0.0);
        assertEquals(1.0, OperatorRegistry.wendeAn("%", 7, 3), 0.0);
        assertEquals(8.0, OperatorRegistry.wendeAn("^", 2, 3), 0.0);
        assertEquals(-4.0, OperatorRegistry.wendeAn(OperatorRegistry.UNAERES_MINUS, 4, 99), 0.0);
    }

    @Test
    void wendeAn_ShouldThrowParserException_WhenDividingByZero()
    {
        // Act & Assert
        assertThrows(AusdruckParserException.class, () -> OperatorRegistry.wendeAn("/", 1, 0));
        assertThrows(AusdruckParserException.class, () -> OperatorRegistry.wendeAn("%", 1, 0));
    }
}
