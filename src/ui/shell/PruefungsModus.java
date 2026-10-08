package ui.shell;

import common.state.RechnerModus;

/**
 * Regeln für den Prüfungsmodus: nur die beiden normalen Rechner, kein Verlauf, keine Hilfswerkzeuge.
 * Die Shell fragt hier nach, statt die Regeln über mehrere Stellen zu verteilen.
 */
public final class PruefungsModus
{
    public static final String HINWEIS = "PRÜFUNGSMODUS";

    private PruefungsModus()
    {
    }

    public static boolean istModusErlaubt(boolean pruefungsModus, RechnerModus modus)
    {
        return !pruefungsModus || modus == RechnerModus.STANDARD || modus == RechnerModus.WISSENSCHAFTLICH;
    }

    /** Ersatz, wenn beim Einschalten gerade ein gesperrter Modus offen ist. */
    public static RechnerModus erlaubterModus(boolean pruefungsModus, RechnerModus gewuenscht)
    {
        return istModusErlaubt(pruefungsModus, gewuenscht) ? gewuenscht : RechnerModus.WISSENSCHAFTLICH;
    }

    /** Verlauf, Einheiten, Konstanten-Bibliothek, Befehlssuche-Werkzeuge und Mini-Rechner sind gesperrt. */
    public static boolean sindWerkzeugeErlaubt(boolean pruefungsModus)
    {
        return !pruefungsModus;
    }
}
