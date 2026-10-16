package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.Flaeche;
import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import modes.graph.model.Tangente;

import java.util.ArrayList;
import java.util.List;

/** Hält Tangente und Fläche zur Funktionsliste, rechnet sie bei Änderungen nach und sucht Schnittpunkte. Kennt kein Swing. */
public class GraphAnalyse
{
    private final GraphEvaluator evaluator;
    private final GraphIntersectionService intersectionService;
    private final FlaechenRechner flaechenRechner;

    private Tangente tangente;
    private Flaeche flaeche;

    public GraphAnalyse(GraphEvaluator evaluator)
    {
        this.evaluator = evaluator;
        this.intersectionService = new GraphIntersectionService(evaluator);
        this.flaechenRechner = new FlaechenRechner(evaluator);
    }

    public Tangente getTangente()
    {
        return tangente;
    }

    public Flaeche getFlaeche()
    {
        return flaeche;
    }

    /** Nach Entfernen/Laden von Funktionen: Indizes stimmen nicht mehr, also beides verwerfen. */
    public void verwerfeAlles()
    {
        tangente = null;
        flaeche = null;
    }

    public void entferneTangente()
    {
        tangente = null;
    }

    public void entferneFlaeche()
    {
        flaeche = null;
    }

    /** Setzt die Tangente an Stelle {@code x}; {@code null}, wenn es dort keine gibt (die alte bleibt dann). */
    public Tangente setzeTangente(GraphState state, int funktionIndex, double x, WinkelModus winkelModus)
    {
        Tangente neu = evaluator.tangente(funktionIndex, state.getFunktion(funktionIndex).getAusdruck(), x, winkelModus);
        if (neu != null)
        {
            tangente = neu;
        }
        return neu;
    }

    /** Fläche unter der aktiven Funktion; wirft {@link IllegalArgumentException} bei ungültigen Grenzen, die alte Fläche bleibt dann. */
    public Flaeche berechneFlaeche(GraphState state, double a, double b, WinkelModus winkelModus)
    {
        flaeche = flaechenRechner.berechne(state.getAktiveFunktionIndex(), state.getAktiveFunktion().getAusdruck(), a, b, winkelModus);
        return flaeche;
    }

    /** Rechnet Tangente und Fläche für geänderte Ausdrücke/Parameter nach; liefert eine Fehlermeldung oder {@code null}. */
    public String aktualisiere(GraphState state, WinkelModus winkelModus)
    {
        if (tangente != null)
        {
            tangente = evaluator.tangente(tangente.funktionIndex(),
                    state.getFunktion(tangente.funktionIndex()).getAusdruck(), tangente.x0(), winkelModus);
        }
        if (flaeche != null)
        {
            try
            {
                flaeche = flaechenRechner.berechne(flaeche.funktionIndex(),
                        state.getFunktion(flaeche.funktionIndex()).getAusdruck(), flaeche.a(), flaeche.b(), winkelModus);
            }
            catch (IllegalArgumentException e)
            {
                flaeche = null;
                return e.getMessage();
            }
        }
        return null;
    }

    /** Schnittpunkte der aktiven mit allen anderen sichtbaren Funktionen im sichtbaren x-Bereich. */
    public List<GraphPunkt> schnittpunkte(GraphState state, WinkelModus winkelModus)
    {
        if (state.getFunktionen().size() < 2 || !state.getAktiveFunktion().isSichtbar())
        {
            return List.of();
        }

        List<GraphPunkt> punkte = new ArrayList<>();
        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            if (index == state.getAktiveFunktionIndex() || !state.getFunktion(index).isSichtbar())
            {
                continue;
            }

            // Eine ungültige Nebenfunktion liefert einfach keine Schnittpunkte (NaN), statt alles zu blockieren.
            punkte.addAll(intersectionService.findeSchnittpunkte(
                    state.getAktiveFunktion().getAusdruck(),
                    state.getFunktion(index).getAusdruck(),
                    state.getXMin(),
                    state.getXMax(),
                    winkelModus
            ));
        }
        return punkte;
    }
}
