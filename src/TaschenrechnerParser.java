import java.util.*;

public class TaschenrechnerParser
{

    public enum WinkelModus
    {DEG, RAD}

    private static final Map<String, Integer> PRIORITY = new HashMap<>();

    static
    {
        PRIORITY.put("+", 1);
        PRIORITY.put("-", 1);
        PRIORITY.put("*", 2);
        PRIORITY.put("/", 2);
        PRIORITY.put("%", 2);
        PRIORITY.put("^", 3);
        PRIORITY.put("u-", 4);
    }

    private static boolean isRightAssociative(String op)
    {
        return "^".equals(op) || "u-".equals(op);
    }

    private static boolean isOperator(String t)
    {
        return PRIORITY.containsKey(t);
    }

    private static boolean isFunction(String t)
    {
        return switch (t)
        {
            case "sin", "cos", "tan", "ln", "log", "sqrt", "abs", "exp" -> true;
            default -> false;
        };
    }

    private static boolean isIdentifier(String t)
    {
        return t != null && t.matches("[a-zA-Z]+");
    }

    public static double auswerten(String expr, double ans, WinkelModus winkelModus)
    {
        if (expr == null) throw new IllegalArgumentException();

        expr = expr
                .replace('×', '*')
                .replace('÷', '/')
                .replace('−', '-')
                .replace('–', '-')
                .replace('—', '-')
                .replaceAll("\\s+", "");

        if (!expr.isEmpty())
        {
            char last = expr.charAt(expr.length() - 1);
            if (last == ',' || last == '.') expr += "0";
        }

        return evalPostfix(
                toPostfix(tokenize(expr)),
                ans,
                winkelModus
        );
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

            if (Character.isLetter(c) || c == 'π')
            {
                flush(number, tokens);
                if (isValue(prev)) tokens.add("*");

                ident.append(c);
                while (i + 1 < expr.length() &&
                        (Character.isLetter(expr.charAt(i + 1)) || expr.charAt(i + 1) == 'π'))
                {
                    ident.append(expr.charAt(++i));
                }

                String id = ident.toString().toLowerCase().replace("π", "pi");
                tokens.add(id);
                ident.setLength(0);
                prev = id;
                continue;
            }

            boolean unaryNumber =
                    c == '-' && number.isEmpty() &&
                            (prev == null || isOperator(prev) || "(".equals(prev)) &&
                            i + 1 < expr.length() &&
                            (Character.isDigit(expr.charAt(i + 1)) || expr.charAt(i + 1) == ',');

            if (Character.isDigit(c) || c == ',' || c == '.' || unaryNumber)
            {
                flush(ident, tokens);
                if (number.isEmpty() && isValue(prev)) tokens.add("*");
                number.append(c);
                prev = null;
                continue;
            }

            flush(number, tokens);
            flush(ident, tokens);

            String t = String.valueOf(c);

            if ("(".equals(t) && isValue(prev)) tokens.add("*");

            if ("-".equals(t) &&
                    (prev == null || isOperator(prev) || "(".equals(prev)) &&
                    i + 1 < expr.length() &&
                    !Character.isDigit(expr.charAt(i + 1)))
            {
                t = "u-";
            }

            if (isOperator(t) || "(".equals(t) || ")".equals(t))
            {
                tokens.add(t);
                prev = t;
            } else
            {
                throw new IllegalArgumentException();
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
            } else if (isIdentifier(t))
            {
                if (i + 1 < tokens.size() && "(".equals(tokens.get(i + 1)) && isFunction(t))
                {
                    stack.push(t);
                } else
                {
                    out.add(t);
                }
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
            } else if ("(".equals(t))
            {
                stack.push(t);
            } else if (")".equals(t))
            {
                while (!stack.isEmpty() && !"(".equals(stack.peek()))
                {
                    out.add(stack.pop());
                }
                if (stack.isEmpty()) throw new IllegalArgumentException();
                stack.pop();
                if (!stack.isEmpty() && isFunction(stack.peek()))
                {
                    out.add(stack.pop());
                }
            } else
            {
                throw new IllegalArgumentException();
            }
        }

        while (!stack.isEmpty())
        {
            String t = stack.pop();
            if ("(".equals(t)) throw new IllegalArgumentException();
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
            } else if (isIdentifier(t) && !isFunction(t))
            {
                stack.push(switch (t)
                {
                    case "pi" -> Math.PI;
                    case "e" -> Math.E;
                    case "ans" -> ans;
                    default -> throw new IllegalArgumentException();
                });
            } else if ("u-".equals(t))
            {
                stack.push(-stack.pop());
            } else if (isOperator(t))
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
                    default -> throw new IllegalArgumentException();
                });
            } else if (isFunction(t))
            {
                double x = stack.pop();
                double r = switch (t)
                {
                    case "sin" -> Math.sin(mode == WinkelModus.DEG ? Math.toRadians(x) : x);
                    case "cos" -> Math.cos(mode == WinkelModus.DEG ? Math.toRadians(x) : x);
                    case "tan" -> Math.tan(mode == WinkelModus.DEG ? Math.toRadians(x) : x);
                    case "ln" -> Math.log(x);
                    case "log" -> Math.log10(x);
                    case "sqrt" -> Math.sqrt(x);
                    case "abs" -> Math.abs(x);
                    case "exp" -> Math.exp(x);
                    default -> throw new IllegalArgumentException();
                };
                stack.push(r);
            } else
            {
                throw new IllegalArgumentException();
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException();
        return stack.pop();
    }

    private static boolean isValue(String t)
    {
        return t != null && (isNumber(t) || isIdentifier(t) || ")".equals(t));
    }

    private static boolean isNumber(String s)
    {
        return s != null && s.matches("-?[0-9]+([.,][0-9]+)?");
    }

    private static void flush(StringBuilder sb, List<String> out)
    {
        if (!sb.isEmpty())
        {
            out.add(sb.toString());
            sb.setLength(0);
        }
    }
}
