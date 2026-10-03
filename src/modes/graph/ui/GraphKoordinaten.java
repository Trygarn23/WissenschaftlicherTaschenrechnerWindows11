package modes.graph.ui;

import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;

/** Rechnet zwischen Bildschirm-Pixeln und Graph-Koordinaten um, für die aktuelle Größe der Zeichenfläche. */
record GraphKoordinaten(GraphState state, int breite, int hoehe)
{
    int zuBildschirmX(double x)
    {
        return (int) Math.round((x - state.getXMin()) / (state.getXMax() - state.getXMin()) * breite);
    }

    int zuBildschirmY(double y)
    {
        return (int) Math.round((state.getYMax() - y) / (state.getYMax() - state.getYMin()) * hoehe);
    }

    double zuWeltX(int x)
    {
        return state.getXMin() + (x / Math.max(1.0, breite - 1.0)) * (state.getXMax() - state.getXMin());
    }

    double zuWeltY(int y)
    {
        return state.getYMax() - (y / Math.max(1.0, hoehe - 1.0)) * (state.getYMax() - state.getYMin());
    }

    boolean istSichtbar(GraphPunkt punkt)
    {
        return punkt.getX() >= state.getXMin()
                && punkt.getX() <= state.getXMax()
                && punkt.getY() >= state.getYMin()
                && punkt.getY() <= state.getYMax();
    }
}
