package modes.logik.logic;

import modes.logik.model.Ausdruck;
import modes.logik.model.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Rekursiver Abstieg. Vorrang (stark nach schwach): NICHT, UND, XOR, ODER, Implikation, Äquivalenz.
 * Die Implikation bindet rechtsassoziativ, alle anderen binären Operatoren linksassoziativ.
 */
public class LogikParser
{
    private enum Art
    {
        VARIABLE, KONSTANTE, NICHT, OPERATOR, AUF, ZU, ENDE
    }

    private record Token(Art art, String text, int stelle, Operator operator)
    {
    }

    private List<Token> tokens;
    private int index;

    public Ausdruck parse(String eingabe)
    {
        if (eingabe == null || eingabe.isBlank())
        {
            throw new IllegalArgumentException("Bitte einen Ausdruck eingeben.");
        }
        tokens = zerlege(eingabe);
        index = 0;
        Ausdruck ausdruck = aequivalenz();
        Token rest = aktuell();
        if (rest.art() != Art.ENDE)
        {
            throw unerwartet(rest);
        }
        return ausdruck;
    }

    private Ausdruck aequivalenz()
    {
        Ausdruck links = implikation();
        while (istOperator(Operator.AEQUIVALENZ))
        {
            index++;
            links = new Ausdruck.Verknuepfung(Operator.AEQUIVALENZ, links, implikation());
        }
        return links;
    }

    private Ausdruck implikation()
    {
        Ausdruck links = oder();
        if (istOperator(Operator.IMPLIKATION))
        {
            index++;
            return new Ausdruck.Verknuepfung(Operator.IMPLIKATION, links, implikation());
        }
        return links;
    }

    private Ausdruck oder()
    {
        Ausdruck links = xor();
        while (istOperator(Operator.ODER))
        {
            index++;
            links = new Ausdruck.Verknuepfung(Operator.ODER, links, xor());
        }
        return links;
    }

    private Ausdruck xor()
    {
        Ausdruck links = und();
        while (istOperator(Operator.XOR))
        {
            index++;
            links = new Ausdruck.Verknuepfung(Operator.XOR, links, und());
        }
        return links;
    }

    private Ausdruck und()
    {
        Ausdruck links = nicht();
        while (istOperator(Operator.UND))
        {
            index++;
            links = new Ausdruck.Verknuepfung(Operator.UND, links, nicht());
        }
        return links;
    }

    private Ausdruck nicht()
    {
        if (aktuell().art() == Art.NICHT)
        {
            index++;
            return new Ausdruck.Nicht(nicht());
        }
        return primaer();
    }

    private Ausdruck primaer()
    {
        Token token = aktuell();
        switch (token.art())
        {
            case VARIABLE ->
            {
                index++;
                return new Ausdruck.Variable(token.text().charAt(0));
            }
            case KONSTANTE ->
            {
                index++;
                return new Ausdruck.Konstante(token.text().equals("1"));
            }
            case AUF ->
            {
                index++;
                Ausdruck inneres = aequivalenz();
                if (aktuell().art() == Art.ENDE)
                {
                    throw new IllegalArgumentException("Schließende Klammer fehlt zur „(“ an Stelle " + token.stelle() + ".");
                }
                if (aktuell().art() != Art.ZU)
                {
                    throw unerwartet(aktuell());
                }
                index++;
                return inneres;
            }
            case ENDE -> throw new IllegalArgumentException("Der Ausdruck ist unvollständig – am Ende fehlt ein Operand.");
            default -> throw unerwartet(token);
        }
    }

    private boolean istOperator(Operator operator)
    {
        return aktuell().art() == Art.OPERATOR && aktuell().operator() == operator;
    }

    private Token aktuell()
    {
        return tokens.get(index);
    }

    private static IllegalArgumentException unerwartet(Token token)
    {
        return new IllegalArgumentException("Unerwartetes „" + token.text() + "“ an Stelle " + token.stelle() + ".");
    }

