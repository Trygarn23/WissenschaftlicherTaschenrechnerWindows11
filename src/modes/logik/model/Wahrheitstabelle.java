package modes.logik.model;

import java.util.List;

/**
 * Zeile i entspricht der Binärzahl i über den Variablen, die erste Variable ist das höchste Bit.
 */
public record Wahrheitstabelle(List<Character> variablen, List<Boolean> ergebnisse)
{
    public Wahrheitstabelle
    {
        variablen = List.copyOf(variablen);
        ergebnisse = List.copyOf(ergebnisse);
        if (ergebnisse.size() != 1 << variablen.size())
        {
            throw new IllegalArgumentException("Anzahl der Zeilen passt nicht zu den Variablen.");
        }
    }

    public int anzahlZeilen()
    {
        return ergebnisse.size();
    }

    public boolean wert(int zeile, int spalte)
    {
        return ((zeile >> (variablen.size() - 1 - spalte)) & 1) == 1;
    }

    public boolean ergebnis(int zeile)
    {
        return ergebnisse.get(zeile);
    }

    public int anzahlWahr()
    {
        return (int) ergebnisse.stream().filter(b -> b).count();
    }

    public boolean istTautologie()
    {
        return anzahlWahr() == anzahlZeilen();
    }

    public boolean istWiderspruch()
    {
        return anzahlWahr() == 0;
    }

    public String beschreibung()
    {
        if (istTautologie())
        {
            return "Tautologie";
        }
        if (istWiderspruch())
        {
            return "Widerspruch";
        }
        return "erfüllbar (" + anzahlWahr() + " von " + anzahlZeilen() + " Zeilen wahr)";
    }
}
