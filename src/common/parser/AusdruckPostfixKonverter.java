package common.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Shunting-Yard ohne Rekursion – deshalb auch bei tief verschachtelten Klammern kein StackOverflow.
 */
final class AusdruckPostfixKonverter
{
    private static final String OPEN = AusdruckTokenizer.OPEN;
    private static final String CLOSE = AusdruckTokenizer.CLOSE;
    private static final String SEMIKOLON = AusdruckTokenizer.SEMIKOLON;

    /** Eine offene Klammer; {@code funktion} ist null bei einer normalen Klammer. */
    private static final class Klammer
    {
        private final AusdruckToken funktion;
        private int argumente;

        private Klammer(AusdruckToken funktion, int argumente)
        {
            this.funktion = funktion;
            this.argumente = argumente;
        }
    }

    private AusdruckPostfixKonverter()
    {
    }

    static List<AusdruckToken> konvertiere(List<AusdruckToken> tokens)
    {
        List<AusdruckToken> out = new ArrayList<>();
        Deque<AusdruckToken> stack = new ArrayDeque<>();
        Deque<Klammer> klammern = new ArrayDeque<>();

        for (int i = 0; i < tokens.size(); i++)
        {
            AusdruckToken token = tokens.get(i);
            String text = token.text();
            String vorher = i > 0 ? tokens.get(i - 1).text() : null;

            if (istZahl(text))
            {
                out.add(token);
            }
            else if (istIdentifier(text))
            {
                boolean istFunktion = i + 1 < tokens.size()
                        && OPEN.equals(tokens.get(i + 1).text())
                        && FunktionsRegistry.istFunktion(text);

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
                // Ein Präfix-Operator hat keinen linken Operanden, darf also nichts vom Stack holen (2^-2).
                boolean praefix = OperatorRegistry.stelligkeit(text) == 1;
                while (!praefix && !stack.isEmpty() && istOperator(stack.peek().text()))
                {
                    String top = stack.peek().text();
                    boolean pop = OperatorRegistry.istRechtsassoziativ(text)
                            ? OperatorRegistry.prioritaet(top) > OperatorRegistry.prioritaet(text)
                            : OperatorRegistry.prioritaet(top) >= OperatorRegistry.prioritaet(text);

                    if (!pop) break;
                    out.add(stack.pop());
                }
                stack.push(token);
            }
            else if (OPEN.equals(text))
            {
                // Der Funktionsname direkt davor liegt schon oben auf dem Stack (siehe Identifier-Zweig).
                boolean funktionsKlammer = istIdentifier(vorher) && FunktionsRegistry.istFunktion(vorher);
                AusdruckToken funktion = funktionsKlammer ? stack.peek() : null;
                boolean leer = i + 1 < tokens.size() && CLOSE.equals(tokens.get(i + 1).text());
                klammern.push(new Klammer(funktion, leer ? 0 : 1));
                stack.push(token);
            }
            else if (SEMIKOLON.equals(text))
            {
                if (klammern.isEmpty() || klammern.peek().funktion == null)
                {
                    throw new AusdruckParserException(ParserFehler.SYNTAX,
                            "„;“ ist nur zwischen Funktionsargumenten erlaubt", token.position());
                }
                if (OPEN.equals(vorher) || SEMIKOLON.equals(vorher))
                {
                    throw new AusdruckParserException(ParserFehler.SYNTAX, "Hier fehlt ein Argument", token.position());
                }
                while (!OPEN.equals(stack.peek().text()))
                {
                    out.add(stack.pop());
                }
                klammern.peek().argumente++;
            }
            else if (CLOSE.equals(text))
            {
                if (SEMIKOLON.equals(vorher))
                {
                    throw new AusdruckParserException(ParserFehler.SYNTAX, "Hier fehlt ein Argument", token.position());
                }
                while (!stack.isEmpty() && !OPEN.equals(stack.peek().text()))
                {
                    out.add(stack.pop());
                }

                if (stack.isEmpty())
                {
                    throw new AusdruckParserException(ParserFehler.KLAMMERN_UNAUSGEGLICHEN,
                            "Zu dieser „)“ fehlt die öffnende Klammer", token.position());
                }

                stack.pop();
                Klammer klammer = klammern.pop();

                if (klammer.funktion != null)
                {
                    pruefeArgumentAnzahl(klammer);
                    out.add(stack.pop());
                }
            }
            else
            {
                throw new AusdruckParserException(ParserFehler.SYNTAX, "Unbekanntes Zeichen „" + text + "“", token.position());
            }
        }

        while (!stack.isEmpty())
        {
            AusdruckToken token = stack.pop();
            if (OPEN.equals(token.text()))
            {
                throw new AusdruckParserException(ParserFehler.KLAMMERN_UNAUSGEGLICHEN,
                        "Diese „(“ wird nicht geschlossen", token.position());
            }
            out.add(token);
        }

        return out;
    }

    private static void pruefeArgumentAnzahl(Klammer klammer)
    {
        String name = klammer.funktion.text();
        int erwartet = FunktionsRegistry.stelligkeit(name);
        if (klammer.argumente != erwartet)
        {
            String argumente = erwartet == 1 ? "1 Argument" : erwartet + " Argumente";
            throw new AusdruckParserException(ParserFehler.SYNTAX,
                    name + " erwartet " + argumente + ", gefunden: " + klammer.argumente, klammer.funktion.position());
        }
    }

    private static boolean istOperator(String text)
    {
        return OperatorRegistry.istOperator(text);
    }

    private static boolean istZahl(String text)
    {
        return AusdruckTokenizer.istZahl(text);
    }

    private static boolean istIdentifier(String text)
    {
        return AusdruckTokenizer.istName(text);
    }
}
