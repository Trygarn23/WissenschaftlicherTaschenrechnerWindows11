import org.junit.jupiter.api.Test;
import ui.shell.GlobalActionBarPanel;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalActionBarPanelTest
{
    @Test
    void actionBar_ShouldShowSettingsButton()
    {
        // Arrange
        GlobalActionBarPanel panel = new GlobalActionBarPanel();

        // Act
        JButton settingsButton = findButton(panel, "⚙");

        // Assert
        assertEquals("Einstellungen öffnen", settingsButton.getToolTipText());
    }

    @Test
    void actionBar_ShouldInvokeSettingsListener_WhenSettingsButtonIsClicked()
    {
        // Arrange
        GlobalActionBarPanel panel = new GlobalActionBarPanel();
        AtomicBoolean invoked = new AtomicBoolean(false);
        panel.setSettingsListener(e -> invoked.set(true));

        // Act
        findButton(panel, "⚙").doClick();

        // Assert
        assertTrue(invoked.get());
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
}
