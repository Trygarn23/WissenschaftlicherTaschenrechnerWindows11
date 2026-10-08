package ui.shell;

import common.konstanten.Konstante;
import common.konstanten.KonstantenKatalog;
import common.state.RechnerModus;
import ui.befehle.Befehl;
import ui.theme.ThemeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Alles, was man in der Befehlssuche (Strg+K) finden kann. */
final class ShellBefehle
{
    /** Funktionen, die als „name(“ in den Ausdruck eingefügt werden. */
    private static final List<String> FUNKTIONEN = List.of(
            "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh",
            "ln", "log", "sqrt", "abs", "exp", "floor", "ceil", "round"
    );

    /** Was die Befehle in der Shell auslösen. */
    record Aktionen(
            Consumer<RechnerModus> modus,
            Consumer<ThemeType> theme,
            Consumer<String> einfuegen,
            Runnable einstellungen,
            Runnable tastenkuerzel,
            Runnable einheiten,
            Runnable konstanten,
            Runnable miniRechner,
            Runnable verlaufUmklappen,
            Runnable winkelUmschalten
    )
    {
    }

    private ShellBefehle()
    {
    }

    static List<Befehl> erstelle(Aktionen aktionen, List<Konstante> eigeneKonstanten, boolean pruefungsModus)
    {
        List<Befehl> befehle = new ArrayList<>();
        for (RechnerModus modus : RechnerModus.values())
        {
            if (PruefungsModus.istModusErlaubt(pruefungsModus, modus))
            {
                befehle.add(new Befehl(modus.getLabel(), "Modus", List.of(modus.name()), () -> aktionen.modus().accept(modus)));
            }
        }
        for (String funktion : FUNKTIONEN)
        {
            befehle.add(new Befehl(funktion + "(", "Funktion", () -> aktionen.einfuegen().accept(funktion + "(")));
        }
        for (ThemeType theme : ThemeType.values())
        {
            befehle.add(new Befehl("Theme: " + theme, "Theme", () -> aktionen.theme().accept(theme)));
        }

        befehle.add(new Befehl("Einstellungen", "Aktion", List.of("settings", "optionen"), aktionen.einstellungen()));
        befehle.add(new Befehl("Tastenkürzel anzeigen", "Aktion", List.of("hilfe", "shortcuts", "F1"), aktionen.tastenkuerzel()));
        befehle.add(new Befehl("DEG/RAD umschalten", "Aktion", List.of("winkel", "grad", "bogenmaß"), aktionen.winkelUmschalten()));

        if (PruefungsModus.sindWerkzeugeErlaubt(pruefungsModus))
        {
            befehle.add(new Befehl("Einheiten umrechnen", "Aktion", List.of("units", "umrechnung"), aktionen.einheiten()));
            befehle.add(new Befehl("Konstanten-Bibliothek", "Aktion", List.of("naturkonstanten"), aktionen.konstanten()));
            befehle.add(new Befehl("Mini-Rechner", "Aktion", List.of("immer oben", "klein"), aktionen.miniRechner()));
            befehle.add(new Befehl("Verlauf ein-/ausklappen", "Aktion", List.of("history"), aktionen.verlaufUmklappen()));

            List<Konstante> konstanten = new ArrayList<>(KonstantenKatalog.STANDARD);
            konstanten.addAll(eigeneKonstanten);
            for (Konstante konstante : konstanten)
            {
                befehle.add(new Befehl(konstante.name() + " (" + konstante.symbol() + ")", "Konstante",
                        List.of(konstante.symbol()), () -> aktionen.einfuegen().accept(konstante.alsEingabe())));
            }
        }
        return befehle;
    }
}
