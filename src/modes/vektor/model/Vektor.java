package modes.vektor.model;

import common.formatting.ZahlenAnzeige;

/** Unveränderlicher 2D- oder 3D-Vektor. Bei 2D ist z immer 0. */
public record Vektor(double x, double y, double z, int dimension)
{
    public Vektor
    {
        if (dimension != 2 && dimension != 3)
        {
            throw new IllegalArgumentException("Ein Vektor muss 2 oder 3 Komponenten haben.");
        }
        if (dimension == 2)
        {
            z = 0;
        }
    }

    public static Vektor zweiD(double x, double y)
    {
        return new Vektor(x, y, 0, 2);
    }

    public static Vektor dreiD(double x, double y, double z)
    {
        return new Vektor(x, y, z, 3);
    }

    public String alsText()
    {
        String text = "(" + ZahlenAnzeige.formatiere(x) + " | " + ZahlenAnzeige.formatiere(y);
        if (dimension == 3)
        {
            text += " | " + ZahlenAnzeige.formatiere(z);
        }
        return text + ")";
    }
}
