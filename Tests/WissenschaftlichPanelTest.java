import modes.wissenschaftlich.ui.WissenschaftlichPanel;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class WissenschaftlichPanelTest
{
    @Test
    void functionMenuButton_ShouldBeKeyboardAccessible()
    {
        // Arrange
        WissenschaftlichPanel panel = new WissenschaftlichPanel();

        // Act
        JButton functionButton = findButtonContaining(panel, "f(x)");

        // Assert
        assertTrue(functionButton.isFocusable());
        assertEquals(Boolean.TRUE, functionButton.getClientProperty("keyboardFocusable"));
        assertEquals(KeyEvent.VK_F, functionButton.getMnemonic());
        assertEquals("Funktionsmenü", functionButton.getAccessibleContext().getAccessibleName());
    }

    private JButton findButtonContaining(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button && button.getText().contains(text))
            {
                return button;
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findButtonContaining(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Button not found: " + text);
        return null;
    }
}
