package ui.befehle;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Filtert und sortiert Befehle nach Suchtext, ohne Swing. */
public final class BefehlsSuche
{
    private static final int KEIN_TREFFER = Integer.MAX_VALUE;

    private BefehlsSuche()
    {
    }

    public static List<Befehl> filtere(List<Befehl> alle, String text, int max)
    {
        String suche = normalisiere(text == null ? "" : text.strip());
        if (suche.isEmpty())
        {
            return alle.stream().limit(max).toList();
        }

        // stream().sorted() ist stabil: bei gleichem Rang bleibt die Originalreihenfolge.
        return alle.stream()
                .filter(befehl -> rang(befehl, suche) != KEIN_TREFFER)
                .sorted(Comparator.comparingInt(befehl -> rang(befehl, suche)))
                .limit(max)
                .toList();
    }

    private static int rang(Befehl befehl, String suche)
    {
        String titel = normalisiere(befehl.titel());
        if (titel.startsWith(suche))
        {
            return 0;
        }
        for (String wort : titel.split("[^\\p{L}\\p{N}]+"))
        {
            if (wort.startsWith(suche))
            {
                return 1;
            }
        }

        String alles = titel + " " + normalisiere(befehl.kategorie()) + " " + normalisiere(String.join(" ", befehl.suchbegriffe()));
        if (alles.contains(suche))
        {
            return 2;
        }
        return enthaeltInReihenfolge(titel, suche) ? 3 : KEIN_TREFFER;
    }

    private static boolean enthaeltInReihenfolge(String text, String zeichen)
    {
        int position = 0;
        for (char c : zeichen.toCharArray())
        {
            position = text.indexOf(c, position) + 1;
            if (position == 0)
            {
                return false;
            }
        }
        return true;
    }

    static String normalisiere(String text)
    {
        return text.toLowerCase(Locale.ROOT)
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss");
    }
}
