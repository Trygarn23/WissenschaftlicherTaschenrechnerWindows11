package ui.shell;

import common.state.RechnerModus;

public final class ModeVisibilityPolicy
{
    private ModeVisibilityPolicy()
    {
    }

    /** Verlauf und globales Display gibt es nur für die beiden Ausdrucksrechner, alle anderen Modi haben eigene Anzeigen. */
    public static boolean sollHistoryAnzeigen(RechnerModus modus)
    {
        return istAusdrucksRechner(modus);
    }

    public static boolean sollGlobalesDisplayAnzeigen(RechnerModus modus)
    {
        return modus == RechnerModus.STANDARD
                || modus == RechnerModus.WISSENSCHAFTLICH
                || modus == RechnerModus.KOMPLEX;
    }

    public static boolean sindStandardShortcutsAktiv(RechnerModus modus)
    {
        return istAusdrucksRechner(modus);
    }

    private static boolean istAusdrucksRechner(RechnerModus modus)
    {
        return modus == RechnerModus.STANDARD || modus == RechnerModus.WISSENSCHAFTLICH;
    }
}
