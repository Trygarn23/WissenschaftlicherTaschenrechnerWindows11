package common.parser;

import java.util.Map;

/**
 * Einzige Stelle für Operatoren: Priorität, Assoziativität, Stelligkeit und Rechnung.
 */
final class OperatorRegistry
{
    static final String UNAERES_MINUS = "u-";

    @FunctionalInterface
    private interface Rechnung
    {
        double rechne(double a, double b);
    }

    private record Operator(int prioritaet, boolean rechtsassoziativ, int stelligkeit, Rechnung rechnung)
    {
    }

    private static final Map<String, Operator> OPERATOREN = Map.of(
            "+", new Operator(1, false, 2, (a, b) -> a + b),
            "-", new Operator(1, false, 2, (a, b) -> a - b),
            "*", new Operator(2, false, 2, (a, b) -> a * b),
            "/", new Operator(2, false, 2, (a, b) -> a / nichtNull(b, "Division durch 0 ist nicht erlaubt")),
            "%", new Operator(2, false, 2, (a, b) -> a % nichtNull(b, "Modulo durch 0 ist nicht erlaubt")),
            "^", new Operator(4, true, 2, Math::pow),
            // Unter ^: -2^2 = -(2^2), aber über * und /.
            UNAERES_MINUS, new Operator(3, true, 1, (a, b) -> -a)
    );

    private OperatorRegistry()
    {
    }

    static boolean istOperator(String text)
    {
        return text != null && OPERATOREN.containsKey(text);
    }

    static int prioritaet(String operator)
    {
        return OPERATOREN.get(operator).prioritaet();
    }

    static boolean istRechtsassoziativ(String operator)
    {
        return OPERATOREN.get(operator).rechtsassoziativ();
    }

    static int stelligkeit(String operator)
    {
        return OPERATOREN.get(operator).stelligkeit();
    }

    /** Bei einstelligen Operatoren wird {@code b} ignoriert. */
    static double wendeAn(String operator, double a, double b)
    {
        return OPERATOREN.get(operator).rechnung().rechne(a, b);
    }

    private static double nichtNull(double b, String meldung)
    {
        if (b == 0.0)
        {
            throw new AusdruckParserException(ParserFehler.DIVISION_DURCH_NULL, meldung);
        }
        return b;
    }
}
