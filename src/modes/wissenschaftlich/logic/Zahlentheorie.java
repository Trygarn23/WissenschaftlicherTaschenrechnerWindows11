package modes.wissenschaftlich.logic;

public final class Zahlentheorie
{
    static final long GRENZE = 1_000_000_000_000_000L;
    private static final String HOCHZAHLEN = "⁰¹²³⁴⁵⁶⁷⁸⁹";

    private Zahlentheorie()
    {
    }

    /** z. B. {@code primfaktoren(360)} → „360 = 2³ · 3² · 5“. */
    public static String primfaktoren(long n)
    {
        if (n < 2 || n > GRENZE)
        {
            throw new IllegalArgumentException("Primfaktorzerlegung geht nur für ganze Zahlen von 2 bis 10¹⁵.");
        }

        StringBuilder ergebnis = new StringBuilder().append(n).append(" =");
        long rest = n;
        // Probedivision bis √n reicht bei 10^15 (≈ 3·10^7 Schritte).
        for (long teiler = 2; teiler * teiler <= rest; teiler += teiler == 2 ? 1 : 2)
        {
            int exponent = 0;
            while (rest % teiler == 0)
            {
                rest /= teiler;
                exponent++;
            }
            if (exponent > 0)
            {
                haengeFaktorAn(ergebnis, teiler, exponent);
            }
        }
        if (rest > 1)
        {
            haengeFaktorAn(ergebnis, rest, 1);
        }
        return ergebnis.toString();
    }

    private static void haengeFaktorAn(StringBuilder ergebnis, long faktor, int exponent)
    {
        ergebnis.append(ergebnis.charAt(ergebnis.length() - 1) == '=' ? " " : " · ").append(faktor);
        if (exponent > 1)
        {
            for (char ziffer : Integer.toString(exponent).toCharArray())
            {
                ergebnis.append(HOCHZAHLEN.charAt(ziffer - '0'));
            }
        }
    }
}
