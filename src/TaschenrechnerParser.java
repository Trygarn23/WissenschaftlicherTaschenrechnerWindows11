import java.util.*;

public class TaschenrechnerParser
{
    private static final Map<String, Integer> PRIORITY = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2,
            "%", 2,
            "^", 3
    );

    private static boolean isRightAssociative(String op)
    {
        return "^".equals(op);
    }

    public static double auswerten(String expr)
    {
        if (expr == null)
        {
            throw new IllegalArgumentException("expr is null");
        }

        // Normalisieren: UI-Zeichen -> Parser-Zeichen
        expr = expr
                .replace('×', '*')
                .replace('÷', '/')
                .replace('−', '-') // Unicode minus
                .replace('–', '-') // En dash
                .replace('—', '-'); // Em dash

        // Whitespace entfernen
        expr = expr.replaceAll("\\s+", "");

        // Wenn der User mit Komma/Punkt endet: als ,0 interpretieren
        if (!expr.isEmpty())
        {
            char last = expr.charAt(expr.length() - 1);
            if (last == ',' || last == '.')
            {
                expr = expr + "0";
            }
        }

        List<String> postfix = toPostfix(tokenize(expr));
        return evalPostfix(postfix);
    }

    private static List<String> tokenize(String expr)
    {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        String prevToken = null;

        for (int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);

            boolean unaryMinus =
                    c == '-' && number.length() == 0 &&
                            (prevToken == null || PRIORITY.containsKey(prevToken) || "(".equals(prevToken));

            if (Character.isDigit(c) || c == ',' || c == '.' || unaryMinus)
            {
                number.append(c);
            } else
            {
                if (number.length() > 0)
                {
                    tokens.add(number.toString());
                    prevToken = number.toString();
                    number.setLength(0);
                }
                String tok = String.valueOf(c);
                tokens.add(tok);
                prevToken = tok;
            }
        }

        if (number.length() > 0)
        {
            tokens.add(number.toString());
        }

        return tokens;
    }

    private static List<String> toPostfix(List<String> tokens)
    {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token : tokens)
        {
            if (isNumber(token))
            {
                output.add(token);
            } else if (PRIORITY.containsKey(token))
            {
                int p1 = PRIORITY.get(token);
                boolean rightAssoc = isRightAssociative(token);

                while (!stack.isEmpty() && PRIORITY.containsKey(stack.peek()))
                {
                    String top = stack.peek();
                    int p2 = PRIORITY.get(top);

                    // ^ ist rechtsassoziativ: bei gleicher Priorität NICHT poppen
                    boolean shouldPop = rightAssoc ? (p2 > p1) : (p2 >= p1);
                    if (!shouldPop) break;

                    output.add(stack.pop());
                }

                stack.push(token);
            } else if (token.equals("("))
            {
                stack.push(token);
            } else if (token.equals(")"))
            {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                {
                    output.add(stack.pop());
                }

                if (stack.isEmpty())
                {
                    throw new IllegalArgumentException("Unbalanced parentheses");
                }

                stack.pop(); // '(' entfernen
            } else
            {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }

        while (!stack.isEmpty())
        {
            String top = stack.pop();
            if (top.equals("("))
            {
                throw new IllegalArgumentException("Unbalanced parentheses");
            }
            output.add(top);
        }

        return output;
    }

    private static double evalPostfix(List<String> postfix)
    {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : postfix)
        {
            if (isNumber(token))
            {
                stack.push(Double.parseDouble(token.replace(',', '.')));
            } else
            {
                if (stack.size() < 2)
                {
                    throw new IllegalArgumentException("Invalid expression");
                }

                double b = stack.pop();
                double a = stack.pop();

                switch (token)
                {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        stack.push(a / b);
                        break;
                    case "%":
                        stack.push(a % b);
                        break;
                    case "^":
                        stack.push(Math.pow(a, b));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operator: " + token);
                }
            }
        }

        if (stack.size() != 1)
        {
            throw new IllegalArgumentException("Invalid expression");
        }

        return stack.pop();
    }

    private static boolean isNumber(String s)
    {
        return s.matches("-?[0-9]+([.,][0-9]+)?");
    }
}
