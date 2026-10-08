package modes.bruch.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.LongBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exakter Bruch, immer gekürzt und mit positivem Nenner.
 * Dadurch sind gleiche Werte auch gleich im Sinne von equals (z. B. 2/4 und -1/-2).
 */
public record Bruch(long zaehler, long nenner)
{
    public static final Bruch NULL = new Bruch(0, 1);

    private static final String UEBERLAUF = "Die Zahlen sind zu groß für eine exakte Bruchrechnung.";
    private static final double TOLERANZ = 1e-10;
    private static final Pattern GEMISCHT = Pattern.compile("(-?)(\\d+)\\s+(\\d+)\\s*/\\s*(\\d+)");

    public Bruch
    {
        if (nenner == 0)
        {
            throw new IllegalArgumentException("Der Nenner darf nicht 0 sein.");
        }
        if (nenner < 0)
        {
            zaehler = exakt(0, zaehler, Math::subtractExact);
            nenner = exakt(0, nenner, Math::subtractExact);
        }
        long teiler = ggT(zaehler, nenner);
        zaehler /= teiler;
        nenner /= teiler;
    }

    public static Bruch ganz(long wert)
    {
        return new Bruch(wert, 1);
    }

    public Bruch plus(Bruch b)
    {
        // Über den ggT der Nenner erweitern statt stur n1*n2, damit große Nenner seltener überlaufen.
        long g = ggT(nenner, b.nenner);
        long links = exakt(zaehler, b.nenner / g, Math::multiplyExact);
        long rechts = exakt(b.zaehler, nenner / g, Math::multiplyExact);
        return new Bruch(exakt(links, rechts, Math::addExact), exakt(nenner / g, b.nenner, Math::multiplyExact));
    }

    public Bruch minus(Bruch b)
    {
        return plus(b.negiert());
    }

    public Bruch mal(Bruch b)
    {
        // Kreuzweise kürzen vor dem Multiplizieren hält die Zwischenwerte klein.
        long g1 = ggT(zaehler, b.nenner);
        long g2 = ggT(b.zaehler, nenner);
        return new Bruch(exakt(zaehler / g1, b.zaehler / g2, Math::multiplyExact),
                exakt(nenner / g2, b.nenner / g1, Math::multiplyExact));
    }

    public Bruch durch(Bruch b)
    {
        if (b.zaehler == 0)
        {
            throw new ArithmeticException("Division durch 0 ist nicht erlaubt.");
        }
        return mal(new Bruch(b.nenner, b.zaehler));
    }

    public Bruch negiert()
    {
        return new Bruch(exakt(0, zaehler, Math::subtractExact), nenner);
    }

    public double alsDouble()
    {
        return (double) zaehler / nenner;
    }

    public String alsGemischteZahl()
    {
        if (nenner == 1 || Math.abs(zaehler) < nenner)
        {
            return toString();
        }
        String vorzeichen = zaehler < 0 ? "-" : "";
        return vorzeichen + Math.abs(zaehler / nenner) + " " + Math.abs(zaehler % nenner) + "/" + nenner;
    }

    @Override
    public String toString()
    {
        return nenner == 1 ? Long.toString(zaehler) : zaehler + "/" + nenner;
    }

    /**
     * Kettenbruch-Näherung. Liefert nur dann einen Bruch, wenn er bis auf 1e-10 passt,
     * sonst leer (z. B. bei π, wenn maxNenner nicht riesig ist).
     */
    public static Optional<Bruch> ausDezimal(double wert, long maxNenner)
    {
        if (!Double.isFinite(wert) || Math.abs(wert) >= 1e18)
        {
            return Optional.empty();
        }
        long h1 = 1, h2 = 0, k1 = 0, k2 = 1;
        double rest = wert;
        for (int i = 0; i < 64; i++)
        {
            long a = (long) Math.floor(rest);
            long h;
            long k;
            try
            {
                h = Math.addExact(Math.multiplyExact(a, h1), h2);
                k = Math.addExact(Math.multiplyExact(a, k1), k2);
            }
            catch (ArithmeticException e)
            {
                return Optional.empty();
            }
            if (k > maxNenner)
            {
                return Optional.empty();
            }
            if (Math.abs(wert - (double) h / k) < TOLERANZ)
            {
                return Optional.of(new Bruch(h, k));
            }
            double nachkomma = rest - a;
            if (nachkomma == 0)
            {
                return Optional.empty();
            }
            rest = 1 / nachkomma;
            h2 = h1;
            h1 = h;
            k2 = k1;
            k1 = k;
        }
        return Optional.empty();
    }

    /** Versteht „3/4“, „-3/4“, „2 1/3“, „0,75“, „1,5/2“ und „5“. */
    public static Bruch parse(String text)
    {
        String eingabe = text == null ? "" : text.trim();
        if (eingabe.isEmpty())
        {
            throw new IllegalArgumentException("Bitte einen Bruch eingeben.");
        }

        Matcher gemischt = GEMISCHT.matcher(eingabe);
        if (gemischt.matches())
        {
            long nenner = Long.parseLong(gemischt.group(4));
            Bruch betrag = ganz(Long.parseLong(gemischt.group(2))).plus(new Bruch(Long.parseLong(gemischt.group(3)), nenner));
            return gemischt.group(1).isEmpty() ? betrag : betrag.negiert();
        }

        String[] teile = eingabe.split("/", -1);
        if (teile.length == 1)
        {
            return dezimal(teile[0], eingabe);
        }
        if (teile.length == 2)
        {
            Bruch unten = dezimal(teile[1], eingabe);
            if (unten.zaehler == 0)
            {
                throw new IllegalArgumentException("Der Nenner darf nicht 0 sein.");
            }
            return dezimal(teile[0], eingabe).durch(unten);
        }
        throw new IllegalArgumentException("„" + eingabe + "“ ist kein gültiger Bruch.");
    }

    private static Bruch dezimal(String teil, String eingabe)
    {
        BigDecimal zahl;
        try
        {
            zahl = new BigDecimal(teil.trim().replace(',', '.')).stripTrailingZeros();
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("„" + eingabe + "“ ist kein gültiger Bruch.");
        }
        try
        {
            if (zahl.scale() <= 0)
            {
                return ganz(zahl.toBigIntegerExact().longValueExact());
            }
            return new Bruch(zahl.unscaledValue().longValueExact(), BigInteger.TEN.pow(zahl.scale()).longValueExact());
        }
        catch (ArithmeticException e)
        {
            throw new ArithmeticException(UEBERLAUF);
        }
    }

    private static long ggT(long a, long b)
    {
        while (b != 0)
        {
            long t = a % b;
            a = b;
            b = t;
        }
        return Math.abs(a);
    }

    private static long exakt(long a, long b, LongBinaryOperator operation)
    {
        try
        {
            return operation.applyAsLong(a, b);
        }
        catch (ArithmeticException e)
        {
            throw new ArithmeticException(UEBERLAUF);
        }
    }
}
