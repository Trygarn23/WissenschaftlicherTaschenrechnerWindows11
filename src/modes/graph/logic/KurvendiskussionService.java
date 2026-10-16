package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.GraphPunkt;
import modes.graph.model.KurvendiskussionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class KurvendiskussionService
{
    private static final int SAMPLES = 360;
    private static final int BISEKTION_SCHRITTE = 48;
    /** Rasterwert gilt direkt als Nullstelle, wenn er so nah an 0 liegt. */
    private static final double NULL_TOLERANZ_RASTER = 1e-7;
    private static final double NULL_TOLERANZ_BISEKTION = 1e-10;
    /** Punkte mit kleinerem x-Abstand gelten als Duplikat. */
    private static final double MIN_X_ABSTAND_PUNKTE = 1e-3;
    private static final double MIN_ABLEITUNGS_SCHRITT = 1e-4;
    private static final double ABLEITUNGS_TEILUNGEN = 10_000.0;
    private static final int MAX_PUNKTE_PRO_KATEGORIE = 8;

    private final GraphEvaluator evaluator;

    public KurvendiskussionService(GraphEvaluator evaluator)
    {
        this.evaluator = evaluator;
    }

    public KurvendiskussionResult analysiere(String ausdruck, double xMin, double xMax, WinkelModus winkelModus)
    {
        DoubleUnaryOperator f = x -> evaluator.wertOderNaN(ausdruck, x, winkelModus);
        double h = Math.max(MIN_ABLEITUNGS_SCHRITT, (xMax - xMin) / ABLEITUNGS_TEILUNGEN);

        GraphPunkt yAchse = berechneYAchsenSchnittpunkt(f);
        List<GraphPunkt> nullstellen = findeNullstellen(f, xMin, xMax);
        List<GraphPunkt> extremstellen = findeAbleitungsNullstellen(f, xMin, xMax, h, false);
        List<GraphPunkt> wendestellen = findeAbleitungsNullstellen(f, xMin, xMax, h, true);

        return new KurvendiskussionResult(yAchse, nullstellen, extremstellen, wendestellen);
    }

    private GraphPunkt berechneYAchsenSchnittpunkt(DoubleUnaryOperator f)
    {
        double y = f.applyAsDouble(0.0);
        return Double.isFinite(y) ? new GraphPunkt(0.0, y) : null;
    }

    private List<GraphPunkt> findeNullstellen(DoubleUnaryOperator f, double xMin, double xMax)
    {
        return findeVorzeichenwechsel(f, xMin, xMax, root -> 0.0);
    }

    private List<GraphPunkt> findeAbleitungsNullstellen(DoubleUnaryOperator f, double xMin, double xMax, double h, boolean zweiteAbleitung)
    {
        DoubleUnaryOperator ableitung = zweiteAbleitung
                ? x -> zweiteAbleitung(f, x, h)
                : x -> ersteAbleitung(f, x, h);

        return findeVorzeichenwechsel(ableitung, xMin, xMax, root -> sicherAuswerten(f, root));
    }

    private List<GraphPunkt> findeVorzeichenwechsel(DoubleUnaryOperator funktion, double xMin, double xMax, DoubleUnaryOperator yResolver)
    {
        List<GraphPunkt> punkte = new ArrayList<>();
        double step = (xMax - xMin) / SAMPLES;
        double prevX = xMin;
        double prevY = sicherAuswerten(funktion, prevX);

        for (int i = 1; i <= SAMPLES && punkte.size() < MAX_PUNKTE_PRO_KATEGORIE; i++)
        {
            double x = xMin + i * step;
            double y = sicherAuswerten(funktion, x);

            if (Double.isFinite(prevY) && Double.isFinite(y))
            {
                if (Math.abs(y) < NULL_TOLERANZ_RASTER)
                {
                    fuegeEinzigartigHinzu(punkte, new GraphPunkt(x, yResolver.applyAsDouble(x)));
                }
                else if (prevY * y < 0.0)
                {
                    double root = bisektion(funktion, prevX, x);
                    fuegeEinzigartigHinzu(punkte, new GraphPunkt(root, yResolver.applyAsDouble(root)));
                }
            }

            prevX = x;
            prevY = y;
        }

        return punkte;
    }

    private double bisektion(DoubleUnaryOperator funktion, double links, double rechts)
    {
        double fLinks = sicherAuswerten(funktion, links);

        for (int i = 0; i < BISEKTION_SCHRITTE; i++)
        {
            double mitte = (links + rechts) / 2.0;
            double fMitte = sicherAuswerten(funktion, mitte);

            if (!Double.isFinite(fMitte) || Math.abs(fMitte) < NULL_TOLERANZ_BISEKTION)
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

    private double ersteAbleitung(DoubleUnaryOperator f, double x, double h)
    {
        double links = sicherAuswerten(f, x - h);
        double rechts = sicherAuswerten(f, x + h);
        return (rechts - links) / (2.0 * h);
    }

    private double zweiteAbleitung(DoubleUnaryOperator f, double x, double h)
    {
        double links = sicherAuswerten(f, x - h);
        double mitte = sicherAuswerten(f, x);
        double rechts = sicherAuswerten(f, x + h);
        return (links - 2.0 * mitte + rechts) / (h * h);
    }

    /** Unendlich zählt hier genauso als „nicht definiert“ wie NaN. */
    private double sicherAuswerten(DoubleUnaryOperator funktion, double x)
    {
        double y = funktion.applyAsDouble(x);
        return Double.isFinite(y) ? y : Double.NaN;
    }

    private void fuegeEinzigartigHinzu(List<GraphPunkt> punkte, GraphPunkt punkt)
    {
        for (GraphPunkt vorhanden : punkte)
        {
            if (Math.abs(vorhanden.getX() - punkt.getX()) < MIN_X_ABSTAND_PUNKTE)
            {
                return;
            }
        }

        if (Double.isFinite(punkt.getX()) && Double.isFinite(punkt.getY()))
        {
            punkte.add(punkt);
        }
    }
}
