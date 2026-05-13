package common.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

final class AusdruckPostfixKonverter
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

    private AusdruckPostfixKonverter()
    {
    }

    static List<AusdruckToken> konvertiere(List<AusdruckToken> tokens)
    {
        List<AusdruckToken> out = new ArrayList<>();
        Deque<AusdruckToken> stack = new ArrayDeque<>();

        for (int i = 0; i < tokens.size(); i++)
        {
            AusdruckToken token = tokens.get(i);
            String text = token.text();

            if (istZahl(text))
            {
                out.add(token);
            }
            else if (istIdentifier(text))
            {
                boolean istFunktion = i + 1 < tokens.size()
                        && OPEN.equals(tokens.get(i + 1).text())
                        && AusdruckTokenizer.isFunction(text);

                if (istFunktion)
                {
                    stack.push(token);
                }
                else
                {
                    out.add(token);
                }
            }
            else if (istOperator(text))
            {
                while (!stack.isEmpty() && istOperator(stack.peek().text()))
                {
                    String top = stack.peek().text();
                    boolean pop = istRechtsassoziativ(text)
                            ? PRIORITY.get(top) > PRIORITY.get(text)
                            : PRIORITY.get(top) >= PRIORITY.get(text);

                    if (!pop) break;
                    out.add(stack.pop());
                }
                stack.push(token);
            }
            else if (OPEN.equals(text))
            {
                stack.push(token);
            }
            else if (CLOSE.equals(text))
            {
                while (!stack.isEmpty() && !OPEN.equals(stack.peek().text()))
                {
                    out.add(stack.pop());
                }

                if (stack.isEmpty())
                {
                    throw parserFehler(ParserFehler.KLAMMERN_UNAUSGEGLICHEN, "Unbalanced parentheses");
                }

                stack.pop();

                if (!stack.isEmpty() && AusdruckTokenizer.isFunction(stack.peek().text()))
                {
                    out.add(stack.pop());
                }
            }
            else
            {
                throw parserFehler(ParserFehler.SYNTAX, "Unknown token: " + text);
            }
        }

        while (!stack.isEmpty())
        {
            AusdruckToken token = stack.pop();
            if (OPEN.equals(token.text()))
            {
                throw parserFehler(ParserFehler.KLAMMERN_UNAUSGEGLICHEN, "Unbalanced parentheses");
            }
            out.add(token);
        }

        return out;
    }

    static boolean istOperator(String text)
    {
        return PRIORITY.containsKey(text);
    }

    static boolean istZahl(String text)
    {
        return text != null && text.matches("-?(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)(?:[eE][+-]?[0-9]+)?");
    }

    static boolean istIdentifier(String text)
    {
        return text != null && text.matches("[a-zA-Z]+");
    }

    private static boolean istRechtsassoziativ(String operator)
    {
        return "^".equals(operator) || UNARY_MINUS.equals(operator);
    }

    private static AusdruckParserException parserFehler(ParserFehler fehler, String message)
    {
        return new AusdruckParserException(fehler, message);
    }
}
