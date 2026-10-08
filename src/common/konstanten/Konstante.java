package common.konstanten;

import java.math.BigDecimal;

public record Konstante(String name, String symbol, double wert, String einheit, KonstantenKategorie kategorie, String beschreibung)
{
    /**
     * Wert als Text, den der Ausdrucksparser versteht: Dezimalkomma, wissenschaftliche Schreibweise mit kleinem „e“
     * (z. B. {@code 6,62607015e-34}). Negative Werte stehen in Klammern, damit sie nach einem Operator gültig bleiben.
     */
    public String alsEingabe()
    {
        BigDecimal zahl = BigDecimal.valueOf(wert).stripTrailingZeros();
        double betrag = Math.abs(wert);
        boolean ohneExponent = betrag == 0 || (betrag >= 1e-4 && betrag < 1e15);
        String text = (ohneExponent ? zahl.toPlainString() : zahl.toString())
                .replace('.', ',')
                .replace("E+", "e")
                .replace('E', 'e');
        return wert < 0 ? "(" + text + ")" : text;
    }
}
