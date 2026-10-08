package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.Flaeche;

import java.util.function.DoubleUnaryOperator;

/** Fläche zwischen Kurve und x-Achse numerisch mit der Simpson-Regel. */
public class FlaechenRechner
{
    private static final int TEILE = 2_000;
    /** Weichen grobe und feine Rechnung stärker ab, konvergiert das Integral nicht (typisch: Polstelle). */
    private static final double KONVERGENZ_TOLERANZ = 1e-4;

    private final GraphEvaluator evaluator;

    public FlaechenRechner(GraphEvaluator evaluator)
    {
        this.evaluator = evaluator;
    }

    public Flaeche berechne(int funktionIndex, String ausdruck, double a, double b, WinkelModus winkelModus)
    {
        if (!Double.isFinite(a) || !Double.isFinite(b))
        {
            throw new IllegalArgumentException("Die Grenzen müssen endliche Zahlen sein");
        }
        if (a == b)
        {
            throw new IllegalArgumentException("Die Grenzen a und b dürfen nicht gleich sein");
        }

        double links = Math.min(a, b);
        double rechts = Math.max(a, b);
        DoubleUnaryOperator f = x -> evaluator.wertOderNaN(ausdruck, x, winkelModus);

        double integral = simpson(f, links, rechts, TEILE);
        double grob = simpson(f, links, rechts, TEILE / 2);
        if (!Double.isFinite(integral) || !Double.isFinite(grob)
                || Math.abs(integral - grob) > KONVERGENZ_TOLERANZ * Math.max(1.0, Math.abs(integral)))
        {
            throw new IllegalArgumentException("Im Intervall ist die Funktion nicht überall definiert (Polstelle o. Ä.) – keine sinnvolle Fläche");
        }

        double inhalt = simpson(x -> Math.abs(f.applyAsDouble(x)), links, rechts, TEILE);
        // Vertauschte Grenzen drehen wie in der Mathematik das Vorzeichen um.
        double vorzeichen = a < b ? 1.0 : -1.0;
        return new Flaeche(funktionIndex, a, b, vorzeichen * integral, inhalt);
    }

    static double simpson(DoubleUnaryOperator f, double a, double b, int teile)
    {
        double h = (b - a) / teile;
        double summe = f.applyAsDouble(a) + f.applyAsDouble(b);
        for (int i = 1; i < teile; i++)
        {
            summe += f.applyAsDouble(a + i * h) * (i % 2 == 0 ? 2 : 4);
        }
        return summe * h / 3.0;
    }
}
