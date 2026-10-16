package common.logic;

import common.formatting.ZahlenFormatter;
import common.state.RechnerZustand;

import java.math.BigInteger;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;

/** Rechnet die letzte Zahl im Ausdruck sofort um (%, n!) und ersetzt sie durch das Ergebnis. */
final class LetzteZahlBearbeitung
{
    private static final String FEHLER_TEXT = "Fehler";

    private final RechnerZustand zustand;
    private final ZahlenFormatter zahlenFormatierer;

    LetzteZahlBearbeitung(RechnerZustand zustand, ZahlenFormatter zahlenFormatierer)
    {
        this.zustand = zustand;
        this.zahlenFormatierer = zahlenFormatierer;
    }

    String prozent()
    {
        return wendeAufLetzteZahlAn(x -> x / 100.0, x -> true);
    }

    String fakultaet()
    {
        if (!kannLetzteZahlBearbeiten()) return zustand.getAusdruckText();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert < 0 || wert != Math.floor(wert)) return fehler();

        int n = (int) wert;
        if (n > 5000) return fehler();

        BigInteger ergebnis = BigInteger.ONE;
        for (int i = 2; i <= n; i++)
        {
            ergebnis = ergebnis.multiply(BigInteger.valueOf(i));
        }

        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(ergebnis.toString());
        zustand.setGleichGedrueckt(false);
        return ergebnis.toString();
    }

    private String wendeAufLetzteZahlAn(DoubleUnaryOperator operation, DoublePredicate eingabeGueltig)
    {
        if (!kannLetzteZahlBearbeiten()) return zustand.getAusdruckText();

        int start = startLetzteZahl();
        double eingabe = letzteZahlAlsDouble();

        if (!eingabeGueltig.test(eingabe)) return fehler();

        double ergebnis = operation.applyAsDouble(eingabe);
        if (!Double.isFinite(ergebnis)) return fehler();

        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(zahlenFormatierer.interneDarstellung(ergebnis));
        zustand.setGleichGedrueckt(false);
        return zahlenFormatierer.formatiereZahl(ergebnis);
    }

    private boolean kannLetzteZahlBearbeiten()
    {
        if (AusdruckEditor.endetMitOperatorOderKlammerAuf(zustand)) return false;
        return AusdruckEditor.letztesZeichen(zustand) != ')';
    }

    private int startLetzteZahl()
    {
        return AusdruckTermFinder.startLetzteZahl(zustand.getAusdruckText());
    }

    private double letzteZahlAlsDouble()
    {
        String zahl = zustand.getAusdruckTeilText(startLetzteZahl()).replace(',', '.');
        return Double.parseDouble(zahl);
    }

    private String fehler()
    {
        zustand.clearAusdruck();
        zustand.clearVerlauf();
        zustand.setGleichGedrueckt(true);
        return FEHLER_TEXT;
    }
}
