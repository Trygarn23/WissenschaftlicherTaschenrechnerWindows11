package common.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class AusdruckTokenizer
{
    static final String OPEN = "(";
    static final String CLOSE = ")";
    static final String MUL = "*";
    /** Trennt Funktionsargumente, z. B. nCr(5;2) – das Komma ist schon Dezimaltrennzeichen. */
    static final String SEMIKOLON = ";";

    private static final Pattern ZAHL = Pattern.compile("-?(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)(?:[eE][+-]?[0-9]+)?");
    private static final Pattern NAME = Pattern.compile("[a-zA-Z]+");

    private AusdruckTokenizer()
    {
    }

    public static List<AusdruckToken> tokenisiere(String expr)
    {
        if (expr == null)
        {
            throw new AusdruckParserException(ParserFehler.SYNTAX, "Kein Ausdruck vorhanden.");
        }

        // Normalisieren, dabei für jedes Zeichen die Stelle im Original merken.
        StringBuilder text = new StringBuilder(expr.length() + 1);
        int[] original = new int[expr.length() + 1];
        for (int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) continue;
            original[text.length()] = i;
            text.append(normalisiere(c));
        }

        if (!text.isEmpty())
        {
            char letztes = text.charAt(text.length() - 1);
            if (letztes == ',' || letztes == '.')
            {
                original[text.length()] = expr.length();
                text.append('0');
            }
        }

        return tokenize(text.toString(), original);
    }

    static boolean istZahl(String text)
    {
        return text != null && ZAHL.matcher(text).matches();
    }

    static boolean istName(String text)
    {
        return text != null && NAME.matcher(text).matches();
    }

    private static char normalisiere(char c)
    {
        return switch (c)
        {
            case '×' -> '*';
            case '÷' -> '/';
            case '−', '–', '—' -> '-';
            default -> c;
        };
    }

    private static List<AusdruckToken> tokenize(String expr, int[] original)
    {
        List<AusdruckToken> tokens = new ArrayList<>();
        String prev = null;

        for (int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);
            int start = i;

            if (isIdentifierChar(c))
            {
                if (isValue(prev)) tokens.add(new AusdruckToken(MUL, original[start]));

                while (i + 1 < expr.length() && isIdentifierChar(expr.charAt(i + 1)))
                {
                    i++;
                }

                String id = expr.substring(start, i + 1).toLowerCase(Locale.ROOT).replace("π", "pi");
                tokens.add(new AusdruckToken(id, original[start]));
                prev = id;
                continue;
            }

            boolean unaryNumber = c == '-' && erwartetWert(prev)
                    && i + 1 < expr.length()
                    && (Character.isDigit(expr.charAt(i + 1)) || expr.charAt(i + 1) == ',');

            if (Character.isDigit(c) || c == ',' || c == '.' || unaryNumber)
            {
                if (isValue(prev)) tokens.add(new AusdruckToken(MUL, original[start]));

                while (i + 1 < expr.length() && istTeilVonZahl(expr, start, i + 1))
                {
                    i++;
                }

                String zahl = expr.substring(start, i + 1);
                if (c == '-' && i + 1 < expr.length() && expr.charAt(i + 1) == '^')
                {
                    // -2^2 = -(2^2): Vorzeichen als unäres Minus, damit ^ stärker bindet.
                    tokens.add(new AusdruckToken(OperatorRegistry.UNAERES_MINUS, original[start]));
                    zahl = zahl.substring(1);
                    start++;
                }
                tokens.add(new AusdruckToken(zahl, original[start]));
                prev = zahl;
                continue;
            }

            String t = String.valueOf(c);

            if (OPEN.equals(t) && isValue(prev) && !FunktionsRegistry.istFunktion(prev))
            {
                tokens.add(new AusdruckToken(MUL, original[start]));
            }

            if ("-".equals(t) && erwartetWert(prev) && i + 1 < expr.length() && !Character.isDigit(expr.charAt(i + 1)))
            {
                t = OperatorRegistry.UNAERES_MINUS;
            }

            if (OperatorRegistry.istOperator(t) || OPEN.equals(t) || CLOSE.equals(t) || SEMIKOLON.equals(t))
            {
                tokens.add(new AusdruckToken(t, original[start]));
                prev = t;
            }
            else
            {
                throw new AusdruckParserException(ParserFehler.SYNTAX, "Unerwartetes Zeichen „" + t + "“", original[start]);
            }
        }

        return tokens;
    }

    private static boolean erwartetWert(String prev)
    {
        return prev == null || OperatorRegistry.istOperator(prev) || OPEN.equals(prev) || SEMIKOLON.equals(prev);
    }

    private static boolean isIdentifierChar(char c)
    {
        return Character.isLetter(c) || c == 'π';
    }

    private static boolean isValue(String text)
    {
        return istZahl(text) || istName(text) || CLOSE.equals(text);
    }

    private static boolean istTeilVonZahl(String expr, int start, int index)
    {
        char c = expr.charAt(index);

        if (Character.isDigit(c) || c == ',' || c == '.')
        {
            return true;
        }

        String bisher = expr.substring(start, index);

        if ((c == 'e' || c == 'E') && !bisher.contains("e") && !bisher.contains("E"))
        {
            int exponentStart = index + 1;
            if (exponentStart < expr.length() && (expr.charAt(exponentStart) == '+' || expr.charAt(exponentStart) == '-'))
            {
                exponentStart++;
            }

            return exponentStart < expr.length() && Character.isDigit(expr.charAt(exponentStart));
        }

        return (c == '+' || c == '-') && (bisher.endsWith("e") || bisher.endsWith("E"));
    }
}
