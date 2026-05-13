package modes.statistik.model;

public record StatistikDatenpunkt(double x, double y, double gewicht)
{
    public StatistikDatenpunkt
    {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(gewicht))
        {
            throw new IllegalArgumentException("Statistikdaten muessen endlich sein.");
        }

        if (gewicht <= 0.0)
        {
            throw new IllegalArgumentException("Gewicht muss groesser als 0 sein.");
        }
    }

    public static StatistikDatenpunkt nurY(double x, double y)
    {
        return new StatistikDatenpunkt(x, y, 1.0);
    }
}
