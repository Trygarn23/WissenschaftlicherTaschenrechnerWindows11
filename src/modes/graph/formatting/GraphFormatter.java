package modes.graph.formatting;

import modes.graph.model.Flaeche;
import modes.graph.model.GraphPunkt;
import modes.graph.model.Tangente;
import modes.graph.model.KurvendiskussionResult;

import java.util.List;
import java.util.stream.Collectors;

public class GraphFormatter
{
    private static final double NULL_TOLERANZ = 1e-9;

    /** Koordinaten mit bis zu drei Nachkommastellen, ohne überflüssige Nullen. */
    public String formatiereZahl(double value)
    {
        if (istGanzzahl(value))
        {
            return Long.toString(Math.round(value));
        }
        return String.format("%.3f", value).replaceAll("0+$", "").replaceAll("[,.]$", "");
    }

    /** Kurze Beschriftung für die Achsen (eine Nachkommastelle). */
    public String formatiereAchsenwert(double value)
    {
        if (istGanzzahl(value))
        {
            return Long.toString(Math.round(value));
        }
        return String.format("%.1f", value);
    }

    public String formatierePunkt(GraphPunkt punkt)
    {
        if (punkt == null)
        {
            return "nicht definiert";
        }
        return "(" + formatiereZahl(punkt.getX()) + " | " + formatiereZahl(punkt.getY()) + ")";
    }

    public String formatierePunkte(List<GraphPunkt> punkte)
    {
        if (punkte.isEmpty())
        {
            return "keine gefunden";
        }
        return punkte.stream().map(this::formatierePunkt).collect(Collectors.joining(", "));
    }

    public String formatiereKurvendiskussion(KurvendiskussionResult result, List<GraphPunkt> schnittpunkte)
    {
        return "Y-Achse: " + formatierePunkt(result.getYAchsenSchnittpunkt()) + "\n"
                + "Nullstellen: " + formatierePunkte(result.getNullstellen()) + "\n"
                + "Extrema: " + formatierePunkte(result.getExtremstellen()) + "\n"
                + "Wendestellen: " + formatierePunkte(result.getWendestellen()) + "\n"
                + "Schnitt mit anderen: " + formatierePunkte(schnittpunkte) + "\n"
                + "Hinweis: numerische Näherung im sichtbaren x-Bereich.";
    }

    /** z. B. „Tangente an x = 1: t(x) = 2·x − 1“. */
    public String formatiereTangente(Tangente tangente)
    {
        double n = tangente.achsenabschnitt();
        String rest = Math.abs(n) < NULL_TOLERANZ ? "" : (n < 0 ? " − " : " + ") + formatiereZahl(Math.abs(n));
        return "Tangente an x = " + formatiereZahl(tangente.x0()) + ": t(x) = "
                + formatiereZahl(tangente.steigung()) + "·x" + rest;
    }

    public String formatiereFlaeche(Flaeche flaeche)
    {
        return "Integral von " + formatiereZahl(flaeche.a()) + " bis " + formatiereZahl(flaeche.b())
                + " = " + formatiereZahl(flaeche.integral()) + "\n"
                + "Flächeninhalt (Betrag) = " + formatiereZahl(flaeche.flaecheninhalt()) + "\n"
                + "Hinweis: Flächen unter der x-Achse zählen im Integral negativ.";
    }

    private boolean istGanzzahl(double value)
    {
        return Math.abs(value - Math.rint(value)) < NULL_TOLERANZ;
    }
}
