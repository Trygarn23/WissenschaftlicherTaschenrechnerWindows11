package common.parser;

import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ParserGrenzfaelleTest
{
    @Test
    void auswerten_ShouldHandleTenThousandSummands()
    {
        // Arrange
        String ausdruck = "1" + "+1".repeat(9_999);

        // Act
        double ergebnis = rechne(ausdruck);

        // Assert
        assertEquals(10_000.0, ergebnis, 1e-9);
    }

    @Test
    void auswerten_ShouldHandleDeeplyNestedParenthesesWithoutStackOverflow()
    {
        // Arrange
        String ausdruck = "(".repeat(500) + "2" + ")".repeat(500);
        String funktionen = "abs(".repeat(500) + "-3" + ")".repeat(500);

        // Act & Assert
        assertEquals(2.0, rechne(ausdruck), 1e-12);
        assertEquals(3.0, rechne(funktionen), 1e-12);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "+", "×÷", "()", "-", "*2", "2*", "sin()"})
    void auswerten_ShouldThrowSyntaxError_WhenExpressionIsEmptyOrOnlyOperators(String ausdruck)
    {
        // Act
        AusdruckParserException exception = fehler(ausdruck);

        // Assert
        assertEquals(ParserFehler.SYNTAX, exception.getFehler());
    }

    @ParameterizedTest
    @ValueSource(strings = {"(((", ")", "(1+2))", "sin(30"})
    void auswerten_ShouldThrowParenthesesError_WhenParenthesesDoNotMatch(String ausdruck)
    {
        // Act
        AusdruckParserException exception = fehler(ausdruck);

        // Assert
        assertEquals(ParserFehler.KLAMMERN_UNAUSGEGLICHEN, exception.getFehler());
    }

    @Test
    void fehler_ShouldContainPositionInOriginalExpression()
    {
        // Act
        AusdruckParserException zeichen = fehler("12 + $3");
        AusdruckParserException klammerZu = fehler("1+2)");
        AusdruckParserException klammerAuf = fehler("1+(2");
        AusdruckParserException division = fehler("4 / 0");
        AusdruckParserException name = fehler("2+abc");

        // Assert
        assertEquals(5, zeichen.getPosition());
        assertEquals("Unerwartetes Zeichen „$“ an Stelle 6", zeichen.getMessage());
        assertEquals(3, klammerZu.getPosition());
        assertEquals(2, klammerAuf.getPosition());
        assertEquals(2, division.getPosition());
        assertEquals(ParserFehler.DIVISION_DURCH_NULL, division.getFehler());
        assertEquals(2, name.getPosition());
    }

    @Test
    void fehler_ShouldKeepOldConstructorWithoutPosition()
    {
        // Act
        AusdruckParserException exception = new AusdruckParserException(ParserFehler.SYNTAX, "kaputt");

        // Assert
        assertEquals(-1, exception.getPosition());
        assertEquals("kaputt", exception.getMessage());
    }

    @Test
    void tokenisiere_ShouldKeepPositionsAfterWhitespaceAndUnicodeOperators()
    {
        // Act
        var tokens = AusdruckTokenizer.tokenisiere(" 2 × π");

        // Assert
        assertEquals(1, tokens.get(0).position());
        assertEquals(3, tokens.get(1).position());
        assertEquals(5, tokens.get(2).position());
        assertEquals("pi", tokens.get(2).text());
    }

    @Test
    void auswerten_ShouldBindPowerStrongerThanUnaryMinus()
    {
        // Act & Assert
        assertEquals(-4.0, rechne("-2^2"), 1e-12);
        assertEquals(-9.0, AusdruckParser.auswerten("-x^2", 0.0, WinkelModus.RAD, java.util.Map.of("x", 3.0)), 1e-12);
        assertEquals(0.25, rechne("2^-2"), 1e-12);
        assertEquals(4.0, rechne("(-2)^2"), 1e-12);
        assertEquals(1.0 / 16.0, rechne("2^-2^2"), 1e-12);
        assertEquals(-6.0, rechne("2*-3"), 1e-12);
        assertEquals(1.0, rechne("5-2^2"), 1e-12);
        assertEquals(-8.0, rechne("-2^3"), 1e-12);
        assertEquals(-4.0, rechne("-(2)^2"), 1e-12);
    }

    @Test
    void auswerten_ShouldAcceptUnaryMinusAfterSemicolon()
    {
        // Act & Assert
        assertEquals(2.0, rechne("ggT(-4;-(6))"), 1e-12);
    }

    private static double rechne(String ausdruck)
    {
        return AusdruckParser.auswerten(ausdruck, 0.0, WinkelModus.DEG);
    }

    private static AusdruckParserException fehler(String ausdruck)
    {
        return assertThrows(AusdruckParserException.class, () -> rechne(ausdruck));
    }
}
