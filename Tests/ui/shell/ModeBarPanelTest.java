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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeBarPanelTest
{
    @Test
    void modeBar_ShouldShowMainModesDirectlyAndMoveExtraModesIntoMenu()
    {
        ModeBarPanel panel = new ModeBarPanel();

        assertEquals(List.of("Standard", "Wissenschaftlich", "PRG", "Graph", "Komplex", "Weitere..."), buttonTexte(panel));
        assertEquals(List.of("Matrix", "Statistik", "Gleichungen", "Brüche", "Vektoren", "Finanzen", "Netzwerk", "Logik", "Datum/Zeit", "Einheiten"),
                buttonTexte(weitereMenu(panel)));
        assertEquals("Weitere Modi und Werkzeuge", weitereButton(panel).getToolTipText());
    }

    @Test
    void modeBar_ShouldOnlyMarkLastUsedButtonActive_WhenExtraModeIsSelected()
    {
        ModeBarPanel panel = new ModeBarPanel();
        AzubiModernTheme theme = new AzubiModernTheme();

        panel.setSelectedMode(RechnerModus.MATRIX, theme);

        assertEquals(theme.modeButtonInactiveBackground(), weitereButton(panel).getBackground());
        assertEquals(theme.modeButtonActiveBackground(), SwingSuche.button(panel, "Matrix").getBackground());
        panel.setSelectedMode(RechnerModus.STANDARD, theme);
        assertEquals(theme.modeButtonInactiveBackground(), SwingSuche.button(panel, "Matrix").getBackground());
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

    @Test
    void modeBar_ShouldShowLastUsedExtraModeAsDirectButton()
    {
        // Arrange
        ModeBarPanel panel = new ModeBarPanel();
        AtomicReference<RechnerModus> selected = new AtomicReference<>();
        panel.setModeListener(selected::set);

        // Act
        panel.setSelectedMode(RechnerModus.MATRIX, new AzubiModernTheme());
        panel.setSelectedMode(RechnerModus.LOGIK, new AzubiModernTheme());
        SwingSuche.button(panel, "Logik").doClick();

        // Assert
        assertEquals(List.of("Standard", "Wissenschaftlich", "PRG", "Graph", "Komplex", "Logik", "Weitere..."),
                buttonTexte(panel).subList(0, 7));
        assertEquals(RechnerModus.LOGIK, selected.get());
    }

    @Test
    void modeBar_ShouldOnlyEnableStandardAndScientific_InExamMode()
    {
        // Arrange
        ModeBarPanel panel = new ModeBarPanel();

        // Act
        panel.setPruefungsModus(true);

        // Assert
        assertTrue(SwingSuche.button(panel, "Standard").isEnabled());
        assertTrue(SwingSuche.button(panel, "Wissenschaftlich").isEnabled());
        assertFalse(SwingSuche.button(panel, "Graph").isEnabled());
        assertFalse(weitereButton(panel).isEnabled());

        // Act
        panel.setPruefungsModus(false);

        // Assert
        assertTrue(SwingSuche.button(panel, "Graph").isEnabled());
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
