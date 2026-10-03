package modes.graph.logic;

import common.parser.AusdruckParser;
import common.state.WinkelModus;
import modes.graph.model.FunktionsDefinition;

import java.util.List;
import java.util.Map;

public class GraphEvaluator
{
    private static final double DEFAULT_H = 1e-4;
    private final GraphFunktionsResolver funktionsResolver = new GraphFunktionsResolver();
    private List<FunktionsDefinition> funktionen = List.of();

    public void setFunktionen(List<FunktionsDefinition> funktionen)
    {
        this.funktionen = funktionen == null ? List.of() : funktionen;
    }

    public double auswerten(String ausdruck, double x, WinkelModus winkelModus)
    {
        if (funktionen.isEmpty())
        {
            return AusdruckParser.auswerten(ausdruck, 0.0, winkelModus, Map.of("x", x));
        }
        return funktionsResolver.auswerten(ausdruck, x, winkelModus, funktionen);
    }

    /**
     * Wie {@link #auswerten}, liefert bei ungültigem Ausdruck oder undefinierter Stelle aber {@code NaN}
     * statt einer Exception. Fürs Zeichnen und die Kurvendiskussion ist das der Normalfall.
     */
    public double wertOderNaN(String ausdruck, double x, WinkelModus winkelModus)
    {
        try
        {
            double y = auswerten(ausdruck, x, winkelModus);
            return Double.isFinite(y) ? y : Double.NaN;
        }
        catch (IllegalArgumentException e)
        {
            return Double.NaN;
        }
    }

    public double ersteAbleitung(String ausdruck, double x, WinkelModus winkelModus)
    {
        double links = wertOderNaN(ausdruck, x - DEFAULT_H, winkelModus);
        double rechts = wertOderNaN(ausdruck, x + DEFAULT_H, winkelModus);
        return (rechts - links) / (2.0 * DEFAULT_H);
    }

    public double zweiteAbleitung(String ausdruck, double x, WinkelModus winkelModus)
    {
        double links = wertOderNaN(ausdruck, x - DEFAULT_H, winkelModus);
        double mitte = wertOderNaN(ausdruck, x, winkelModus);
        double rechts = wertOderNaN(ausdruck, x + DEFAULT_H, winkelModus);
        return (links - 2.0 * mitte + rechts) / (DEFAULT_H * DEFAULT_H);
    }

    public boolean istGueltig(String ausdruck, WinkelModus winkelModus)
    {
        return Double.isFinite(wertOderNaN(ausdruck, 0.0, winkelModus));
    }
}
