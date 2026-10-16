package common.logic;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class KlammerPaare
{
    private KlammerPaare()
    {
    }

    /**
     * Verschachtelungstiefe jeder Klammer, die ein Gegenstück hat (äußerstes Paar = 0).
     * Beide Klammern eines Paares bekommen dieselbe Tiefe; alle anderen Zeichen und
     * Klammern ohne Partner bekommen -1. Beispiel: „(1+(2))“ → 0, -1, -1, 1, -1, 1, 0.
     */
    public static int[] tiefen(String text)
    {
        int[] tiefen = new int[text == null ? 0 : text.length()];
        Arrays.fill(tiefen, -1);

        Deque<Integer> offen = new ArrayDeque<>();
        for (int i = 0; i < tiefen.length; i++)
        {
            char zeichen = text.charAt(i);
            if (zeichen == '(')
            {
                offen.push(i);
            }
            else if (zeichen == ')' && !offen.isEmpty())
            {
                int start = offen.pop();
                tiefen[start] = offen.size();
                tiefen[i] = offen.size();
            }
        }
        return tiefen;
    }
}
