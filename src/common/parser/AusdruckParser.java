package common.parser;

import common.state.WinkelModus;

import java.util.List;
import java.util.Map;

public final class AusdruckParser
{
    private AusdruckParser()
    {
    }

    public static double auswerten(String expr, double ans, WinkelModus winkelModus)
    {
        return auswerten(expr, ans, winkelModus, Map.of());
    }

    public static double auswerten(String expr, double ans, WinkelModus winkelModus, Map<String, Double> variablen)
    {
        if (expr == null) throw parserFehler(ParserFehler.SYNTAX, "expr is null");

        List<AusdruckToken> tokens = AusdruckTokenizer.tokenisiere(expr);
        List<AusdruckToken> postfix = AusdruckPostfixKonverter.konvertiere(tokens);
        return AusdruckPostfixAuswerter.werteAus(postfix, ans, winkelModus, variablen == null ? Map.of() : variablen);
    }

    private static AusdruckParserException parserFehler(ParserFehler fehler, String message)
    {
        return new AusdruckParserException(fehler, message);
    }
}
