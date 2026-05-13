package common.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class AusdruckTokenizer
{
    static final String UNARY_MINUS = "u-";
    static final String OPEN = "(";
    static final String CLOSE = ")";
    static final String MUL = "*";

    private static final Set<String> OPERATORS = Set.of("+", "-", "*", "/", "%", "^", UNARY_MINUS);

    static final Set<String> FUNCTIONS = Set.of(
            "sin", "cos", "tan",
            "asin", "acos", "atan",
            "sinh", "cosh", "tanh",
            "ln", "log", "sqrt", "abs", "exp",
            "floor", "ceil", "round",
            "rand"
    );

    private AusdruckTokenizer()
    {
    }

    public static List<AusdruckToken> tokenisiere(String expr)
    {
        if (expr == null)
        {
            throw parserFehler(ParserFehler.SYNTAX, "expr is null");
        }

        return tokenize(ensureTrailingZero(normalize(expr)));
    }

    static boolean isFunction(String text)
    {
        return FUNCTIONS.contains(text);
    }

    private static String normalize(String expr)
    {
        return expr
                .replace('\u00d7', '*')
                .replace('\u00f7', '/')
                .replace('\u2212', '-')
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replaceAll("\\s+", "");
    }

    private static String ensureTrailingZero(String expr)
    {
        if (expr.isEmpty()) return expr;
        char last = expr.charAt(expr.length() - 1);
        if (last == ',' || last == '.') return expr + "0";
        return expr;
    }

    private static List<AusdruckToken> tokenize(String expr)
    {
        List<AusdruckToken> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        StringBuilder ident = new StringBuilder();
        String prev = null;

        for (int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);

            if (isIdentifierChar(c))
            {
                String flushedNumber = flush(number, tokens);
                if (flushedNumber != null) prev = flushedNumber;

                if (isValue(prev)) tokens.add(new AusdruckToken(MUL));

                ident.append(c);
                while (i + 1 < expr.length() && isIdentifierChar(expr.charAt(i + 1)))
                {
                    ident.append(expr.charAt(++i));
                }

                String id = ident.toString().toLowerCase(Locale.ROOT).replace("\u03c0", "pi");
                tokens.add(new AusdruckToken(id));
                ident.setLength(0);
                prev = id;
                continue;
            }

            boolean unaryNumber =
                    c == '-' && number.isEmpty() &&
                            (prev == null || isOperator(prev) || OPEN.equals(prev)) &&
                            i + 1 < expr.length() &&
                            (Character.isDigit(expr.charAt(i + 1)) || expr.charAt(i + 1) == ',');

            if (Character.isDigit(c) || c == ',' || c == '.' || unaryNumber)
            {
                String flushedIdent = flush(ident, tokens);
                if (flushedIdent != null) prev = flushedIdent;

                if (number.isEmpty() && isValue(prev)) tokens.add(new AusdruckToken(MUL));

                number.append(c);
                while (i + 1 < expr.length() && istTeilVonZahl(expr, i + 1, number))
                {
                    number.append(expr.charAt(++i));
                }

                prev = null;
                continue;
            }

            String flushedNumber = flush(number, tokens);
            if (flushedNumber != null) prev = flushedNumber;

            String flushedIdent = flush(ident, tokens);
            if (flushedIdent != null) prev = flushedIdent;

            String t = String.valueOf(c);

            if (OPEN.equals(t) && isValue(prev) && !isFunction(prev)) tokens.add(new AusdruckToken(MUL));

            if ("-".equals(t) &&
                    (prev == null || isOperator(prev) || OPEN.equals(prev)) &&
                    i + 1 < expr.length() &&
                    !Character.isDigit(expr.charAt(i + 1)))
            {
                t = UNARY_MINUS;
            }

            if (isOperator(t) || OPEN.equals(t) || CLOSE.equals(t))
            {
                tokens.add(new AusdruckToken(t));
                prev = t;
            } else
            {
                throw parserFehler(ParserFehler.SYNTAX, "Unknown token: " + t);
            }
        }

        flush(number, tokens);
        flush(ident, tokens);
        return tokens;
    }

    private static boolean isOperator(String text)
    {
        return OPERATORS.contains(text);
    }

    private static boolean isIdentifier(String text)
    {
        return text != null && text.matches("[a-zA-Z]+");
    }

    private static boolean isIdentifierChar(char c)
    {
        return Character.isLetter(c) || c == '\u03c0';
    }

    private static boolean isValue(String text)
    {
        return text != null && (isNumber(text) || isIdentifier(text) || CLOSE.equals(text));
    }

    private static boolean isNumber(String text)
    {
        return text != null && text.matches("-?(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)(?:[eE][+-]?[0-9]+)?");
    }

    private static boolean istTeilVonZahl(String expr, int index, StringBuilder number)
    {
        char c = expr.charAt(index);

        if (Character.isDigit(c) || c == ',' || c == '.')
        {
            return true;
        }

        String bisher = number.toString();

        if ((c == 'e' || c == 'E') && !bisher.contains("e") && !bisher.contains("E"))
        {
            int exponentStart = index + 1;
            if (exponentStart < expr.length() && (expr.charAt(exponentStart) == '+' || expr.charAt(exponentStart) == '-'))
            {
                exponentStart++;
            }

            return exponentStart < expr.length() && Character.isDigit(expr.charAt(exponentStart));
        }

        return (c == '+' || c == '-')
                && !bisher.isEmpty()
                && (bisher.endsWith("e") || bisher.endsWith("E"));
    }

    private static String flush(StringBuilder sb, List<AusdruckToken> out)
    {
        if (sb.isEmpty()) return null;

        String token = sb.toString();
        out.add(new AusdruckToken(token));
        sb.setLength(0);
        return token;
    }

    private static AusdruckParserException parserFehler(ParserFehler fehler, String message)
    {
        return new AusdruckParserException(fehler, message);
    }
}
