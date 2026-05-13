import modes.statistik.ui.StatistikPanel;
import org.junit.jupiter.api.Test;
import ui.theme.themes.LightTheme;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTable;
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
        assertNotNull(findButton(panel, "Clear"));
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
