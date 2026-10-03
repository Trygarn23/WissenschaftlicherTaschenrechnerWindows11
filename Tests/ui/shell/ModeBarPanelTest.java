package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;
import ui.theme.themes.AzubiModernTheme;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
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

        assertEquals(List.of("Standard", "Wissenschaftlich", "PRG", "Graph", "Komplex", "Weitere..."), buttonTexte(panel));
        assertEquals(List.of("Matrix", "Statistik", "Einheiten"), buttonTexte(weitereMenu(panel)));
        assertEquals("Weitere Modi und Werkzeuge", weitereButton(panel).getToolTipText());
    }

    @Test
    void modeBar_ShouldMarkMoreButtonActive_WhenExtraModeIsSelected()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AzubiModernTheme theme = new AzubiModernTheme();

        panel.setSelectedMode(RechnerModus.MATRIX, theme);

        assertEquals(theme.modeButtonActiveBackground(), weitereButton(panel).getBackground());
    }

    @Test
    void modeBar_ShouldInvokeUnitsListener_FromMoreMenuAction()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AtomicBoolean invoked = new AtomicBoolean(false);
        panel.setUnitsListener(() -> invoked.set(true));

        SwingSuche.button(weitereMenu(panel), "Einheiten").doClick();

        assertTrue(invoked.get());
    }

    @Test
    void modeBar_ShouldInvokeModeListener_ForExtraMode()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AtomicReference<RechnerModus> selected = new AtomicReference<>();
        panel.setModeListener(selected::set);

        SwingSuche.button(weitereMenu(panel), "Statistik").doClick();

        assertEquals(RechnerModus.STATISTIK, selected.get());
    }

    private static AbstractButton weitereButton(ModeBarPanel panel)
    {
        return SwingSuche.button(panel, "Weitere...");
    }

    private static JPopupMenu weitereMenu(ModeBarPanel panel)
    {
        return weitereButton(panel).getComponentPopupMenu();
    }

    private static List<String> buttonTexte(java.awt.Container container)
    {
        return SwingSuche.alle(container, JButton.class).stream().map(JButton::getText).toList();
    }
}
