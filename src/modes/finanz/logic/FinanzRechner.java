package modes.finanz.logic;

import modes.finanz.model.FinanzErgebnis;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static modes.finanz.formatting.GeldFormat.euro;
import static modes.finanz.formatting.GeldFormat.prozent;
import static modes.finanz.formatting.GeldFormat.zahl;

/** Alltags-Finanzrechnungen mit BigDecimal; Geldbeträge werden kaufmännisch auf Cent gerundet. */
public final class FinanzRechner
{
    private static final BigDecimal HUNDERT = BigDecimal.valueOf(100);
    private static final BigDecimal ZWOELF = BigDecimal.valueOf(12);
    private static final MathContext GENAU = MathContext.DECIMAL64;

    private FinanzRechner()
    {
    }

    public static BigDecimal runde(BigDecimal wert)
    {
        return wert.setScale(2, RoundingMode.HALF_UP);
    }

    public static FinanzErgebnis prozentwert(BigDecimal prozent, BigDecimal grundwert)
    {
        BigDecimal faktor = prozent.divide(HUNDERT, GENAU);
        BigDecimal wert = runde(grundwert.multiply(faktor));
        return new FinanzErgebnis(wert, euro(wert), List.of(
                prozent(prozent) + " von " + euro(grundwert) + " = " + euro(grundwert) + " × " + zahl(faktor)
                        + " = " + euro(wert)));
    }

    public static FinanzErgebnis prozentsatz(BigDecimal prozentwert, BigDecimal grundwert)
    {
        pruefe(grundwert.signum() != 0, "Der Grundwert darf nicht 0 sein.");
        BigDecimal satz = runde(prozentwert.divide(grundwert, GENAU).multiply(HUNDERT));
        return new FinanzErgebnis(satz, prozent(satz), List.of(
                euro(prozentwert) + " ÷ " + euro(grundwert) + " × 100 = " + prozent(satz)));
    }

    public static FinanzErgebnis grundwert(BigDecimal prozentwert, BigDecimal prozent)
    {
        pruefe(prozent.signum() != 0, "Der Prozentsatz darf nicht 0 sein.");
        BigDecimal faktor = prozent.divide(HUNDERT, GENAU);
        BigDecimal wert = runde(prozentwert.divide(faktor, GENAU));
        return new FinanzErgebnis(wert, euro(wert), List.of(
                euro(prozentwert) + " sind " + prozent(prozent) + " → " + euro(prozentwert) + " ÷ " + zahl(faktor)
                        + " = " + euro(wert)));
    }

    public static FinanzErgebnis veraenderung(BigDecimal alt, BigDecimal neu)
    {
        pruefe(alt.signum() != 0, "Der Ausgangswert darf nicht 0 sein.");
        BigDecimal differenz = neu.subtract(alt);
        BigDecimal satz = runde(differenz.divide(alt.abs(), GENAU).multiply(HUNDERT));
        String vorzeichen = satz.signum() > 0 ? "+" : "";
        return new FinanzErgebnis(satz, vorzeichen + prozent(satz), List.of(
                "Differenz: " + euro(neu) + " − " + euro(alt) + " = " + euro(differenz),
                euro(differenz) + " ÷ " + euro(alt.abs()) + " × 100 = " + vorzeichen + prozent(satz)));
    }

    public static FinanzErgebnis bruttoAusNetto(BigDecimal netto, BigDecimal steuersatz)
    {
        pruefeNichtNegativ(netto, "Der Nettobetrag");
        pruefeNichtNegativ(steuersatz, "Der Steuersatz");
        BigDecimal basis = runde(netto);
        BigDecimal steuer = runde(basis.multiply(steuersatz).divide(HUNDERT, GENAU));
        BigDecimal brutto = basis.add(steuer);
        return new FinanzErgebnis(brutto, euro(brutto), List.of(
                "MwSt: " + euro(basis) + " × " + prozent(steuersatz) + " = " + euro(steuer),
                "Brutto: " + euro(basis) + " + " + euro(steuer) + " = " + euro(brutto)));
    }

