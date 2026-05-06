package ui.tooltips;

import ui.shortcuts.KeyboardShortcutText;

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
            Map.entry("mod", "Berechnet den Rest einer Division"),
            Map.entry("Ans", "Fügt das letzte berechnete Ergebnis ein"),
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
            Map.entry("10ˣ", "Berechnet 10 hoch dem letzten Term"),
            Map.entry("|x|", "Betrag des letzten Terms"),
            Map.entry("floor", "Rundet ab"),
            Map.entry("ceil", "Rundet auf"),
            Map.entry("round", "Rundet kaufmännisch"),
            Map.entry("rand", "Fügt eine Zufallszahl im Bereich 0 <= x < 1 ein"),
            Map.entry("f(x)", "Öffnet weitere trigonometrische Funktionen"),
            Map.entry("BIN", "Wechselt zur Binärdarstellung"),
            Map.entry("OCT", "Wechselt zur Oktaldarstellung"),
            Map.entry("DEC", "Wechselt zur Dezimaldarstellung"),
            Map.entry("HEX", "Wechselt zur Hexadezimaldarstellung"),
            Map.entry("BYTE", "Setzt die Wortbreite auf 8 Bit"),
            Map.entry("WORD", "Setzt die Wortbreite auf 16 Bit"),
            Map.entry("DWORD", "Setzt die Wortbreite auf 32 Bit"),
            Map.entry("QWORD", "Setzt die Wortbreite auf 64 Bit"),
            Map.entry("A", "Gibt die Hexadezimalziffer A ein"),
            Map.entry("B", "Gibt die Hexadezimalziffer B ein"),
            Map.entry("PRG:C", "Gibt die Hexadezimalziffer C ein"),
            Map.entry("D", "Gibt die Hexadezimalziffer D ein"),
            Map.entry("E", "Gibt die Hexadezimalziffer E ein"),
            Map.entry("F", "Gibt die Hexadezimalziffer F ein"),
            Map.entry("NOT", "Invertiert alle Bits innerhalb der aktuellen Wortbreite"),
            Map.entry("AND", "Verknüpft zwei Werte bitweise mit AND"),
            Map.entry("OR", "Verknüpft zwei Werte bitweise mit OR"),
            Map.entry("XOR", "Verknüpft zwei Werte bitweise mit XOR"),
            Map.entry("<<", "Verschiebt die Bits um eine Stelle nach links"),
            Map.entry(">>", "Verschiebt die Bits arithmetisch nach rechts"),
            Map.entry(">>>", "Verschiebt die Bits logisch nach rechts"),
            Map.entry("CLR", "Löscht Wert und ausstehende Programmierer-Operation"),
            Map.entry("SIGNED", "Schaltet zwischen vorzeichenbehafteter und vorzeichenloser Darstellung um"),
            Map.entry("UNSIGNED", "Schaltet zwischen vorzeichenloser und vorzeichenbehafteter Darstellung um")
    );

    private static final Map<String, String> SHORTCUTS = Map.ofEntries(
            Map.entry("0", KeyboardShortcutText.digit("0")),
            Map.entry("1", KeyboardShortcutText.digit("1")),
            Map.entry("2", KeyboardShortcutText.digit("2")),
            Map.entry("3", KeyboardShortcutText.digit("3")),
            Map.entry("4", KeyboardShortcutText.digit("4")),
            Map.entry("5", KeyboardShortcutText.digit("5")),
            Map.entry("6", KeyboardShortcutText.digit("6")),
            Map.entry("7", KeyboardShortcutText.digit("7")),
            Map.entry("8", KeyboardShortcutText.digit("8")),
            Map.entry("9", KeyboardShortcutText.digit("9")),
            Map.entry(",", KeyboardShortcutText.COMMA),
            Map.entry("+", KeyboardShortcutText.PLUS),
            Map.entry("-", KeyboardShortcutText.MINUS),
            Map.entry("×", KeyboardShortcutText.MULTIPLY),
            Map.entry("÷", KeyboardShortcutText.DIVIDE),
            Map.entry("mod", KeyboardShortcutText.MODULO),
            Map.entry("=", KeyboardShortcutText.ENTER),
            Map.entry("←", KeyboardShortcutText.BACKSPACE),
            Map.entry("CLR", KeyboardShortcutText.ESCAPE)
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

        String normalizedKey = normalisiereKey(key);
        String description = normalizedKey.matches("\\d")
                ? "Gibt die Ziffer " + key + " ein"
                : DESCRIPTIONS.get(normalizedKey);

        if (description == null)
        {
            return null;
        }

        String shortcut = SHORTCUTS.get(normalizedKey);
        if (shortcut == null)
        {
            return description;
        }

        return description + " (Taste: " + shortcut + ")";
    }

    private static String normalisiereKey(String key)
    {
        if ("C".equals(key))
        {
            return key;
        }

        if ("f(x) ▼".equals(key))
        {
            return "f(x)";
        }

        return key;
    }
}
