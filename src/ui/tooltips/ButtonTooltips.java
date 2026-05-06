package ui.tooltips;

import javax.swing.JButton;
import java.util.Map;

public final class ButtonTooltips
{
    private static final Map<String, String> DESCRIPTIONS = Map.ofEntries(
            Map.entry("%", "Wandelt die letzte Zahl in Prozent um"),
            Map.entry("CE", "Löscht die aktuelle Eingabe"),
            Map.entry("C", "Löscht die gesamte Eingabe"),
            Map.entry("←", "Löscht das letzte Zeichen"),
            Map.entry("1/x", "Bildet den Kehrwert der letzten Eingabe"),
            Map.entry("x²", "Quadriert den letzten Term"),
            Map.entry("√x", "Zieht die Quadratwurzel des letzten Terms"),
            Map.entry("÷", "Division"),
            Map.entry("×", "Multiplikation"),
            Map.entry("-", "Subtraktion oder negatives Vorzeichen"),
            Map.entry("+", "Addition"),
            Map.entry("±", "Wechselt das Vorzeichen"),
            Map.entry(",", "Fügt ein Dezimalkomma ein"),
            Map.entry("=", "Berechnet das Ergebnis"),
            Map.entry("(", "Öffnet eine Klammer"),
            Map.entry(")", "Schließt eine Klammer"),
            Map.entry("xʸ", "Fügt eine Potenz ein"),
            Map.entry("mod", "Modulo: Rest einer Division"),
            Map.entry("Ans", "Fügt das letzte Ergebnis ein"),
            Map.entry("MC", "Löscht den Speicher"),
            Map.entry("MR", "Ruft den Speicherwert ab"),
            Map.entry("M+", "Addiert den aktuellen Wert zum Speicher"),
            Map.entry("M-", "Subtrahiert den aktuellen Wert vom Speicher"),
            Map.entry("π", "Fügt die Kreiszahl Pi ein"),
            Map.entry("e", "Fügt die Eulersche Zahl ein"),
            Map.entry("sin", "Sinusfunktion"),
            Map.entry("cos", "Kosinusfunktion"),
            Map.entry("tan", "Tangensfunktion"),
            Map.entry("asin", "Arkussinusfunktion"),
            Map.entry("acos", "Arkuskosinusfunktion"),
            Map.entry("atan", "Arkustangensfunktion"),
            Map.entry("sinh", "Hyperbolischer Sinus"),
            Map.entry("cosh", "Hyperbolischer Kosinus"),
            Map.entry("tanh", "Hyperbolischer Tangens"),
            Map.entry("ln", "Natürlicher Logarithmus"),
            Map.entry("log", "Logarithmus zur Basis 10"),
            Map.entry("10ˣ", "Zehn hoch letzter Term"),
            Map.entry("|x|", "Betrag des letzten Terms"),
            Map.entry("floor", "Rundet ab"),
            Map.entry("ceil", "Rundet auf"),
            Map.entry("round", "Rundet kaufmännisch"),
            Map.entry("rand", "Fügt eine Zufallszahl zwischen 0 und 1 ein"),
            Map.entry("f(x)", "Öffnet weitere trigonometrische Funktionen")
    );

    private static final Map<String, String> SHORTCUTS = Map.ofEntries(
            Map.entry("0", "0 oder Num 0"),
            Map.entry("1", "1 oder Num 1"),
            Map.entry("2", "2 oder Num 2"),
            Map.entry("3", "3 oder Num 3"),
            Map.entry("4", "4 oder Num 4"),
            Map.entry("5", "5 oder Num 5"),
            Map.entry("6", "6 oder Num 6"),
            Map.entry("7", "7 oder Num 7"),
            Map.entry("8", "8 oder Num 8"),
            Map.entry("9", "9 oder Num 9"),
            Map.entry(",", ", oder ."),
            Map.entry("+", "+ oder Num +"),
            Map.entry("-", "- oder Num -"),
            Map.entry("×", "* oder Num *"),
            Map.entry("÷", "/ oder Num /"),
            Map.entry("mod", "% oder P"),
            Map.entry("=", "Enter"),
            Map.entry("←", "Backspace")
    );

    private ButtonTooltips()
    {
    }

    public static void apply(JButton button, String key)
    {
        button.setToolTipText(textFor(key));
    }

    public static String textFor(String key)
    {
        if (key == null || key.isBlank())
        {
            return null;
        }

        String description = key.matches("\\d")
                ? "Gibt die Ziffer " + key + " ein"
                : DESCRIPTIONS.get(key);

        if (description == null)
        {
            return null;
        }

        String shortcut = SHORTCUTS.get(key);
        if (shortcut == null)
        {
            return description;
        }

        return description + " (Taste: " + shortcut + ")";
    }
}
