package common.parser;

import common.state.WinkelModus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

final class AusdruckPostfixAuswerter
{
    private static final String UNARY_MINUS = AusdruckTokenizer.UNARY_MINUS;

    private AusdruckPostfixAuswerter()
    {
    }

    static double werteAus(
            List<AusdruckToken> postfix,
            double ans,
            WinkelModus winkelModus,
            Map<String, Double> variablen)
    {
        Deque<Double> stack = new ArrayDeque<>();

        for (AusdruckToken token : postfix)
        {
            String text = token.text();

            if (AusdruckPostfixKonverter.istZahl(text))
            {
                stack.push(Double.parseDouble(text.replace(',', '.')));
            }
            else if (AusdruckPostfixKonverter.istIdentifier(text) && !AusdruckTokenizer.isFunction(text))
            {
                stack.push(identifierWert(text, ans, variablen));
            }
            else if (UNARY_MINUS.equals(text))
            {
                stack.push(-popOperand(stack));
            }
            else if (AusdruckPostfixKonverter.istOperator(text))
            {
                stack.push(wendeOperatorAn(text, stack));
            }
            else if (AusdruckTokenizer.isFunction(text))
            {
                stack.push(wendeFunktionAn(text, stack, winkelModus));
            }
            else
            {
                throw parserFehler(ParserFehler.SYNTAX, "Unknown token: " + text);
            }
        }

        if (stack.size() != 1)
        {
            throw parserFehler(ParserFehler.SYNTAX, "Invalid expression");
        }

        return stack.pop();
    }

    private static double identifierWert(String text, double ans, Map<String, Double> variablen)
    {
        return switch (text)
        {
            case "pi" -> Math.PI;
            case "e" -> Math.E;
            case "ans" -> ans;
            default ->
            {
                if (variablen.containsKey(text))
                {
                    yield variablen.get(text);
                }
                throw parserFehler(ParserFehler.UNBEKANNTE_FUNKTION, "Unknown identifier: " + text);
            }
        };
    }

    private static double wendeOperatorAn(String operator, Deque<Double> stack)
    {
        double b = popOperand(stack);
        double a = popOperand(stack);

        return switch (operator)
        {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" ->
            {
                if (b == 0.0)
                {
                    throw parserFehler(ParserFehler.DIVISION_DURCH_NULL, "Division by zero");
                }
                yield a / b;
            }
            case "%" ->
            {
                if (b == 0.0)
                {
                    throw parserFehler(ParserFehler.DIVISION_DURCH_NULL, "Modulo by zero");
                }
                yield a % b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw parserFehler(ParserFehler.SYNTAX, "Unknown operator: " + operator);
        };
    }

    private static double wendeFunktionAn(String funktion, Deque<Double> stack, WinkelModus winkelModus)
    {
        double ergebnis;

        if ("rand".equals(funktion))
        {
            ergebnis = Math.random();
        }
        else
        {
            double x = popOperand(stack);
            double trigArg = x;
            if (winkelModus == WinkelModus.DEG && istDirekteTrigonometrie(funktion))
            {
                trigArg = Math.toRadians(x);
            }

            ergebnis = switch (funktion)
            {
                case "sin" -> Math.sin(trigArg);
                case "cos" -> Math.cos(trigArg);
                case "tan" ->
                {
                    if (Math.abs(Math.cos(trigArg)) < 1e-12)
                    {
                        throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "tan undefined");
                    }
                    yield Math.tan(trigArg);
                }
                case "asin" -> inverseTrigonometrie(Math.asin(x), winkelModus);
                case "acos" -> inverseTrigonometrie(Math.acos(x), winkelModus);
                case "atan" -> inverseTrigonometrie(Math.atan(x), winkelModus);
                case "sinh" -> Math.sinh(x);
                case "cosh" -> Math.cosh(x);
                case "tanh" -> Math.tanh(x);
                case "ln" ->
                {
                    if (x <= 0.0)
                    {
                        throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "ln domain");
                    }
                    yield Math.log(x);
                }
                case "log" ->
                {
                    if (x <= 0.0)
                    {
                        throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "log domain");
                    }
                    yield Math.log10(x);
                }
                case "sqrt" ->
                {
                    if (x < 0.0)
                    {
                        throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "sqrt domain");
                    }
                    yield Math.sqrt(x);
                }
                case "abs" -> Math.abs(x);
                case "exp" -> Math.exp(x);
                case "floor" -> Math.floor(x);
                case "ceil" -> Math.ceil(x);
                case "round" -> (double) Math.round(x);
                default -> throw parserFehler(ParserFehler.UNBEKANNTE_FUNKTION, "Unknown function: " + funktion);
            };
        }

        if (!Double.isFinite(ergebnis))
        {
            throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "Invalid function result: " + funktion);
        }

        return ergebnis;
    }

    private static boolean istDirekteTrigonometrie(String funktion)
    {
        return "sin".equals(funktion) || "cos".equals(funktion) || "tan".equals(funktion);
    }

    private static double inverseTrigonometrie(double wert, WinkelModus winkelModus)
    {
        return winkelModus == WinkelModus.DEG ? Math.toDegrees(wert) : wert;
    }

    private static double popOperand(Deque<Double> stack)
    {
        if (stack.isEmpty())
        {
            throw parserFehler(ParserFehler.SYNTAX, "Missing operand");
        }
        return stack.pop();
    }

    private static AusdruckParserException parserFehler(ParserFehler fehler, String message)
    {
        return new AusdruckParserException(fehler, message);
    }
}
