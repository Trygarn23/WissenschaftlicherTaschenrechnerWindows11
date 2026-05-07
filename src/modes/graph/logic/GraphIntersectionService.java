package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.GraphPunkt;

import java.util.ArrayList;
import java.util.List;

public class GraphIntersectionService
{
    private static final int SAMPLES = 360;
    private static final int MAX_INTERSECTIONS = 8;

    private final GraphEvaluator evaluator;

    public GraphIntersectionService(GraphEvaluator evaluator)
    {
        this.evaluator = evaluator;
    }

    public List<GraphPunkt> findeSchnittpunkte(String ersterAusdruck, String zweiterAusdruck, double xMin, double xMax, WinkelModus winkelModus)
    {
        List<GraphPunkt> result = new ArrayList<>();
        double step = (xMax - xMin) / SAMPLES;
        double prevX = xMin;
        double prevDiff = differenz(ersterAusdruck, zweiterAusdruck, prevX, winkelModus);

        for (int i = 1; i <= SAMPLES && result.size() < MAX_INTERSECTIONS; i++)
        {
            double x = xMin + i * step;
            double diff = differenz(ersterAusdruck, zweiterAusdruck, x, winkelModus);

            if (Double.isFinite(prevDiff) && Double.isFinite(diff))
            {
                if (Math.abs(diff) < 1e-7)
                {
                    fuegeEinzigartigHinzu(result, punkt(ersterAusdruck, x, winkelModus));
                }
                else if (prevDiff * diff < 0.0)
                {
                    double root = bisektion(ersterAusdruck, zweiterAusdruck, prevX, x, winkelModus);
                    fuegeEinzigartigHinzu(result, punkt(ersterAusdruck, root, winkelModus));
                }
            }

            prevX = x;
            prevDiff = diff;
        }

        return result;
    }

    private double bisektion(String ersterAusdruck, String zweiterAusdruck, double links, double rechts, WinkelModus winkelModus)
    {
        double fLinks = differenz(ersterAusdruck, zweiterAusdruck, links, winkelModus);

        for (int i = 0; i < 48; i++)
        {
            double mitte = (links + rechts) / 2.0;
            double fMitte = differenz(ersterAusdruck, zweiterAusdruck, mitte, winkelModus);

            if (!Double.isFinite(fMitte) || Math.abs(fMitte) < 1e-10)
            {
                return mitte;
            }

            if (fLinks * fMitte <= 0.0)
            {
                rechts = mitte;
            }
            else
            {
                links = mitte;
                fLinks = fMitte;
            }
        }

        return (links + rechts) / 2.0;
    }

    private GraphPunkt punkt(String ausdruck, double x, WinkelModus winkelModus)
    {
        return new GraphPunkt(x, evaluator.auswerten(ausdruck, x, winkelModus));
    }

    private double differenz(String ersterAusdruck, String zweiterAusdruck, double x, WinkelModus winkelModus)
    {
        try
        {
            double erster = evaluator.auswerten(ersterAusdruck, x, winkelModus);
            double zweiter = evaluator.auswerten(zweiterAusdruck, x, winkelModus);
            double diff = erster - zweiter;
            return Double.isFinite(diff) ? diff : Double.NaN;
        }
        catch (RuntimeException e)
        {
            return Double.NaN;
        }
    }

    private void fuegeEinzigartigHinzu(List<GraphPunkt> punkte, GraphPunkt punkt)
    {
        if (!Double.isFinite(punkt.getX()) || !Double.isFinite(punkt.getY()))
        {
            return;
        }

        for (GraphPunkt vorhanden : punkte)
        {
            if (Math.abs(vorhanden.getX() - punkt.getX()) < 1e-3)
            {
                return;
            }
        }

        punkte.add(punkt);
    }
}
