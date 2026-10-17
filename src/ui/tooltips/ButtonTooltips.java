package ui.tooltips;

import ui.shortcuts.Tastenkuerzel;

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
            Map.entry("f(x)", "Öffnet weitere Funktionen: Trigonometrie, nCr, nPr, ggT, kgV, n!, S↔D, Variablen"),
            Map.entry("nCr", "Kombinationen ohne Wiederholung: nCr(n;k), z. B. nCr(5;2) = 10"),
            Map.entry("nPr", "Variationen ohne Wiederholung: nPr(n;k), z. B. nPr(5;2) = 20"),
            Map.entry("ggT", "Größter gemeinsamer Teiler: ggT(a;b), z. B. ggT(12;18) = 6"),
            Map.entry("kgV", "Kleinstes gemeinsames Vielfaches: kgV(a;b), z. B. kgV(4;6) = 12"),
            Map.entry("n!", "Fakultät der letzten ganzen Zahl, z. B. 5! = 120"),
            Map.entry(";","Trennt zwei Funktionsargumente, z. B. nCr(5;2)"),
            Map.entry("S↔D", "Wechselt zwischen Dezimalzahl und exakter Form (Bruch, Wurzel, π)"),
            Map.entry("STO", "Speichert den aktuellen Wert in einer Variablen A–F"),
            Map.entry("RCL", "Fügt den Wert einer Variablen A–F ein"),
            Map.entry("Mehr Stellen", "Zeigt das Ergebnis mit voller Genauigkeit"),
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

    /** Was ein Screenreader statt des Symbols vorlesen soll; „←“ oder „xʸ“ spricht sonst keiner sinnvoll aus. */
    private static final Map<String, String> SPRECHNAMEN = Map.ofEntries(
            Map.entry("%", "Prozent"),
            Map.entry("CE", "Eingabe löschen"),
            Map.entry("C", "Alles löschen"),
            Map.entry("CLR", "Alles löschen"),
            Map.entry("←", "Zeichen löschen"),
            Map.entry("1/x", "Kehrwert"),
            Map.entry("x²", "x Quadrat"),
            Map.entry("√x", "Wurzel"),
            Map.entry("÷", "Geteilt"),
            Map.entry("×", "Mal"),
            Map.entry("-", "Minus"),
            Map.entry("+", "Plus"),
            Map.entry("±", "Vorzeichen wechseln"),
            Map.entry(",", "Komma"),
            Map.entry("=", "Gleich"),
            Map.entry("(", "Klammer auf"),
            Map.entry(")", "Klammer zu"),
            Map.entry("xʸ", "x hoch y"),
            Map.entry("10ˣ", "10 hoch x"),
            Map.entry("|x|", "Betrag"),
            Map.entry("π", "Pi"),
            Map.entry("e", "Eulersche Zahl"),
            Map.entry("n!", "Fakultät"),
            Map.entry(";", "Argumente trennen"),
            Map.entry("S↔D", "Exakt oder dezimal"),
            Map.entry("MC", "Speicher löschen"),
            Map.entry("MR", "Speicher abrufen"),
            Map.entry("M+", "Speicher plus"),
            Map.entry("M-", "Speicher minus"),
            Map.entry("f(x)", "Funktionsmenü"),
            Map.entry("<<", "Shift links"),
            Map.entry(">>", "Shift rechts arithmetisch"),
            Map.entry(">>>", "Shift rechts logisch")
    );

    private ButtonTooltips()
    {
    }

    public static void apply(JButton button, String key)
    {
        button.setToolTipText(textFor(key));

        String normalizedKey = normalize(key);
        String sprechname = normalizedKey == null ? null : SPRECHNAMEN.get(normalizedKey);
        if (sprechname != null)
        {
            button.getAccessibleContext().setAccessibleName(sprechname);
        }
        button.getAccessibleContext().setAccessibleDescription(beschreibungFor(normalizedKey));
    }

    private static String normalize(String key)
    {
        return "f(x) ▼".equals(key) ? "f(x)" : key;
    }

    public static String textFor(String key)
    {
        if (key == null || key.isBlank())
        {
            return null;
        }

        String normalizedKey = normalize(key);
        String description = beschreibungFor(normalizedKey);

        if (description == null)
        {
            return null;
        }

        String shortcut = shortcutFor(normalizedKey);
        if (shortcut == null)
        {
            return description;
        }

        return description + " (Taste: " + shortcut + ")";
    }

    /** Nur die Beschreibung ohne Tastenkürzel, z. B. für die Tastenkürzel-Übersicht. */
    public static String beschreibungFor(String key)
    {
        if (key == null)
        {
            return null;
        }
        return key.matches("\\d") ? "Gibt die Ziffer " + key + " ein" : DESCRIPTIONS.get(key);
    }

    private static String shortcutFor(String key)
    {
        if (key.matches("\\d"))
        {
            return Tastenkuerzel.ziffernText(key);
        }
        if ("CLR".equals(key))
        {
            return Tastenkuerzel.PROGRAMMIERER_CLR_TEXT;
        }
        return Tastenkuerzel.fuerAktion(key).map(Tastenkuerzel::getAnzeigeText).orElse(null);
    }
}
