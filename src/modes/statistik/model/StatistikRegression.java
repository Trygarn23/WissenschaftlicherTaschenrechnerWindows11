package modes.statistik.model;

public final class StatistikRegression
{
    private final String typ;
    private final double a;
    private final double b;
    private final double c;
    private final double bestimmtheitsmass;

    private StatistikRegression(String typ, double a, double b, double c, double bestimmtheitsmass)
    {
        this.typ = typ;
        this.a = a;
        this.b = b;
        this.c = c;
        this.bestimmtheitsmass = bestimmtheitsmass;
    }

    public static StatistikRegression linear(double steigung, double achsenabschnitt, double bestimmtheitsmass)
    {
        return new StatistikRegression("Linear", 0.0, steigung, achsenabschnitt, bestimmtheitsmass);
    }

    public static StatistikRegression quadratisch(double a, double b, double c, double bestimmtheitsmass)
    {
        return new StatistikRegression("Quadratisch", a, b, c, bestimmtheitsmass);
    }

    public String getTyp()
    {
        return typ;
    }

    public double getA()
    {
        return a;
    }

    public double getB()
    {
        return b;
    }

    public double getC()
    {
        return c;
    }

    public double getBestimmtheitsmass()
    {
        return bestimmtheitsmass;
    }

    public double vorhersage(double x)
    {
        return a * x * x + b * x + c;
    }
}
