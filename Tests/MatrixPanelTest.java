import modes.matrix.ui.MatrixPanel;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixPanelTest
{
    @Test
    void matrixPanel_ShouldExposeSizeControlsInputsOperationsAndResultArea()
    {
        MatrixPanel panel = new MatrixPanel();

        assertEquals(4, countComponents(panel, JComboBox.class));
        assertEquals(9, countComponents(panel, JTextField.class));
        assertEquals(1, countComponents(panel, JTextArea.class));
        assertNotNull(findButton(panel, "A + B"));
        assertNotNull(findButton(panel, "A × B"));
        assertNotNull(findButton(panel, "A^T"));
        assertNotNull(findButton(panel, "spur A"));
        assertNotNull(findButton(panel, "rang A"));
        assertNotNull(findButton(panel, "det A"));
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
