package ui.shell;

import common.state.RechnerModus;

public final class ModeVisibilityPolicy
{
    private ModeVisibilityPolicy()
    {
    }

    public static boolean sollHistoryAnzeigen(RechnerModus modus)
    {
        return switch (modus)
        {
            case STANDARD, WISSENSCHAFTLICH -> true;
            case PROGRAMMIERER, GRAPH, KOMPLEX, MATRIX, STATISTIK -> false;
        };
    }

    public static boolean sollGlobalesDisplayAnzeigen(RechnerModus modus)
    {
        return modus != RechnerModus.PROGRAMMIERER
                && modus != RechnerModus.GRAPH
                && modus != RechnerModus.MATRIX
                && modus != RechnerModus.STATISTIK;
    }

    public static boolean sindStandardShortcutsAktiv(RechnerModus modus)
    {
        return modus == RechnerModus.STANDARD || modus == RechnerModus.WISSENSCHAFTLICH;
    }
}
