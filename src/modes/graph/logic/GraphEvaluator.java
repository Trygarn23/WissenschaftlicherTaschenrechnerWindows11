package modes.graph.logic;

import common.parser.AusdruckParser;
import common.state.WinkelModus;

import java.util.Map;

public class GraphEvaluator
{
    private static final double DEFAULT_H = 1e-4;

    public double auswerten(String ausdruck, double x, WinkelModus winkelModus)
    {
        return AusdruckParser.auswerten(ausdruck, 0.0, winkelModus, Map.of("x", x));
    }

    public double ersteAbleitung(String ausdruck, double x, WinkelModus winkelModus)
    {
        double links = auswerten(ausdruck, x - DEFAULT_H, winkelModus);
        double rechts = auswerten(ausdruck, x + DEFAULT_H, winkelModus);
        return (rechts - links) / (2.0 * DEFAULT_H);
    }

    public double zweiteAbleitung(String ausdruck, double x, WinkelModus winkelModus)
    {
        double links = auswerten(ausdruck, x - DEFAULT_H, winkelModus);
        double mitte = auswerten(ausdruck, x, winkelModus);
        double rechts = auswerten(ausdruck, x + DEFAULT_H, winkelModus);
        return (links - 2.0 * mitte + rechts) / (DEFAULT_H * DEFAULT_H);
    }

    public boolean istGueltig(String ausdruck, WinkelModus winkelModus)
    {
        try
        {
            double wert = auswerten(ausdruck, 0.0, winkelModus);
            return Double.isFinite(wert);
        }
        catch (RuntimeException e)
        {
            return false;
        }
    }
}
