package common.parser;

import common.state.WinkelModus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

final class AusdruckPostfixAuswerter
{
    private AusdruckPostfixAuswerter()
    {
    }

    static double werteAus(
            List<AusdruckToken> postfix,
            double ans,
            WinkelModus winkelModus,
            Map<String, Double> variablen)
    {
        Deque<Double> stack = new ArrayDeque<>();

        for (AusdruckToken token : postfix)
        {
            String text = token.text();

            if (AusdruckTokenizer.istZahl(text))
            {
                stack.push(Double.parseDouble(text.replace(',', '.')));
            }
            else if (OperatorRegistry.istOperator(text))
            {
                stack.push(wendeOperatorAn(token, stack));
            }
            else if (FunktionsRegistry.istFunktion(text))
            {
                stack.push(wendeFunktionAn(token, stack, ans, winkelModus));
            }
            else if (AusdruckTokenizer.istName(text))
            {
                stack.push(identifierWert(token, ans, variablen));
            }
            else
            {
                throw new AusdruckParserException(ParserFehler.SYNTAX, "Unbekanntes Zeichen „" + text + "“", token.position());
            }
        }

        if (stack.size() != 1)
        {
            throw new AusdruckParserException(ParserFehler.SYNTAX, "Der Ausdruck ist unvollständig.");
        }

        return stack.pop();
    }

    private static double identifierWert(AusdruckToken token, double ans, Map<String, Double> variablen)
    {
        String text = token.text();
        if (FunktionsRegistry.istKonstante(text))
        {
            return FunktionsRegistry.konstante(text, ans);
        }
        if (variablen.containsKey(text))
        {
            return variablen.get(text);
        }
        throw new AusdruckParserException(ParserFehler.UNBEKANNTE_FUNKTION, "Unbekannter Name „" + text + "“", token.position());
    }

    private static double wendeOperatorAn(AusdruckToken token, Deque<Double> stack)
    {
        String operator = token.text();
        double b = popOperand(stack, token);
        double a = OperatorRegistry.stelligkeit(operator) == 2 ? popOperand(stack, token) : b;

        try
        {
            return OperatorRegistry.wendeAn(operator, a, b);
        }
        catch (AusdruckParserException e)
        {
            throw e.mitPosition(token.position());
        }
    }

    private static double wendeFunktionAn(AusdruckToken token, Deque<Double> stack, double ans, WinkelModus winkelModus)
    {
        String funktion = token.text();
        double[] argumente = new double[FunktionsRegistry.stelligkeit(funktion)];
        for (int i = argumente.length - 1; i >= 0; i--)
        {
            argumente[i] = popOperand(stack, token);
        }

        double ergebnis;
        try
        {
            ergebnis = FunktionsRegistry.rechne(funktion, argumente, ans, winkelModus);
        }
        catch (AusdruckParserException e)
        {
            // Bei Benutzerfunktionen zeigt die innere Stelle in deren Ausdruck – nach außen zählt der Aufruf.
            throw e.mitPosition(token.position());
        }

        if (!Double.isFinite(ergebnis))
        {
            throw new AusdruckParserException(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH,
                    funktion + " ist hier nicht definiert oder das Ergebnis ist zu groß", token.position());
        }

        return ergebnis;
    }

    private static double popOperand(Deque<Double> stack, AusdruckToken token)
    {
        if (stack.isEmpty())
        {
            throw new AusdruckParserException(ParserFehler.SYNTAX, "Hier fehlt eine Zahl", token.position());
        }
        return stack.pop();
    }
}
