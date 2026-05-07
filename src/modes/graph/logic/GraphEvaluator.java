package modes.graph.logic;

import common.parser.AusdruckParser;
import common.state.WinkelModus;

import java.util.Map;

public class GraphEvaluator
{
    public double auswerten(String ausdruck, double x, WinkelModus winkelModus)
    {
        return AusdruckParser.auswerten(ausdruck, 0.0, winkelModus, Map.of("x", x));
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
