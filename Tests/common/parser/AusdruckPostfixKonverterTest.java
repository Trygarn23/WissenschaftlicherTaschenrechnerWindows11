package common.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AusdruckPostfixKonverterTest
{
    @Test
    void konvertiere_ShouldRespectPrecedenceParenthesesAndRightAssociativePower()
    {
        assertEquals(
                List.of("2", "3", "4", "+", "*"),
                postfixTexte("2*(3+4)")
        );

        assertEquals(
                List.of("2", "3", "2", "^", "^"),
                postfixTexte("2^3^2")
        );
    }

    @Test
    void konvertiere_ShouldKeepUnaryMinusAfterWrappedExpression()
    {
        assertEquals(
                List.of("3", "5", "+", "u-"),
                postfixTexte("-(3+5)")
        );
    }

    @Test
    void konvertiere_ShouldExposeUnbalancedParenthesesAsParserError()
    {
        AusdruckParserException exception = assertThrows(
                AusdruckParserException.class,
                () -> AusdruckPostfixKonverter.konvertiere(AusdruckTokenizer.tokenisiere("(1+2"))
        );

        assertEquals(ParserFehler.KLAMMERN_UNAUSGEGLICHEN, exception.getFehler());
    }

    private List<String> postfixTexte(String expression)
    {
        return AusdruckPostfixKonverter.konvertiere(AusdruckTokenizer.tokenisiere(expression)).stream()
                .map(AusdruckToken::text)
                .toList();
    }
}
