package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import ui.theme.themes.AzubiModernTheme;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeBarPanelTest
{
    @Test
    void modeBar_ShouldShowMainModesDirectlyAndMoveExtraModesIntoMenu()
    {
        ModeBarPanel panel = new ModeBarPanel();

        assertEquals(List.of("Standard", "Wissenschaftlich", "PRG", "Graph", "Komplex"), panel.direkteModusLabelsForTest());
        assertEquals(List.of("Matrix", "Statistik", "Einheiten"), panel.weitereLabelsForTest());
        assertEquals("Weitere...", panel.weitereButtonForTest().getText());
        assertEquals("Weitere Modi und Werkzeuge", panel.weitereButtonForTest().getToolTipText());
    }

    @Test
    void modeBar_ShouldMarkMoreButtonActive_WhenExtraModeIsSelected()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AzubiModernTheme theme = new AzubiModernTheme();

        panel.setSelectedMode(RechnerModus.MATRIX, theme);

        assertEquals(theme.modeButtonActiveBackground(), panel.weitereButtonForTest().getBackground());
    }

    @Test
    void modeBar_ShouldInvokeUnitsListener_FromMoreMenuAction()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AtomicBoolean invoked = new AtomicBoolean(false);
        panel.setUnitsListener(() -> invoked.set(true));

        panel.weitereUnitsButtonForTest().doClick();

        assertTrue(invoked.get());
    }

    @Test
    void modeBar_ShouldInvokeModeListener_ForExtraMode()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AtomicReference<RechnerModus> selected = new AtomicReference<>();
        panel.setModeListener(selected::set);

        panel.weitereModeButtonForTest(RechnerModus.STATISTIK).doClick();

        assertEquals(RechnerModus.STATISTIK, selected.get());
    }
}
