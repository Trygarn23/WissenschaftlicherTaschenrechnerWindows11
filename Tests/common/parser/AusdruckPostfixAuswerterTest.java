package common.parser;

import common.state.WinkelModus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AusdruckPostfixAuswerterTest
{
    @Test
    void werteAus_ShouldEvaluateOperatorsFunctionsAndConstants()
    {
        List<AusdruckToken> postfix = postfix("sin(90)+2pi");

        double result = AusdruckPostfixAuswerter.werteAus(postfix, 0.0, WinkelModus.DEG, Map.of());

        assertEquals(1.0 + 2.0 * Math.PI, result, 1e-10);
    }

    @Test
    void werteAus_ShouldUseAnsAndVariables()
    {
        List<AusdruckToken> postfix = postfix("ans+x^2");

        double result = AusdruckPostfixAuswerter.werteAus(postfix, 3.0, WinkelModus.DEG, Map.of("x", 4.0));

        assertEquals(19.0, result, 1e-10);
    }

    private List<AusdruckToken> postfix(String expression)
    {
        return AusdruckPostfixKonverter.konvertiere(AusdruckTokenizer.tokenisiere(expression));
    }
}
