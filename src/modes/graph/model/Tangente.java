package modes.graph.model;

/** Tangente t(x) = steigung·x + achsenabschnitt an der Stelle x0 einer Funktion. */
public record Tangente(int funktionIndex, double x0, double y0, double steigung)
{
    public double achsenabschnitt()
    {
        return y0 - steigung * x0;
    }

    public double wert(double x)
    {
        return steigung * x + achsenabschnitt();
    }
}
