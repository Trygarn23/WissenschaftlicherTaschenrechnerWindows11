package common.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AusdruckTokenizerTest
{
    @Test
    void tokenisiere_ShouldInsertImplicitMultiplication_WhenValueIsBeforeParenthesisOrConstant()
    {
        assertEquals(
                List.of("2", "*", "(", "3", "+", "4", ")"),
                tokenTexte(AusdruckTokenizer.tokenisiere("2(3+4)"))
        );

        assertEquals(
                List.of("2", "*", "pi"),
                tokenTexte(AusdruckTokenizer.tokenisiere("2\u03c0"))
        );
    }

    @Test
    void tokenisiere_ShouldKeepFunctionCallWithoutImplicitMultiplication()
    {
        assertEquals(
                List.of("sin", "(", "90", ")", "+", "2", "*", "pi"),
                tokenTexte(AusdruckTokenizer.tokenisiere("sin(90)+2pi"))
        );
    }

    @Test
    void tokenisiere_ShouldHandleUnaryMinusAndScientificNotation()
    {
        assertEquals(
                List.of("u-", "(", "3", "+", "5", ")", "+", "-2,5e-3"),
                tokenTexte(AusdruckTokenizer.tokenisiere("-(3+5)+-2,5e-3"))
        );
    }

    @Test
    void tokenisiere_ShouldAppendTrailingZero_WhenExpressionEndsWithDecimalSeparator()
    {
        assertEquals(List.of("1,0"), tokenTexte(AusdruckTokenizer.tokenisiere("1,")));
        assertEquals(List.of("1.0"), tokenTexte(AusdruckTokenizer.tokenisiere("1.")));
    }

    @Test
    void tokenisiere_ShouldThrowParserException_WhenTokenIsUnknown()
    {
        AusdruckParserException exception = assertThrows(
                AusdruckParserException.class,
                () -> AusdruckTokenizer.tokenisiere("2$3")
        );

        assertEquals(ParserFehler.SYNTAX, exception.getFehler());
    }

    private List<String> tokenTexte(List<AusdruckToken> tokens)
    {
        return tokens.stream()
                .map(AusdruckToken::text)
                .toList();
    }
}
