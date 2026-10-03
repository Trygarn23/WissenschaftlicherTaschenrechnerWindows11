package ui.history;

import common.history.VerlaufEintrag;
import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryPanelTest
{
    @Test
    void historyPanel_ShouldKeepLegacyAccess_WhenStructuredEntriesAreUsed()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        VerlaufEintrag eintrag = new VerlaufEintrag(
                "sin(90)",
                "1",
                RechnerModus.WISSENSCHAFTLICH,
                LocalDateTime.of(2026, 5, 12, 10, 30),
                false
        );

        // Act
        panel.setAllStructuredEntries(List.of(eintrag));

        // Assert
        assertEquals(List.of("sin(90) = 1"), panel.getAllEntries());
        assertEquals(List.of(eintrag), panel.getAllStructuredEntries());
    }

    @Test
    void historyPanel_ShouldSearchExpressionResultAndMode()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        panel.setAllStructuredEntries(List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, LocalDateTime.now(), false),
                new VerlaufEintrag("sin(90)", "1", RechnerModus.WISSENSCHAFTLICH, LocalDateTime.now(), false)
        ));

        // Act / Assert
        SwingSuche.finde(panel, JTextField.class).setText("sin");
        assertEquals(1, liste(panel).getModel().getSize());

        SwingSuche.finde(panel, JTextField.class).setText("5");
        assertEquals(1, liste(panel).getModel().getSize());

        SwingSuche.finde(panel, JTextField.class).setText("wissenschaftlich");
        assertEquals(1, liste(panel).getModel().getSize());
    }

    @Test
    void historyPanel_ShouldShowAndToggleFavorites()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        List<String> favoriteEvents = new ArrayList<>();
        panel.setFavoriteChangedListener(e -> favoriteEvents.add(e.getActionCommand()));
        panel.setAllStructuredEntries(List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD,
                        LocalDateTime.of(2026, 5, 12, 10, 30), false)
        ));

        // Act
        liste(panel).setSelectedIndex(0);
        favoritButton(panel).doClick();

        // Assert
        assertTrue(panel.getAllStructuredEntries().getFirst().isFavorit());
        assertTrue(angezeigterText(panel, 0).startsWith("\u2605 "));
        assertEquals(List.of("favoriteChanged"), favoriteEvents);

        // Act
        favoritButton(panel).doClick();

        // Assert
        assertFalse(panel.getAllStructuredEntries().getFirst().isFavorit());
        assertTrue(angezeigterText(panel, 0).startsWith("\u2606 "));
    }

    @Test
    void historyPanel_SearchShouldRemainStable_WhenFavoriteChanges()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        panel.setAllStructuredEntries(List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, LocalDateTime.now(), false),
                new VerlaufEintrag("sin(90)", "1", RechnerModus.WISSENSCHAFTLICH, LocalDateTime.now(), true)
        ));

        // Act
        SwingSuche.finde(panel, JTextField.class).setText("sin");
        liste(panel).setSelectedIndex(0);
        favoritButton(panel).doClick();

        // Assert
        assertEquals(1, liste(panel).getModel().getSize());
        assertEquals("sin(90)", panel.getAllStructuredEntries().get(1).getAusdruck());
        assertFalse(panel.getAllStructuredEntries().get(1).isFavorit());
    }

    @Test
    void historyPanel_ShouldReadLegacyEntriesAsStructuredEntries()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();

        // Act
        panel.setAllEntries(List.of("7*6 = 42"));

        // Assert
        assertEquals(1, panel.getAllStructuredEntries().size());
        assertEquals("7*6", panel.getAllStructuredEntries().getFirst().getAusdruck());
        assertEquals("42", panel.getAllStructuredEntries().getFirst().getErgebnis());
        assertEquals(RechnerModus.STANDARD, panel.getAllStructuredEntries().getFirst().getModus());
    }

    private static javax.swing.AbstractButton favoritButton(HistoryPanel panel)
    {
        return SwingSuche.finde(panel, javax.swing.AbstractButton.class, b -> "Favorit umschalten".equals(b.getToolTipText()));
    }

    @SuppressWarnings("unchecked")
    private static JList<VerlaufEintrag> liste(HistoryPanel panel)
    {
        return SwingSuche.finde(panel, JList.class);
    }

    // Über den echten Renderer lesen, so wie der Eintrag auch auf dem Bildschirm steht.
    private static String angezeigterText(HistoryPanel panel, int index)
    {
        JList<VerlaufEintrag> liste = liste(panel);
        JLabel label = (JLabel) liste.getCellRenderer()
                .getListCellRendererComponent(liste, liste.getModel().getElementAt(index), index, false, false);
        return label.getText();
    }
}
