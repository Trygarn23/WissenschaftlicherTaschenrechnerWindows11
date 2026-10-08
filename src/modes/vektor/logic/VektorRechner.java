package modes.vektor.logic;

import modes.vektor.model.Vektor;

public final class VektorRechner
{
    private static final double NULL_TOLERANZ = 1e-12;

    private VektorRechner()
    {
    }

    public static Vektor addiere(Vektor a, Vektor b)
    {
        pruefeGleicheDimension(a, b);
        return new Vektor(a.x() + b.x(), a.y() + b.y(), a.z() + b.z(), a.dimension());
    }

    public static Vektor subtrahiere(Vektor a, Vektor b)
    {
        pruefeGleicheDimension(a, b);
        return new Vektor(a.x() - b.x(), a.y() - b.y(), a.z() - b.z(), a.dimension());
    }

    public static Vektor skaliere(Vektor a, double faktor)
    {
        return new Vektor(a.x() * faktor, a.y() * faktor, a.z() * faktor, a.dimension());
    }

    public static double skalarprodukt(Vektor a, Vektor b)
    {
        pruefeGleicheDimension(a, b);
        return a.x() * b.x() + a.y() * b.y() + a.z() * b.z();
    }

    public static double betrag(Vektor a)
    {
        return Math.sqrt(a.x() * a.x() + a.y() * a.y() + a.z() * a.z());
    }

    public static double winkelInGrad(Vektor a, Vektor b)
    {
        pruefeGleicheDimension(a, b);
        double betragA = betrag(a);
        double betragB = betrag(b);
        if (betragA < NULL_TOLERANZ || betragB < NULL_TOLERANZ)
        {
            throw new IllegalArgumentException("Mit dem Nullvektor gibt es keinen Winkel.");
        }
        // Rundungsfehler können den Kosinus knapp über 1 schieben, dann liefert acos NaN.
        double kosinus = Math.max(-1, Math.min(1, skalarprodukt(a, b) / (betragA * betragB)));
        return Math.toDegrees(Math.acos(kosinus));
    }

    public static Vektor kreuzprodukt(Vektor a, Vektor b)
    {
        pruefeGleicheDimension(a, b);
        if (a.dimension() != 3)
        {
            throw new IllegalArgumentException("Das Kreuzprodukt gibt es nur für 3D-Vektoren.");
        }
        return Vektor.dreiD(
                a.y() * b.z() - a.z() * b.y(),
                a.z() * b.x() - a.x() * b.z(),
                a.x() * b.y() - a.y() * b.x());
    }

    public static double abstand(Vektor p, Vektor q)
    {
        return betrag(subtrahiere(q, p));
    }

    public static Vektor mittelpunkt(Vektor p, Vektor q)
    {
        return skaliere(addiere(p, q), 0.5);
    }

    public static double steigung(Vektor p, Vektor q)
    {
        pruefeGleicheDimension(p, q);
        if (p.dimension() != 2)
        {
            throw new IllegalArgumentException("Die Steigung gibt es nur für 2D-Punkte.");
        }
        double dx = q.x() - p.x();
        if (Math.abs(dx) < NULL_TOLERANZ)
        {
            throw new IllegalArgumentException("Steigung nicht definiert (senkrechte Gerade).");
        }
        return (q.y() - p.y()) / dx;
    }

    private static void pruefeGleicheDimension(Vektor a, Vektor b)
    {
        if (a.dimension() != b.dimension())
        {
            throw new IllegalArgumentException("Beide Vektoren brauchen die gleiche Dimension (2D oder 3D).");
        }
    }
}
