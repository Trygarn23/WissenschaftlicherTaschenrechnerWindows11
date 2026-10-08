package modes.matrix.model;

import java.util.List;

/**
 * Ergebnis von Ax = b. {@code loesung} ist nur bei genau einer Lösung gesetzt,
 * {@code stufenform} ist immer die reduzierte erweiterte Matrix (A|b).
 */
public record LgsLoesung(Art art, Matrix loesung, Matrix stufenform, int rangA, int rangErweitert,
                         int unbekannte, List<RechenSchritt> schritte)
{
    public enum Art
    {
        EINDEUTIG,
        KEINE,
        UNENDLICH_VIELE
    }

    public LgsLoesung
    {
        schritte = List.copyOf(schritte);
    }

    public String meldung()
    {
        return switch (art)
        {
            case EINDEUTIG -> "Das Gleichungssystem hat genau eine Lösung.";
            case KEINE -> "Das Gleichungssystem hat keine Lösung (Rang A = " + rangA
                    + " < Rang (A|b) = " + rangErweitert + ").";
            case UNENDLICH_VIELE -> "Das Gleichungssystem hat unendlich viele Lösungen (Rang " + rangA
                    + " < " + unbekannte + " Unbekannte).";
        };
    }
}