    public static FinanzErgebnis nettoAusBrutto(BigDecimal brutto, BigDecimal steuersatz)
    {
        pruefeNichtNegativ(brutto, "Der Bruttobetrag");
        pruefeNichtNegativ(steuersatz, "Der Steuersatz");
        BigDecimal basis = runde(brutto);
        BigDecimal faktor = BigDecimal.ONE.add(steuersatz.divide(HUNDERT, GENAU));
        BigDecimal netto = runde(basis.divide(faktor, GENAU));
        BigDecimal steuer = basis.subtract(netto);
        return new FinanzErgebnis(netto, euro(netto), List.of(
                "Netto: " + euro(basis) + " ÷ " + zahl(faktor) + " = " + euro(netto),
                "Enthaltene MwSt: " + euro(basis) + " − " + euro(netto) + " = " + euro(steuer)));
    }

    public static FinanzErgebnis rabatt(BigDecimal preis, BigDecimal prozent)
    {
        pruefeNichtNegativ(preis, "Der Preis");
        pruefe(prozent.signum() >= 0 && prozent.compareTo(HUNDERT) <= 0, "Der Rabatt muss zwischen 0 und 100 % liegen.");
        BigDecimal basis = runde(preis);
        BigDecimal nachlass = runde(basis.multiply(prozent).divide(HUNDERT, GENAU));
        BigDecimal neuerPreis = basis.subtract(nachlass);
        return new FinanzErgebnis(neuerPreis, euro(neuerPreis), List.of(
                "Rabatt: " + euro(basis) + " × " + prozent(prozent) + " = " + euro(nachlass),
                "Neuer Preis: " + euro(basis) + " − " + euro(nachlass) + " = " + euro(neuerPreis)));
    }

    public static FinanzErgebnis trinkgeld(BigDecimal betrag, BigDecimal prozent, BigDecimal personen)
    {
        pruefeNichtNegativ(betrag, "Der Rechnungsbetrag");
        pruefeNichtNegativ(prozent, "Das Trinkgeld");
        int anzahl = ganzzahl(personen, "Die Anzahl der Personen");
        pruefe(anzahl >= 1, "Es muss mindestens eine Person bezahlen.");

        BigDecimal basis = runde(betrag);
        BigDecimal trinkgeld = runde(basis.multiply(prozent).divide(HUNDERT, GENAU));
        BigDecimal gesamt = basis.add(trinkgeld);
        List<String> weg = new ArrayList<>(List.of(
                "Trinkgeld: " + euro(basis) + " × " + prozent(prozent) + " = " + euro(trinkgeld),
                "Gesamt: " + euro(basis) + " + " + euro(trinkgeld) + " = " + euro(gesamt)));
        if (anzahl == 1)
        {
            return new FinanzErgebnis(gesamt, euro(gesamt), weg);
        }

        BigDecimal proPerson = runde(gesamt.divide(BigDecimal.valueOf(anzahl), GENAU));
        weg.add("Pro Person: " + euro(gesamt) + " ÷ " + anzahl + " = " + euro(proPerson));
        return new FinanzErgebnis(proPerson, euro(proPerson) + " pro Person", weg);
    }

    public static FinanzErgebnis einfacheZinsen(BigDecimal kapital, BigDecimal zinssatz, BigDecimal jahre)
    {
        pruefeZinsEingaben(kapital, zinssatz, jahre);
        BigDecimal basis = runde(kapital);
        BigDecimal zinsen = runde(basis.multiply(zinssatz).multiply(jahre).divide(HUNDERT, GENAU));
        BigDecimal endbetrag = basis.add(zinsen);
        return new FinanzErgebnis(endbetrag, euro(endbetrag), List.of(
                "Zinsen: " + euro(basis) + " × " + prozent(zinssatz) + " × " + zahl(jahre) + " Jahre = " + euro(zinsen),
                "Endbetrag: " + euro(basis) + " + " + euro(zinsen) + " = " + euro(endbetrag)));
    }

