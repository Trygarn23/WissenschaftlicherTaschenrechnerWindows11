package modes.programmierer.logic;

import modes.programmierer.model.Ieee754Darstellung;
import modes.programmierer.model.Ieee754Darstellung.Art;

/** Zerlegt eine Zahl in ihr IEEE-754-Bitmuster als float (1/8/23) und double (1/11/52). */
public final class Ieee754Zerlegung
{
    private Ieee754Zerlegung()
    {
    }

    public static Ieee754Darstellung alsFloat(double wert)
    {
        float f = (float) wert;
        long bits = Float.floatToRawIntBits(f) & 0xFFFFFFFFL;
        return zerlege("float", f, bits, 8, 23);
    }

    public static Ieee754Darstellung alsDouble(double wert)
    {
        return zerlege("double", wert, Double.doubleToRawLongBits(wert), 11, 52);
    }

    private static Ieee754Darstellung zerlege(String name, double gespeichert, long bits, int exponentAnzahl, int mantisseAnzahl)
    {
        int vorzeichen = (int) ((bits >>> (exponentAnzahl + mantisseAnzahl)) & 1);
        int exponentMax = (1 << exponentAnzahl) - 1;
        int exponentRoh = (int) ((bits >>> mantisseAnzahl) & exponentMax);
        long mantisse = bits & ((1L << mantisseAnzahl) - 1);
        int bias = (1 << (exponentAnzahl - 1)) - 1;

        Art art;
        if (exponentRoh == exponentMax)
        {
            art = mantisse == 0 ? Art.UNENDLICH : Art.NAN;
        }
        else if (exponentRoh == 0)
        {
            art = mantisse == 0 ? Art.NULL : Art.SUBNORMAL;
        }
        else
        {
            art = Art.NORMAL;
        }

        return new Ieee754Darstellung(name, gespeichert, vorzeichen,
                binaer(exponentRoh, exponentAnzahl), binaer(mantisse, mantisseAnzahl),
                exponentRoh, bias, art);
    }

    private static String binaer(long wert, int stellen)
    {
        String text = Long.toBinaryString(wert);
        return "0".repeat(stellen - text.length()) + text;
    }
}
