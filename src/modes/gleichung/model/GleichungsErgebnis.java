package modes.gleichung.model;

import modes.komplex.model.KomplexeZahl;

import java.util.List;

/**
 * Ergebnis einer linearen oder quadratischen Gleichung.
 * Reelle Lösungen stehen ebenfalls als {@link KomplexeZahl} mit Imaginärteil 0 in der Liste.
 */
public record GleichungsErgebnis(Art art, List<KomplexeZahl> loesungen, String anzeige, List<String> rechenweg)
{
    public enum Art
    {
        KEINE_LOESUNG,
        UNENDLICH,
        REELL,
        KOMPLEX
    }

    public GleichungsErgebnis
    {
        loesungen = List.copyOf(loesungen);
        rechenweg = List.copyOf(rechenweg);
    }
}
