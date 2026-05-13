package common.parser;

import common.state.WinkelModus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public final class AusdruckParser
{
    private static final String UNARY_MINUS = AusdruckTokenizer.UNARY_MINUS;
    private static final String OPEN = AusdruckTokenizer.OPEN;
    private static final String CLOSE = AusdruckTokenizer.CLOSE;

    private static final Map<String, Integer> PRIORITY = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2,
            "%", 2,
            "^", 3,
            UNARY_MINUS, 4
    );

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

        List<String> tokens = AusdruckTokenizer.tokenisiere(expr).stream()
                .map(AusdruckToken::text)
                .toList();
        List<String> postfix = toPostfix(tokens);
        return evalPostfix(postfix, ans, winkelModus, variablen == null ? Map.of() : variablen);
    }

    private static boolean isRightAssociative(String op)
    {
        return "^".equals(op) || UNARY_MINUS.equals(op);
    }

    private static boolean isOperator(String t)
    {
        return PRIORITY.containsKey(t);
    }

    private static boolean isFunction(String t)
    {
        return AusdruckTokenizer.isFunction(t);
    }

    private static boolean isIdentifier(String t)
    {
        return t != null && t.matches("[a-zA-Z]+");
    }

    private static List<String> toPostfix(List<String> tokens)
    {
        List<String> out = new java.util.ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (int i = 0; i < tokens.size(); i++)
        {
            String t = tokens.get(i);

            if (isNumber(t))
            {
                out.add(t);
            } else if (isIdentifier(t))
            {
                boolean isFunc = i + 1 < tokens.size() && OPEN.equals(tokens.get(i + 1)) && isFunction(t);
                if (isFunc) stack.push(t);
                else out.add(t);
            } else if (isOperator(t))
            {
                while (!stack.isEmpty() && isOperator(stack.peek()))
                {
                    String top = stack.peek();
                    boolean pop = isRightAssociative(t)
                            ? PRIORITY.get(top) > PRIORITY.get(t)
                            : PRIORITY.get(top) >= PRIORITY.get(t);

                    if (!pop) break;
                    out.add(stack.pop());
                }
                stack.push(t);
            } else if (OPEN.equals(t))
            {
                stack.push(t);
            } else if (CLOSE.equals(t))
            {
                while (!stack.isEmpty() && !OPEN.equals(stack.peek()))
                {
                    out.add(stack.pop());
                }

                if (stack.isEmpty()) throw parserFehler(ParserFehler.KLAMMERN_UNAUSGEGLICHEN, "Unbalanced parentheses");
                stack.pop();

                if (!stack.isEmpty() && isFunction(stack.peek()))
                {
                    out.add(stack.pop());
                }
            } else
            {
                throw parserFehler(ParserFehler.SYNTAX, "Unknown token: " + t);
            }
        }

        while (!stack.isEmpty())
        {
            String t = stack.pop();
            if (OPEN.equals(t)) throw parserFehler(ParserFehler.KLAMMERN_UNAUSGEGLICHEN, "Unbalanced parentheses");
            out.add(t);
        }

        return out;
    }

    private static double evalPostfix(List<String> postfix, double ans, WinkelModus mode, Map<String, Double> variablen)
    {
        Deque<Double> stack = new ArrayDeque<>();

        for (String t : postfix)
        {
            if (isNumber(t))
            {
                stack.push(Double.parseDouble(t.replace(',', '.')));
            } else if (isIdentifier(t) && !isFunction(t))
            {
                stack.push(switch (t)
                {
                    case "pi" -> Math.PI;
                    case "e" -> Math.E;
                    case "ans" -> ans;
                    default ->
                    {
                        if (variablen.containsKey(t))
                        {
                            yield variablen.get(t);
                        }
                        throw parserFehler(ParserFehler.UNBEKANNTE_FUNKTION, "Unknown identifier: " + t);
                    }
                });
            } else if (UNARY_MINUS.equals(t))
            {
                stack.push(-popOperand(stack));
            } else if (isOperator(t))
            {
                double b = popOperand(stack);
                double a = popOperand(stack);
                stack.push(switch (t)
                {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" ->
                    {
                        if (b == 0.0) throw parserFehler(ParserFehler.DIVISION_DURCH_NULL, "Division by zero");
                        yield a / b;
                    }
                    case "%" ->
                    {
                        if (b == 0.0) throw parserFehler(ParserFehler.DIVISION_DURCH_NULL, "Modulo by zero");
                        yield a % b;
                    }
                    case "^" -> Math.pow(a, b);
                    default -> throw parserFehler(ParserFehler.SYNTAX, "Unknown operator: " + t);
                });
            }
            else if (isFunction(t))
            {
                double r;

                if ("rand".equals(t))
                {
                    r = Math.random();
                }
                else
                {
                    double x = popOperand(stack);

                    double trigArg = x;
                    if (mode == WinkelModus.DEG && ("sin".equals(t) || "cos".equals(t) || "tan".equals(t)))
                    {
                        trigArg = Math.toRadians(x);
                    }

                    r = switch (t)
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

                        case "asin" ->
                        {
                            double wert = Math.asin(x);
                            yield mode == WinkelModus.DEG ? Math.toDegrees(wert) : wert;
                        }
                        case "acos" ->
                        {
                            double wert = Math.acos(x);
                            yield mode == WinkelModus.DEG ? Math.toDegrees(wert) : wert;
                        }
                        case "atan" ->
                        {
                            double wert = Math.atan(x);
                            yield mode == WinkelModus.DEG ? Math.toDegrees(wert) : wert;
                        }

                        case "sinh" -> Math.sinh(x);
                        case "cosh" -> Math.cosh(x);
                        case "tanh" -> Math.tanh(x);

                        case "ln" ->
                        {
                            if (x <= 0.0) throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "ln domain");
                            yield Math.log(x);
                        }
                        case "log" ->
                        {
                            if (x <= 0.0) throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "log domain");
                            yield Math.log10(x);
                        }
                        case "sqrt" ->
                        {
                            if (x < 0.0) throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "sqrt domain");
                            yield Math.sqrt(x);
                        }
                        case "abs" -> Math.abs(x);
                        case "exp" -> Math.exp(x);

                        case "floor" -> Math.floor(x);
                        case "ceil" -> Math.ceil(x);
                        case "round" -> (double) Math.round(x);

                        default -> throw parserFehler(ParserFehler.UNBEKANNTE_FUNKTION, "Unknown function: " + t);
                    };
                }

                if (!Double.isFinite(r))
                {
                    throw parserFehler(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, "Invalid function result: " + t);
                }

                stack.push(r);
            }else
            {
                throw parserFehler(ParserFehler.SYNTAX, "Unknown token: " + t);
            }
        }

        if (stack.size() != 1) throw parserFehler(ParserFehler.SYNTAX, "Invalid expression");
        return stack.pop();
    }

    private static double popOperand(Deque<Double> stack)
    {
        if (stack.isEmpty()) throw parserFehler(ParserFehler.SYNTAX, "Missing operand");
        return stack.pop();
    }

    private static AusdruckParserException parserFehler(ParserFehler fehler, String message)
    {
        return new AusdruckParserException(fehler, message);
    }

    private static boolean isNumber(String s)
    {
        return s != null && s.matches("-?(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)(?:[eE][+-]?[0-9]+)?");
    }
}