    /** @param periodenProJahr 1 für jährliche, 12 für monatliche Verzinsung */
    public static FinanzErgebnis zinseszins(BigDecimal kapital, BigDecimal zinssatz, BigDecimal jahre, int periodenProJahr)
    {
        pruefeZinsEingaben(kapital, zinssatz, jahre);
        pruefe(periodenProJahr >= 1, "Es muss mindestens eine Zinsperiode pro Jahr geben.");
        int perioden = ganzzahl(jahre.multiply(BigDecimal.valueOf(periodenProJahr)),
                periodenProJahr == 12 ? "Die Laufzeit in Monaten" : "Die Laufzeit in Jahren");

        BigDecimal basis = runde(kapital);
        BigDecimal periodenZins = zinssatz.divide(HUNDERT.multiply(BigDecimal.valueOf(periodenProJahr)), GENAU);
        BigDecimal zinsfaktor = BigDecimal.ONE.add(periodenZins);
        BigDecimal endbetrag = runde(basis.multiply(zinsfaktor.pow(perioden, GENAU)));
        BigDecimal zinsen = endbetrag.subtract(basis);
        String faktorText = periodenProJahr == 1
                ? "Zinsfaktor: 1 + " + prozent(zinssatz) + " = " + zahl(zinsfaktor)
                : "Zinsfaktor pro Periode: 1 + " + prozent(zinssatz) + " ÷ " + periodenProJahr + " ≈ " + zahl(zinsfaktor);
        return new FinanzErgebnis(endbetrag, euro(endbetrag), List.of(
                faktorText,
                "Endbetrag: " + euro(basis) + " × " + zahl(zinsfaktor) + "^" + perioden + " = " + euro(endbetrag),
                "Zinsen: " + euro(endbetrag) + " − " + euro(basis) + " = " + euro(zinsen)));
    }

    /** Annuitätendarlehen: gleichbleibende Monatsrate aus Zins und Tilgung. */
    public static FinanzErgebnis kreditrate(BigDecimal kreditsumme, BigDecimal zinssatz, BigDecimal laufzeitMonate)
    {
        pruefe(kreditsumme.signum() > 0, "Die Kreditsumme muss größer als 0 sein.");
        pruefeNichtNegativ(zinssatz, "Der Zinssatz");
        int monate = ganzzahl(laufzeitMonate, "Die Laufzeit in Monaten");
        pruefe(monate >= 1, "Die Laufzeit muss mindestens einen Monat betragen.");

        BigDecimal summe = runde(kreditsumme);
        BigDecimal anzahl = BigDecimal.valueOf(monate);
        BigDecimal monatsZins = zinssatz.divide(HUNDERT.multiply(ZWOELF), GENAU);
        BigDecimal rate;
        String rateText;
        if (monatsZins.signum() == 0)
        {
            rate = runde(summe.divide(anzahl, GENAU));
            rateText = "Ohne Zinsen: " + euro(summe) + " ÷ " + monate + " Monate = " + euro(rate);
        }
        else
        {
            BigDecimal abzinsung = BigDecimal.ONE.divide(BigDecimal.ONE.add(monatsZins).pow(monate, GENAU), GENAU);
            rate = runde(summe.multiply(monatsZins).divide(BigDecimal.ONE.subtract(abzinsung), GENAU));
            rateText = "Rate: " + euro(summe) + " × i ÷ (1 − (1 + i)^−" + monate + ") mit i = "
                    + prozent(zinssatz) + " ÷ 12 = " + euro(rate);
        }

        // ponytail: kein Cent-Ausgleich in der letzten Rate; Gesamtsumme = Rate × Monate, kann um Cents abweichen.
        BigDecimal gesamt = rate.multiply(anzahl);
        BigDecimal zinsen = gesamt.subtract(summe);
        return new FinanzErgebnis(rate, euro(rate) + " pro Monat", List.of(
                rateText,
                "Gesamt: " + euro(rate) + " × " + monate + " = " + euro(gesamt),
                "Zinsen gesamt: " + euro(gesamt) + " − " + euro(summe) + " = " + euro(zinsen)));
    }

    private static void pruefeZinsEingaben(BigDecimal kapital, BigDecimal zinssatz, BigDecimal jahre)
    {
        pruefeNichtNegativ(kapital, "Das Kapital");
        pruefeNichtNegativ(zinssatz, "Der Zinssatz");
        pruefe(jahre.signum() > 0, "Die Laufzeit muss größer als 0 sein.");
    }

    private static void pruefeNichtNegativ(BigDecimal wert, String name)
    {
        pruefe(wert.signum() >= 0, name + " darf nicht negativ sein.");
    }

    // Obergrenze schützt vor riesigen Potenzen (z. B. 10^9 Monate), die BigDecimal ewig rechnen ließen.
    private static int ganzzahl(BigDecimal wert, String name)
    {
        BigDecimal ohneNullen = wert.stripTrailingZeros();
        pruefe(ohneNullen.scale() <= 0, name + " muss eine ganze Zahl sein.");
        pruefe(ohneNullen.compareTo(BigDecimal.valueOf(100_000)) <= 0, name + " ist zu groß.");
        return ohneNullen.intValueExact();
    }

    private static void pruefe(boolean bedingung, String meldung)
    {
        if (!bedingung)
        {
            throw new IllegalArgumentException(meldung);
        }
    }
}
