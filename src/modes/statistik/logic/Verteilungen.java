package modes.statistik.logic;

/** Binomial- und Normalverteilung für die typischen Abi-Aufgaben. */
public final class Verteilungen
{
    private static final int MAX_N = 10_000_000;
    private static final double GRENZE_Z = 8.5;

    private Verteilungen()
    {
    }

    /** P(X = k) */
    public static double binomialGenau(int n, double p, int k)
    {
        return binomialSumme(n, p, k, k);
    }

    /** P(X ≤ k) */
    public static double binomialHoechstens(int n, double p, int k)
    {
        return binomialSumme(n, p, 0, k);
    }

    /** P(X ≥ k) */
    public static double binomialMindestens(int n, double p, int k)
    {
        return binomialSumme(n, p, k, n);
    }

    public static double binomialErwartungswert(int n, double p)
    {
        pruefeBinomial(n, p);
        return n * p;
    }

    public static double binomialStandardabweichung(int n, double p)
    {
        pruefeBinomial(n, p);
        return Math.sqrt(n * p * (1.0 - p));
    }

    /** P(X ≤ x) */
    public static double normalVerteilung(double mu, double sigma, double x)
    {
        pruefeNormal(mu, sigma);
        if (!Double.isFinite(x))
        {
            throw new IllegalArgumentException("x muss eine endliche Zahl sein.");
        }
        return phi((x - mu) / sigma);
    }

    /** P(a ≤ X ≤ b) */
    public static double normalIntervall(double mu, double sigma, double a, double b)
    {
        if (a > b)
        {
            throw new IllegalArgumentException("Die untere Grenze a darf nicht größer als b sein.");
        }
        return normalVerteilung(mu, sigma, b) - normalVerteilung(mu, sigma, a);
    }

    /** Das x mit P(X ≤ x) = wahrscheinlichkeit. */
    public static double normalQuantil(double mu, double sigma, double wahrscheinlichkeit)
    {
        pruefeNormal(mu, sigma);
        if (!(wahrscheinlichkeit > 0.0 && wahrscheinlichkeit < 1.0))
        {
            throw new IllegalArgumentException("Die Wahrscheinlichkeit für das Quantil muss zwischen 0 und 1 liegen.");
        }

        // Bisektion: langsam, aber robust; Φ ist streng monoton.
        double unten = -GRENZE_Z;
        double oben = GRENZE_Z;
        for (int i = 0; i < 200 && oben - unten > 1e-15; i++)
        {
            double mitte = (unten + oben) / 2.0;
            if (phi(mitte) < wahrscheinlichkeit)
            {
                unten = mitte;
            }
            else
            {
                oben = mitte;
            }
        }
        return mu + sigma * (unten + oben) / 2.0;
    }

    /**
     * Standardnormalverteilung nach Marsaglia (2004), absolut genau auf etwa 1e-15.
     * ponytail: jenseits von |z| = 8,5 wird auf 0 bzw. 1 gerundet (Φ(-8,5) ≈ 1e-17);
     * für winzige Randwahrscheinlichkeiten bräuchte es eine erfc-Kettenbruch-Variante.
     */
    static double phi(double z)
    {
        if (z < -GRENZE_Z)
        {
            return 0.0;
        }
        if (z > GRENZE_Z)
        {
            return 1.0;
        }

        double summe = z;
        double vorher = 0.0;
        double term = z;
        double quadrat = z * z;
        for (int i = 3; summe != vorher; i += 2)
        {
            vorher = summe;
            term *= quadrat / i;
            summe += term;
        }
        // 0.9189... = ln(√(2π))
        double wert = 0.5 + summe * Math.exp(-0.5 * quadrat - 0.91893853320467274178);
        return Math.min(1.0, Math.max(0.0, wert));
    }

    /**
     * Summiert P(X = i) für i in [von, bis]. Gerechnet wird im Logarithmus,
     * damit (1 - p)^n bei großem n nicht auf 0 abrutscht und die Rekursion abreißt.
     */
    private static double binomialSumme(int n, double p, int von, int bis)
    {
        pruefeBinomial(n, p);
        von = Math.max(0, von);
        bis = Math.min(n, bis);
        if (von > bis)
        {
            return 0.0;
        }
        if (p == 0.0)
        {
            return von == 0 ? 1.0 : 0.0;
        }
        if (p == 1.0)
        {
            return bis == n ? 1.0 : 0.0;
        }

        double lnP = Math.log(p);
        double lnQ = Math.log1p(-p);
        double lnWahrscheinlichkeit = n * lnQ;
        double summe = 0.0;
        for (int i = 0; i <= bis; i++)
        {
            if (i >= von)
            {
                summe += Math.exp(lnWahrscheinlichkeit);
            }
            lnWahrscheinlichkeit += Math.log(n - i) - Math.log(i + 1.0) + lnP - lnQ;
        }
        return Math.min(1.0, summe);
    }

    private static void pruefeBinomial(int n, double p)
    {
        if (n < 0)
        {
            throw new IllegalArgumentException("n darf nicht negativ sein.");
        }
        if (n > MAX_N)
        {
            throw new IllegalArgumentException("n ist zu groß (höchstens 10.000.000).");
        }
        if (!(p >= 0.0 && p <= 1.0))
        {
            throw new IllegalArgumentException("p muss zwischen 0 und 1 liegen.");
        }
    }

    private static void pruefeNormal(double mu, double sigma)
    {
        if (!Double.isFinite(mu))
        {
            throw new IllegalArgumentException("μ muss eine endliche Zahl sein.");
        }
        if (!(sigma > 0.0) || !Double.isFinite(sigma))
        {
            throw new IllegalArgumentException("σ muss größer als 0 sein.");
        }
    }
}
