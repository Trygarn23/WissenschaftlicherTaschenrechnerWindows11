import java.util.*;

public final class TaschenrechnerParser
{
    public enum WinkelModus { DEG, RAD }

    private static final String UNARY_MINUS = "u-";
    private static final String OPEN = "(";
    private static final String CLOSE = ")";
    private static final String MUL = "*";

    private static final Map<String, Integer> PRIORITY = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2,
            "%", 2,
            "^", 3,
            UNARY_MINUS, 4
    );

    private static final Set<String> FUNCTIONS = Set.of(
            "sin", "cos", "tan", "ln", "log", "sqrt", "abs", "exp"
    );

    private TaschenrechnerParser() {}

    public static double auswerten(String expr, double ans, WinkelModus winkelModus)
    {
        if (expr == null) throw new IllegalArgumentException("expr is null");

        String normalized = normalize(expr);
        normalized = ensureTrailingZero(normalized);

        List<String> tokens = tokenize(normalized);
        List<String> postfix = toPostfix(tokens);
        return evalPostfix(postfix, ans, winkelModus);
    }

    private static String normalize(String expr)
    {
        return expr
                .replace('×', '*')
                .replace('÷', '/')
                .replace('−', '-')
                .replace('–', '-')
                .replace('—', '-')
                .replaceAll("\\s+", "");
    }

    private static String ensureTrailingZero(String expr)
    {
        if (expr.isEmpty()) return expr;
        char last = expr.charAt(expr.length() - 1);
        if (last == ',' || last == '.') return expr + "0";
        return expr;
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
        return FUNCTIONS.contains(t);
    }

    private static boolean isIdentifier(String t)
    {
        return t != null && t.matches("[a-zA-Z]+");
    }

    private static boolean isIdentifierChar(char c)
    {
        return Character.isLetter(c) || c == 'π';
    }

    private static List<String> tokenize(String expr)
    {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        StringBuilder ident = new StringBuilder();
        String prev = null;

        for (int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);

            if (isIdentifierChar(c))
            {
                flush(number, tokens);
                if (isValue(prev)) tokens.add(MUL);

                ident.append(c);
                while (i + 1 < expr.length() && isIdentifierChar(expr.charAt(i + 1)))
                {
                    ident.append(expr.charAt(++i));
                }

                String id = ident.toString().toLowerCase(Locale.ROOT).replace("π", "pi");
                tokens.add(id);
                ident.setLength(0);
                prev = id;
                continue;
            }

            boolean unaryNumber =
                    c == '-' && number.length() == 0 &&
                            (prev == null || isOperator(prev) || OPEN.equals(prev)) &&
                            i + 1 < expr.length() &&
                            (Character.isDigit(expr.charAt(i + 1)) || expr.charAt(i + 1) == ',');

            if (Character.isDigit(c) || c == ',' || c == '.' || unaryNumber)
            {
                flush(ident, tokens);
                if (number.length() == 0 && isValue(prev)) tokens.add(MUL);

                number.append(c);
                prev = null;
                continue;
            }

            flush(number, tokens);
            flush(ident, tokens);

            String t = String.valueOf(c);

            if (OPEN.equals(t) && isValue(prev)) tokens.add(MUL);

            if ("-".equals(t) &&
                    (prev == null || isOperator(prev) || OPEN.equals(prev)) &&
                    i + 1 < expr.length() &&
                    !Character.isDigit(expr.charAt(i + 1)))
            {
                t = UNARY_MINUS;
            }

            if (isOperator(t) || OPEN.equals(t) || CLOSE.equals(t))
            {
                tokens.add(t);
                prev = t;
            }
            else
            {
                throw new IllegalArgumentException("Unknown token: " + t);
            }
        }

        flush(number, tokens);
        flush(ident, tokens);
        return tokens;
    }

    private static List<String> toPostfix(List<String> tokens)
    {
        List<String> out = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (int i = 0; i < tokens.size(); i++)
        {
            String t = tokens.get(i);

            if (isNumber(t))
            {
                out.add(t);
            }
            else if (isIdentifier(t))
            {
                boolean isFunc = i + 1 < tokens.size() && OPEN.equals(tokens.get(i + 1)) && isFunction(t);
                if (isFunc) stack.push(t);
                else out.add(t);
            }
            else if (isOperator(t))
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
            }
            else if (OPEN.equals(t))
            {
                stack.push(t);
            }
            else if (CLOSE.equals(t))
            {
                while (!stack.isEmpty() && !OPEN.equals(stack.peek()))
                {
                    out.add(stack.pop());
                }

                if (stack.isEmpty()) throw new IllegalArgumentException("Unbalanced parentheses");
                stack.pop();

                if (!stack.isEmpty() && isFunction(stack.peek()))
                {
                    out.add(stack.pop());
                }
            }
            else
            {
                throw new IllegalArgumentException("Unknown token: " + t);
            }
        }

        while (!stack.isEmpty())
        {
            String t = stack.pop();
            if (OPEN.equals(t)) throw new IllegalArgumentException("Unbalanced parentheses");
            out.add(t);
        }

        return out;
    }

    private static double evalPostfix(List<String> postfix, double ans, WinkelModus mode)
    {
        Deque<Double> stack = new ArrayDeque<>();

        for (String t : postfix)
        {
            if (isNumber(t))
            {
                stack.push(Double.parseDouble(t.replace(',', '.')));
            }
            else if (isIdentifier(t) && !isFunction(t))
            {
                stack.push(switch (t)
                {
                    case "pi" -> Math.PI;
                    case "e" -> Math.E;
                    case "ans" -> ans;
                    default -> throw new IllegalArgumentException("Unknown identifier: " + t);
                });
            }
            else if (UNARY_MINUS.equals(t))
            {
                stack.push(-stack.pop());
            }
            else if (isOperator(t))
            {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(switch (t)
                {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> a / b;
                    case "%" -> a % b;
                    case "^" -> Math.pow(a, b);
                    default -> throw new IllegalArgumentException("Unknown operator: " + t);
                });
            }
            else if (isFunction(t))
            {
                double x = stack.pop();

                double trigArg = x;
                if (mode == WinkelModus.DEG && ("sin".equals(t) || "cos".equals(t) || "tan".equals(t)))
                {
                    trigArg = Math.toRadians(x);
                }

                double r = switch (t)
                {
                    case "sin" -> Math.sin(trigArg);
                    case "cos" -> Math.cos(trigArg);
                    case "tan" -> Math.tan(trigArg);
                    case "ln" -> Math.log(x);
                    case "log" -> Math.log10(x);
                    case "sqrt" -> Math.sqrt(x);
                    case "abs" -> Math.abs(x);
                    case "exp" -> Math.exp(x);
                    default -> throw new IllegalArgumentException("Unknown function: " + t);
                };

                stack.push(r);
            }
            else
            {
                throw new IllegalArgumentException("Unknown token: " + t);
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Invalid expression");
        return stack.pop();
    }

    private static boolean isValue(String t)
    {
        return t != null && (isNumber(t) || isIdentifier(t) || CLOSE.equals(t));
    }

    private static boolean isNumber(String s)
    {
        return s != null && s.matches("-?[0-9]+([.,][0-9]+)?");
    }

    private static void flush(StringBuilder sb, List<String> out)
    {
        if (sb.length() == 0) return;
        out.add(sb.toString());
        sb.setLength(0);
    }
}
