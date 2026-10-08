package modes.logik.ui;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.*;

public class LogikPanelTest
{
    @Test
    void logicFonts_ShouldDisplayEveryOperatorAfterThemeChanges()
    {
        LogikPanel panel = new LogikPanel();
        for (ui.theme.AppTheme theme : java.util.List.of(new ui.theme.themes.DarkTheme(), new ui.theme.themes.Win95Theme()))
        {
            panel.applyTheme(theme);
            assertEquals(-1, SwingSuche.finde(panel, JTextField.class).getFont().canDisplayUpTo("∧∨¬⊕→↔"));
            for (String symbol : java.util.List.of("∧", "∨", "⊕"))
                assertEquals(-1, SwingSuche.button(panel, symbol).getFont().canDisplayUpTo(symbol));
        }
    }

    @Test
    void logikPanel_ShouldShowTruthTableForExampleAndOfferSymbolButtons()
    {
        // Arrange
        LogikPanel panel = new LogikPanel();

        // Assert
        assertEquals(RechnerModus.LOGIK, panel.getRechnerModus());
        for (String symbol : new String[]{"∧", "∨", "¬", "⊕", "→", "↔", "(", ")", "Auswerten"})
        {
            assertNotNull(SwingSuche.button(panel, symbol));
        }
        JTable tabelle = SwingSuche.alle(panel, JTable.class).getFirst();
        assertEquals(8, tabelle.getRowCount());
        assertEquals("Ergebnis", tabelle.getColumnName(3));
    }

    @Test
    void auswerten_ShouldUpdateTableAndHintAfterClick()
    {
        // Arrange
        LogikPanel panel = new LogikPanel();
        JTextField feld = SwingSuche.finde(panel, JTextField.class);
        feld.setText("A && B");

        // Act
        SwingSuche.button(panel, "Auswerten").doClick();

        // Assert
        JTable tabelle = SwingSuche.alle(panel, JTable.class).getFirst();
        assertEquals(4, tabelle.getRowCount());
        assertEquals("1", tabelle.getValueAt(3, 2));
        assertFalse(tabelle.isCellEditable(0, 0));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "erfüllbar (1 von 4 Zeilen wahr)".equals(l.getText())));
    }

    @Test
    void symbolButton_ShouldInsertOperatorAndErrorShouldShowInStatus()
    {
        // Arrange
        LogikPanel panel = new LogikPanel();
        JTextField feld = SwingSuche.finde(panel, JTextField.class);
        feld.setText("A");
        feld.setCaretPosition(1);

        // Act
        SwingSuche.button(panel, "∧").doClick();
        SwingSuche.button(panel, "Auswerten").doClick();

        // Assert
        assertEquals("A ∧ ", feld.getText());
        assertNotNull(SwingSuche.finde(panel, JLabel.class,
                l -> "Der Ausdruck ist unvollständig – am Ende fehlt ein Operand.".equals(l.getText())));
    }
}
