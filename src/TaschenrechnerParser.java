import java.util.*;

public class TaschenrechnerParser
{
    private static final Map<String, Integer> PRIORITY = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2
    );

    public static double evaluate(String expr)
    {
        List<String> postfix = toPostfix(tokenize(expr));
        return evalPostfix(postfix);
    }

    private static List<String> tokenize(String expr)
    {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char c : expr.toCharArray())
        {
            if (Character.isDigit(c) || c == ',' || c == '.')
            {
                number.append(c);
            }
            else
            {
                if (number.length() > 0)
                {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }

        if (number.length() > 0)
            tokens.add(number.toString());

        return tokens;
    }

    private static List<String> toPostfix(List<String> tokens)
    {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens)
        {
            if (isNumber(token))
            {
                output.add(token);
            }
            else if (PRIORITY.containsKey(token))
            {
                while (!stack.isEmpty() &&
                        PRIORITY.containsKey(stack.peek()) &&
                        PRIORITY.get(stack.peek()) >= PRIORITY.get(token))
                {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
            else if (token.equals("("))
            {
                stack.push(token);
            }
            else if (token.equals(")"))
            {
                while (!stack.peek().equals("("))
                    output.add(stack.pop());
                stack.pop();
            }
        }

        while (!stack.isEmpty())
            output.add(stack.pop());

        return output;
    }

    private static double evalPostfix(List<String> postfix)
    {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix)
        {
            if (isNumber(token))
            {
                stack.push(Double.parseDouble(token.replace(',', '.')));
            }
            else
            {
                double b = stack.pop();
                double a = stack.pop();

                switch (token)
                {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            }
        }

        return stack.pop();
    }

    private static boolean isNumber(String s)
    {
        return s.matches("[0-9.,]+");
    }
}