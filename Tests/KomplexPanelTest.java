import modes.komplex.ui.KomplexPanel;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.*;

public class KomplexPanelTest
{
    @Test
    void komplexPanel_ShouldExposeInputFieldsOperationsAndDisplayMode()
    {
        // Arrange
        KomplexPanel panel = new KomplexPanel();

        // Assert
        assertEquals(4, countComponents(panel, JTextField.class));
        assertNotNull(findButton(panel, "+"));
        assertNotNull(findButton(panel, "÷"));
        assertNotNull(findButton(panel, "conj z1"));
        assertEquals(1, countComponents(panel, JComboBox.class));
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
