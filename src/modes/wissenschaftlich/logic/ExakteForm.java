package modes.wissenschaftlich.logic;

import modes.bruch.model.Bruch;

import java.util.Optional;

/**
 * Sucht zu einer Dezimalzahl eine einfache exakte Form für die S↔D-Taste:
 * Bruch mit kleinem Nenner (0,75 → 3/4), Wurzel (0,7071… → √2/2) oder Vielfaches von π (2,356… → 3π/4).
 */
public final class ExakteForm
{
    private static final long MAX_NENNER = 1000;
    private static final long MAX_NENNER_WURZEL = 100;
    private static final long MAX_NENNER_PI = 12;
    private static final long MAX_QUADRAT_ZAEHLER = 1_000_000;
    private static final long MAX_RADIKAND = 1000;

    private ExakteForm()
    {
    }

    /** Leer, wenn es keine einfache exakte Form gibt (z. B. bei e oder 1,23456789). */
    public static Optional<String> finde(double wert)
    {
        Optional<Bruch> bruch = Bruch.ausDezimal(wert, MAX_NENNER);
        if (bruch.isPresent())
        {
            return Optional.of(bruch.get().toString());
        }
        return wurzelForm(wert).or(() -> piForm(wert));
    }

    /** wert² ist ein Bruch z/m, also |wert| = √(z·m)/m; Quadratfaktoren werden aus der Wurzel gezogen. */
    private static Optional<String> wurzelForm(double wert)
    {
        Optional<Bruch> quadrat = Bruch.ausDezimal(wert * wert, MAX_NENNER_WURZEL);
        if (quadrat.isEmpty() || quadrat.get().zaehler() > MAX_QUADRAT_ZAEHLER)
        {
            return Optional.empty();
        }

        long radikand = quadrat.get().zaehler() * quadrat.get().nenner();
        long aussen = 1;
        for (long i = 2; i * i <= radikand; i++)
        {
            while (radikand % (i * i) == 0)
            {
                radikand /= i * i;
                aussen *= i;
            }
        }
        if (radikand <= 1 || radikand > MAX_RADIKAND)
        {
            return Optional.empty();
        }

        Bruch faktor = new Bruch(aussen, quadrat.get().nenner());
        return Optional.of(mitFaktor(wert < 0, faktor, "√" + radikand));
    }

    private static Optional<String> piForm(double wert)
    {
        return Bruch.ausDezimal(wert / Math.PI, MAX_NENNER_PI)
                .filter(faktor -> faktor.zaehler() != 0)
                .map(faktor -> mitFaktor(faktor.zaehler() < 0, new Bruch(Math.abs(faktor.zaehler()), faktor.nenner()), "π"));
    }

    /** Baut z. B. „-3√2/4“ aus Vorzeichen, Faktor 3/4 und „√2“; Faktor 1 wird weggelassen. */
    private static String mitFaktor(boolean negativ, Bruch faktor, String symbol)
    {
        String zaehler = faktor.zaehler() == 1 ? "" : Long.toString(faktor.zaehler());
        String nenner = faktor.nenner() == 1 ? "" : "/" + faktor.nenner();
        return (negativ ? "-" : "") + zaehler + symbol + nenner;
    }
}
