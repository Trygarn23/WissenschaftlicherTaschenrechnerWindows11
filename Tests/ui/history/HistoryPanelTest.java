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
        assertTrue(angezeigterStern(panel, 0).isFilled());
        assertEquals(List.of("favoriteChanged"), favoriteEvents);

        // Act
        favoritButton(panel).doClick();

        // Assert
        assertFalse(panel.getAllStructuredEntries().getFirst().isFavorit());
        assertFalse(angezeigterStern(panel, 0).isFilled());
    }

    @Test
    void historyPanel_SearchFieldShouldUseFullPanelWidth()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        panel.setSize(220, 400);

        // Act
        panel.doLayout();
        SwingSuche.alle(panel, java.awt.Container.class).forEach(java.awt.Container::doLayout);

        // Assert
        assertEquals(panel.getWidth(), SwingSuche.finde(panel, JTextField.class).getWidth());
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

    @Test
    void historyPanel_ShouldDeleteSelectedEntryWithoutAskingAndAllowUndo()
    {
        // Arrange
        List<String> rueckfragen = new ArrayList<>();
        List<String> aenderungen = new ArrayList<>();
        HistoryPanel panel = new HistoryPanel(frage -> rueckfragen.add(frage));
        panel.setEntriesChangedListener(e -> aenderungen.add(e.getActionCommand()));
        panel.setAllStructuredEntries(zweiEintraege());

        // Act
        liste(panel).setSelectedIndex(0);
        menuePunkt(panel, "Ausgewählten Eintrag löschen").doClick();

        // Assert
        assertEquals(List.of("sin(90)"), ausdruecke(panel));
        assertTrue(rueckfragen.isEmpty());
        assertTrue(menuePunkt(panel, "Löschen rückgängig").isEnabled());

        // Act
        menuePunkt(panel, "Löschen rückgängig").doClick();

        // Assert
        assertEquals(List.of("2+3", "sin(90)"), ausdruecke(panel));
        assertFalse(menuePunkt(panel, "Löschen rückgängig").isEnabled());
        assertEquals(List.of("entriesChanged", "entriesChanged"), aenderungen);
    }

    @Test
    void historyPanel_ShouldAskBeforeDeletingAll_AndKeepEntriesWhenDeclined()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel(frage -> false);
        panel.setAllStructuredEntries(zweiEintraege());

        // Act
        menuePunkt(panel, "Alle Einträge löschen…").doClick();

        // Assert
        assertEquals(2, panel.getAllStructuredEntries().size());
    }

    @Test
    void historyPanel_ShouldDeleteAll_WhenConfirmed()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel(frage -> true);
        panel.setAllStructuredEntries(zweiEintraege());

        // Act
        menuePunkt(panel, "Alle Einträge löschen…").doClick();

        // Assert
        assertTrue(panel.getAllStructuredEntries().isEmpty());
    }

    @Test
    void historyPanel_ShouldDeleteOnlyVisibleEntries_WhenFilteredByMode()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel(frage -> true);
        panel.setAllStructuredEntries(zweiEintraege());

        // Act
        waehleFilter(panel, "Nur Wissenschaftlich");
        menuePunkt(panel, "Angezeigte Einträge löschen…").doClick();

        // Assert
        assertEquals(List.of("2+3"), ausdruecke(panel));
    }

    @Test
    void historyPanel_ShouldFilterByModeAndFavorites()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        panel.setAllStructuredEntries(zweiEintraege());

        // Act / Assert
        waehleFilter(panel, "Nur Standard");
        assertEquals(1, liste(panel).getModel().getSize());
        assertEquals("2+3", liste(panel).getModel().getElementAt(0).getAusdruck());

        waehleFilter(panel, "Nur Favoriten");
        assertEquals(1, liste(panel).getModel().getSize());
        assertEquals("sin(90)", liste(panel).getModel().getElementAt(0).getAusdruck());

        waehleFilter(panel, "Alle Einträge");
        assertEquals(2, liste(panel).getModel().getSize());
    }

    @Test
    void historyPanel_UndoShouldExpire_WhenNewEntryIsAdded()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel(frage -> true);
        panel.setAllStructuredEntries(zweiEintraege());
        menuePunkt(panel, "Alle Einträge löschen…").doClick();

        // Act
        panel.addStructuredEntry(new VerlaufEintrag("1+1", "2", RechnerModus.STANDARD, LocalDateTime.now(), false));

        // Assert
        assertFalse(menuePunkt(panel, "Löschen rückgängig").isEnabled());
    }

    private static List<VerlaufEintrag> zweiEintraege()
    {
        return List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, LocalDateTime.of(2026, 5, 12, 10, 30), false),
                new VerlaufEintrag("sin(90)", "1", RechnerModus.WISSENSCHAFTLICH, LocalDateTime.of(2026, 5, 12, 10, 31), true)
        );
    }

    private static List<String> ausdruecke(HistoryPanel panel)
    {
        return panel.getAllStructuredEntries().stream().map(VerlaufEintrag::getAusdruck).toList();
    }

    // Das Menü hängt am „Mehr“-Button, so wie es auch beim Klicken aufgeht.
    private static javax.swing.JMenuItem menuePunkt(HistoryPanel panel, String text)
    {
        javax.swing.JPopupMenu menue = SwingSuche.button(panel, "Mehr").getComponentPopupMenu();
        return SwingSuche.finde(menue, javax.swing.JMenuItem.class, item -> text.equals(item.getText()));
    }

    private static void waehleFilter(HistoryPanel panel, String text)
    {
        javax.swing.JComboBox<?> filter = SwingSuche.finde(panel, javax.swing.JComboBox.class);
        for (int i = 0; i < filter.getItemCount(); i++)
        {
            if (text.equals(String.valueOf(filter.getItemAt(i))))
            {
                filter.setSelectedIndex(i);
                return;
            }
        }
        fail("Filter nicht gefunden: " + text);
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
    private static StarIcon angezeigterStern(HistoryPanel panel, int index)
    {
        JList<VerlaufEintrag> liste = liste(panel);
        JLabel label = (JLabel) liste.getCellRenderer()
                .getListCellRendererComponent(liste, liste.getModel().getElementAt(index), index, false, false);
        return (StarIcon) label.getIcon();
    }
}
