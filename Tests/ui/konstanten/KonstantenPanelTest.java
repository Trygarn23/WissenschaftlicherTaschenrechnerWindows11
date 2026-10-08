package ui.konstanten;

import common.konstanten.EigeneKonstanten;
import common.konstanten.KonstantenFavoriten;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testhilfen.SwingSuche;
import ui.theme.themes.DarkTheme;
import ui.theme.themes.LightTheme;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KonstantenPanelTest
{
    @TempDir
    Path ordner;

    @Test
    void einfuegen_ShouldPassParserReadyValueOfSearchedConstant()
    {
        // Arrange
        List<String> eingefuegt = new ArrayList<>();
        KonstantenPanel panel = new KonstantenPanel(new DarkTheme(), favoriten(), eigene(), eingefuegt::add);

        // Act
        SwingSuche.finde(panel, JTextField.class).setText("boltzmann-konstante");
        SwingSuche.button(panel, "Einfügen").doClick();

        // Assert
        assertEquals(List.of("1,380649e-23"), eingefuegt);
    }

    @Test
    void favoritSpalte_ShouldMoveConstantToTopAndPersist() throws Exception
    {
        // Arrange
        KonstantenFavoriten favoriten = favoriten();
        KonstantenPanel panel = new KonstantenPanel(new DarkTheme(), favoriten, eigene(), wert -> { });
        JTable tabelle = SwingSuche.finde(panel, JTable.class);
        int letzteZeile = tabelle.getRowCount() - 1;
        String name = (String) tabelle.getValueAt(letzteZeile, 1);

        // Act
        tabelle.setValueAt(true, letzteZeile, 0);
        SwingUtilities.invokeAndWait(() -> { });

        // Assert
        assertEquals(name, tabelle.getValueAt(0, 1));
        assertEquals(Boolean.TRUE, tabelle.getValueAt(0, 0));
        assertTrue(new KonstantenFavoriten(ordner.resolve("favoriten.txt")).istFavorit(name));
    }

    @Test
    void panel_ShouldShowCustomConstantsAndSwitchThemes()
    {
        // Arrange
        EigeneKonstanten eigene = eigene();
        eigene.fuegeHinzu("Mehrwertsteuer", "MwSt", "0,19", "");
        KonstantenPanel panel = new KonstantenPanel(new LightTheme(), favoriten(), eigene, wert -> { });
        JTable tabelle = SwingSuche.finde(panel, JTable.class);

        // Act
        SwingSuche.finde(panel, JTextField.class).setText("mehrwert");
        panel.applyTheme(new DarkTheme());

        // Assert
        assertEquals(1, tabelle.getRowCount());
        assertEquals("0,19", tabelle.getValueAt(0, 3));
        assertTrue(SwingSuche.button(panel, "Löschen").isEnabled());
        assertNotNull(SwingSuche.button(panel, "Eigene hinzufügen…"));
    }

    private KonstantenFavoriten favoriten()
    {
        return new KonstantenFavoriten(ordner.resolve("favoriten.txt"));
    }

    private EigeneKonstanten eigene()
    {
        return new EigeneKonstanten(ordner.resolve("eigene.txt"));
    }
}
