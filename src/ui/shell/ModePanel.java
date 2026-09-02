package ui.shell;

import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.AppTheme;

/**
 * Kleiner Vertrag zwischen einem Modus-Panel und der Shell.
 * Die Modi behalten ihre eigenen Zustaende und ihre Fachlogik.
 */
public interface ModePanel
{
    RechnerModus getRechnerModus();

    default boolean zeigtGlobalesDisplay()
    {
        return ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(getRechnerModus());
    }

    default boolean zeigtHistory()
    {
        return ModeVisibilityPolicy.sollHistoryAnzeigen(getRechnerModus());
    }

    default boolean nutztStandardShortcuts()
    {
        return ModeVisibilityPolicy.sindStandardShortcutsAktiv(getRechnerModus());
    }

    default void setWinkelModus(WinkelModus winkelModus)
    {
    }

    void applyTheme(AppTheme theme);
}
