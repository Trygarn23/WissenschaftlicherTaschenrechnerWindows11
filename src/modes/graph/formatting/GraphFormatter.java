package modes.graph.formatting;

import modes.graph.model.GraphPunkt;
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

    private boolean istGanzzahl(double value)
    {
        return Math.abs(value - Math.rint(value)) < NULL_TOLERANZ;
    }
}