    private static List<Token> zerlege(String eingabe)
    {
        List<Token> liste = new ArrayList<>();
        int i = 0;
        while (i < eingabe.length())
        {
            char c = eingabe.charAt(i);
            int stelle = i + 1;
            if (Character.isWhitespace(c))
            {
                i++;
                continue;
            }
            if (Character.isLetter(c))
            {
                int ende = i;
                while (ende < eingabe.length() && Character.isLetter(eingabe.charAt(ende)))
                {
                    ende++;
                }
                liste.add(wortToken(eingabe.substring(i, ende), stelle));
                i = ende;
                continue;
            }

            String zweier = eingabe.substring(i, Math.min(i + 2, eingabe.length()));
            String dreier = eingabe.substring(i, Math.min(i + 3, eingabe.length()));
            if (dreier.equals("<->") || dreier.equals("<=>"))
            {
                liste.add(new Token(Art.OPERATOR, dreier, stelle, Operator.AEQUIVALENZ));
                i += 3;
            }
            else if (zweier.equals("->") || zweier.equals("=>"))
            {
                liste.add(new Token(Art.OPERATOR, zweier, stelle, Operator.IMPLIKATION));
                i += 2;
            }
            else if (zweier.equals("&&") || zweier.equals("||"))
            {
                liste.add(new Token(Art.OPERATOR, zweier, stelle, c == '&' ? Operator.UND : Operator.ODER));
                i += 2;
            }
            else
            {
                liste.add(zeichenToken(eingabe, i, stelle));
                i++;
            }
        }
        liste.add(new Token(Art.ENDE, "", eingabe.length() + 1, null));
        return liste;
    }

    private static Token zeichenToken(String eingabe, int i, int stelle)
    {
        char c = eingabe.charAt(i);
        String text = String.valueOf(c);
        return switch (c)
        {
            case '(' -> new Token(Art.AUF, text, stelle, null);
            case ')' -> new Token(Art.ZU, text, stelle, null);
            case '0', '1' -> new Token(Art.KONSTANTE, text, stelle, null);
            case '¬', '!', '~' -> new Token(Art.NICHT, text, stelle, null);
            case '∧', '&' -> new Token(Art.OPERATOR, text, stelle, Operator.UND);
            case '∨', '|' -> new Token(Art.OPERATOR, text, stelle, Operator.ODER);
            case '⊕', '^' -> new Token(Art.OPERATOR, text, stelle, Operator.XOR);
            case '→' -> new Token(Art.OPERATOR, text, stelle, Operator.IMPLIKATION);
            case '↔' -> new Token(Art.OPERATOR, text, stelle, Operator.AEQUIVALENZ);
            default -> throw new IllegalArgumentException("Unerwartetes Zeichen „"
                    + new String(Character.toChars(eingabe.codePointAt(i))) + "“ an Stelle " + stelle + ".");
        };
    }

    private static Token wortToken(String wort, int stelle)
    {
        String gross = wort.toUpperCase(Locale.ROOT);
        if (gross.equals("NOT"))
        {
            return new Token(Art.NICHT, wort, stelle, null);
        }
        Operator operator = switch (gross)
        {
            case "AND" -> Operator.UND;
            case "OR" -> Operator.ODER;
            case "XOR" -> Operator.XOR;
            default -> null;
        };
        if (operator != null)
        {
            return new Token(Art.OPERATOR, wort, stelle, operator);
        }

        char erstes = wort.charAt(0);
        if (wort.length() == 1 && erstes >= 'A' && erstes <= 'Z')
        {
            return new Token(Art.VARIABLE, wort, stelle, null);
        }
        if (wort.length() == 1 && erstes >= 'a' && erstes <= 'z')
        {
            throw new IllegalArgumentException("Variablen bitte groß schreiben: „" + wort + "“ an Stelle " + stelle + ".");
        }
        throw new IllegalArgumentException("Unbekanntes Wort „" + wort + "“ an Stelle " + stelle
                + " – Variablen sind einzelne Großbuchstaben A–Z.");
    }
}
