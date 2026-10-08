package modes.gleichung.logic;

import common.formatting.ZahlenAnzeige;
import common.parser.AusdruckParser;
import common.parser.AusdruckParserException;
import common.state.WinkelModus;
import modes.gleichung.model.GleichungsErgebnis;
import modes.gleichung.model.GleichungsErgebnis.Art;
import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.model.KomplexeZahl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Löst ax² + bx + c = 0 (bei a = 0 linear) – bewusst kein CAS. */
public class GleichungsLoeser
{
    public static final String NUR_GRAD_ZWEI = "Nur lineare und quadratische Gleichungen werden unterstützt.";

    // Rundungsrauschen aus der Auswertung bzw. aus b² − 4ac relativ zur Größenordnung wegschneiden.
    private static final double RELATIVE_TOLERANZ = 1e-10;
    private static final double[] PRUEFSTELLEN = {0.5, 2.7};

    private final KomplexFormatter komplexFormatter = new KomplexFormatter();

    public GleichungsErgebnis loese(double a, double b, double c)
    {
        if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(c))
        {
            throw new IllegalArgumentException("Die Koeffizienten müssen endliche Zahlen sein.");
        }
        return a == 0.0 ? loeseLinear(b, c) : loeseQuadratisch(a, b, c);
    }

    /** Freitext „links = rechts“ mit Variable x; ohne „=“ wird „… = 0“ angenommen. */
    public GleichungsErgebnis loese(String gleichung)
    {
        if (gleichung == null || gleichung.isBlank())
        {
            throw new IllegalArgumentException("Bitte eine Gleichung eingeben.");
        }

        String[] seiten = gleichung.split("=", -1);
        if (seiten.length > 2 || seiten[0].isBlank() || (seiten.length == 2 && seiten[1].isBlank()))
        {
            throw new IllegalArgumentException("Bitte genau ein „=“ mit zwei Seiten verwenden.");
        }
        String funktion = seiten.length == 2 ? "(" + seiten[0] + ")-(" + seiten[1] + ")" : seiten[0];

        // f an -1, 0, 1 auswerten, daraus a, b, c bestimmen und an zwei weiteren Stellen gegenprüfen.
        double fMinus = werteAus(funktion, -1.0);
        double fNull = werteAus(funktion, 0.0);
        double fPlus = werteAus(funktion, 1.0);
        double skala = Math.max(1.0, Math.max(Math.abs(fNull), Math.max(Math.abs(fMinus), Math.abs(fPlus))));
        double a = bereinige((fPlus + fMinus - 2.0 * fNull) / 2.0, skala);
        double b = bereinige((fPlus - fMinus) / 2.0, skala);
        double c = bereinige(fNull, skala);

        for (double x : PRUEFSTELLEN)
        {
            double erwartet = a * x * x + b * x + c;
            double toleranz = RELATIVE_TOLERANZ * Math.max(skala, Math.abs(a * x * x) + Math.abs(b * x) + Math.abs(c));
            if (Math.abs(werteAus(funktion, x) - erwartet) > toleranz)
            {
                throw new IllegalArgumentException(NUR_GRAD_ZWEI);
            }
        }

        GleichungsErgebnis ergebnis = loese(a, b, c);
        List<String> rechenweg = new ArrayList<>();
        rechenweg.add("Umgeformt: " + formatierePolynom(a, b, c) + " = 0");
        rechenweg.addAll(ergebnis.rechenweg());
        return new GleichungsErgebnis(ergebnis.art(), ergebnis.loesungen(), ergebnis.anzeige(), rechenweg);
    }

    private GleichungsErgebnis loeseLinear(double b, double c)
    {
        if (b == 0.0)
        {
            if (c == 0.0)
            {
                return new GleichungsErgebnis(Art.UNENDLICH, List.of(), "Unendlich viele Lösungen",
                        List.of("Übrig bleibt 0 = 0 – das stimmt für jedes x."));
            }
            return new GleichungsErgebnis(Art.KEINE_LOESUNG, List.of(), "Keine Lösung",
                    List.of("Übrig bleibt " + zahl(c) + " = 0 – das stimmt für kein x."));
        }

        double x = -c / b;
        return new GleichungsErgebnis(Art.REELL, List.of(new KomplexeZahl(x, 0.0)), "x = " + zahl(x),
                List.of("Lineare Gleichung: bx + c = 0", "x = −c / b = " + zahl(x)));
    }

    private GleichungsErgebnis loeseQuadratisch(double a, double b, double c)
    {
        double diskriminante = b * b - 4.0 * a * c;
        if (Math.abs(diskriminante) <= RELATIVE_TOLERANZ * Math.max(b * b, Math.abs(4.0 * a * c)))
        {
            diskriminante = 0.0;
        }
        String dZeile = "D = b² − 4ac = " + zahl(diskriminante);

        if (diskriminante == 0.0)
        {
            double x = -b / (2.0 * a);
            return new GleichungsErgebnis(Art.REELL, List.of(new KomplexeZahl(x, 0.0)), "x = " + zahl(x),
                    List.of(dZeile, "D = 0: doppelte Lösung, x = −b / 2a"));
        }

        if (diskriminante > 0.0)
        {
            // Stabile Variante: vermeidet Auslöschung, wenn b² viel größer als 4ac ist.
            double q = -(b + Math.copySign(Math.sqrt(diskriminante), b)) / 2.0;
            double x1 = Math.min(q / a, c / q);
            double x2 = Math.max(q / a, c / q);
            return new GleichungsErgebnis(Art.REELL,
                    List.of(new KomplexeZahl(x1, 0.0), new KomplexeZahl(x2, 0.0)),
                    "x₁ = " + zahl(x1) + ",  x₂ = " + zahl(x2),
                    List.of(dZeile, "D > 0: zwei reelle Lösungen, x = (−b ± √D) / 2a"));
        }

        double real = -b / (2.0 * a);
        double imaginaer = Math.sqrt(-diskriminante) / (2.0 * Math.abs(a));
        KomplexeZahl x1 = new KomplexeZahl(real, imaginaer);
        KomplexeZahl x2 = x1.konjugiert();
        return new GleichungsErgebnis(Art.KOMPLEX, List.of(x1, x2),
                "x₁ = " + komplexFormatter.formatiereKartesisch(x1) + ",  x₂ = " + komplexFormatter.formatiereKartesisch(x2),
                List.of(dZeile, "D < 0: keine reelle Lösung, komplex x = (−b ± i·√−D) / 2a"));
    }

    private double werteAus(String funktion, double x)
    {
        double y;
        try
        {
            y = AusdruckParser.auswerten(funktion, 0.0, WinkelModus.RAD, Map.of("x", x));
        }
        catch (AusdruckParserException e)
        {
            throw switch (e.getFehler())
            {
                case SYNTAX, KLAMMERN_UNAUSGEGLICHEN -> new IllegalArgumentException("Die Gleichung ist ungültig – bitte Schreibweise prüfen.");
                case UNBEKANNTE_FUNKTION -> new IllegalArgumentException("Unbekannter Name – als Variable ist nur x erlaubt.");
                // Division durch 0 oder Wurzel aus Negativem an einer Stützstelle: kein Polynom.
                default -> new IllegalArgumentException(NUR_GRAD_ZWEI);
            };
        }
        if (!Double.isFinite(y))
        {
            throw new IllegalArgumentException(NUR_GRAD_ZWEI);
        }
        return y;
    }

    private static double bereinige(double wert, double skala)
    {
        return Math.abs(wert) <= RELATIVE_TOLERANZ * skala ? 0.0 : wert;
    }

    private static String formatierePolynom(double a, double b, double c)
    {
        StringBuilder text = new StringBuilder();
        haengeTermAn(text, a, "x²");
        haengeTermAn(text, b, "x");
        haengeTermAn(text, c, "");
        return text.isEmpty() ? "0" : text.toString();
    }

    private static void haengeTermAn(StringBuilder text, double koeffizient, String variable)
    {
        if (koeffizient == 0.0)
        {
            return;
        }
        if (!text.isEmpty())
        {
            text.append(koeffizient < 0 ? " − " : " + ");
        }
        else if (koeffizient < 0)
        {
            text.append("−");
        }
        double betrag = Math.abs(koeffizient);
        if (betrag != 1.0 || variable.isEmpty())
        {
            text.append(zahl(betrag));
        }
        text.append(variable);
    }

    private static String zahl(double wert)
    {
        return ZahlenAnzeige.formatiere(wert);
    }
}
