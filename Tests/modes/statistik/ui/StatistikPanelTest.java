package modes.statistik.ui;

import modes.statistik.ui.StatistikPanel;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;
import ui.theme.themes.LightTheme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class StatistikPanelTest
{
    @Test
    void statistikPanel_ShouldExposeInputTableDiagramSelectionAndResultArea()
    {
        StatistikPanel panel = new StatistikPanel();

        assertEquals(1, countComponents(panel, JTable.class));
        assertEquals(1, countComponents(panel, JComboBox.class));
        assertEquals(2, countComponents(panel, JTextArea.class));
        assertNotNull(findButton(panel, "Text auswerten"));
        assertNotNull(findButton(panel, "Tabelle auswerten"));
        assertNotNull(findButton(panel, "Beispiel"));
        assertNotNull(findButton(panel, "Leeren"));
    }

    @Test
    void textAuswerten_ShouldUpdateResultArea()
    {
        StatistikPanel panel = new StatistikPanel();

        findButton(panel, "Text auswerten").doClick();

        assertNotNull(findTextAreaContaining(panel, "Kennzahlen"));
    }

    @Test
    void applyTheme_ShouldUpdateButtonColors()
    {
        StatistikPanel panel = new StatistikPanel();
        LightTheme theme = new LightTheme();

        panel.applyTheme(theme);

        assertEquals(theme.toggleButtonBackground(), findButton(panel, "Text auswerten").getBackground());
        assertEquals(theme.toggleButtonForeground(), findButton(panel, "Text auswerten").getForeground());
    }

    @Test
    void statistikPanel_ShouldOfferCopyImportAndDistributionButtons()
    {
        // Arrange & Act
        StatistikPanel panel = new StatistikPanel();

        // Assert
        assertNotNull(SwingSuche.button(panel, "Als Tabelle kopieren"));
        assertNotNull(SwingSuche.button(panel, "CSV importieren"));
        assertNotNull(SwingSuche.button(panel, "Binomial berechnen"));
        assertNotNull(SwingSuche.button(panel, "Normal berechnen"));
    }

    @Test
    void textAuswerten_ShouldMarkOutlierRowAndCountIt()
    {
        // Arrange
        StatistikPanel panel = new StatistikPanel();
        LightTheme theme = new LightTheme();
        panel.applyTheme(theme);
        SwingSuche.finde(panel, JTextArea.class, JTextArea::isEditable).setText("1\n2\n2\n4\n5\n8\n40");

        // Act
        SwingSuche.button(panel, "Text auswerten").doClick();

        // Assert
        JTable table = SwingSuche.finde(panel, JTable.class);
        // Der Renderer ist eine einzige Komponente, deshalb die Farbe sofort abgreifen.
        Color ausreisser = table.prepareRenderer(table.getCellRenderer(6, 1), 6, 1).getBackground();
        Color normal = table.prepareRenderer(table.getCellRenderer(0, 1), 0, 1).getBackground();
        assertEquals(theme.errorPulseColor(), ausreisser);
        assertEquals(theme.inputBackground(), normal);
        assertNotNull(findTextAreaContaining(panel, "Ausreißer: 1 (bei 1,5×IQR) → 40"));
    }

    @Test
    void binomialBerechnen_ShouldShowProbabilities()
    {
        // Arrange
        StatistikPanel panel = new StatistikPanel();

        // Act
        SwingSuche.button(panel, "Binomial berechnen").doClick();

        // Assert: Standardwerte n = 10, p = 0,5, k = 5
        assertNotNull(SwingSuche.finde(panel, JLabel.class,
                label -> label.getText().contains("P(X = 5) = 0,24609375")));
    }

    @Test
    void normalBerechnen_ShouldShowStatusErrorForInvalidSigma()
    {
        // Arrange: das erste Feld mit "1" ist σ (b steht weiter hinten)
        StatistikPanel panel = new StatistikPanel();
        SwingSuche.finde(panel, JTextField.class, field -> "1".equals(field.getText())).setText("0");

        // Act
        SwingSuche.button(panel, "Normal berechnen").doClick();

        // Assert
        assertNotNull(SwingSuche.finde(panel, JLabel.class, label -> "σ muss größer als 0 sein.".equals(label.getText())));
    }

    private JButton findButton(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button && text.equals(button.getText()))
            {
                return button;
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findButton(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Button not found: " + text);
        return null;
    }

    private JTextArea findTextAreaContaining(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JTextArea textArea && textArea.getText().contains(text))
            {
                return textArea;
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findTextAreaContaining(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Text area not found: " + text);
        return null;
    }

    private int countComponents(Container container, Class<?> type)
    {
        int count = 0;
        for (Component component : container.getComponents())
        {
            if (type.isInstance(component))
            {
                count++;
            }

            if (component instanceof Container child)
            {
                count += countComponents(child, type);
            }
        }
        return count;
    }
}
