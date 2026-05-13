package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeVisibilityPolicyTest
{
    @Test
    void sollHistoryAnzeigen_ShouldOnlyShowHistoryForStandardAndScientific()
    {
        assertTrue(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.STANDARD));
        assertTrue(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.WISSENSCHAFTLICH));

        assertFalse(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.PROGRAMMIERER));
        assertFalse(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.GRAPH));
        assertFalse(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.KOMPLEX));
        assertFalse(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.MATRIX));
        assertFalse(ModeVisibilityPolicy.sollHistoryAnzeigen(RechnerModus.STATISTIK));
    }

    @Test
    void sollGlobalesDisplayAnzeigen_ShouldHideDisplayForModesWithOwnMainDisplay()
    {
        assertTrue(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.STANDARD));
        assertTrue(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.WISSENSCHAFTLICH));
        assertTrue(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.KOMPLEX));

        assertFalse(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.PROGRAMMIERER));
        assertFalse(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.GRAPH));
        assertFalse(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.MATRIX));
        assertFalse(ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(RechnerModus.STATISTIK));
    }

    @Test
    void sindStandardShortcutsAktiv_ShouldOnlyAllowStandardCalculatorModes()
    {
        assertTrue(ModeVisibilityPolicy.sindStandardShortcutsAktiv(RechnerModus.STANDARD));
        assertTrue(ModeVisibilityPolicy.sindStandardShortcutsAktiv(RechnerModus.WISSENSCHAFTLICH));
        assertFalse(ModeVisibilityPolicy.sindStandardShortcutsAktiv(RechnerModus.PROGRAMMIERER));
        assertFalse(ModeVisibilityPolicy.sindStandardShortcutsAktiv(RechnerModus.STATISTIK));
    }
}
