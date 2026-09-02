import org.junit.jupiter.api.Test;
import ui.shell.GlobalActionBarPanel;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GlobalActionBarPanelTest
{
    @Test
    void actionBar_ShouldShowSettingsButton()
    {
        GlobalActionBarPanel panel = new GlobalActionBarPanel();

        JButton settingsButton = findButton(panel, "Einstellungen");

        assertEquals("Einstellungen oeffnen", settingsButton.getToolTipText());
    }

    @Test
    void actionBar_ShouldInvokeSettingsListener_WhenSettingsButtonIsClicked()
    {
        GlobalActionBarPanel panel = new GlobalActionBarPanel();
        AtomicBoolean invoked = new AtomicBoolean(false);
        panel.setSettingsListener(e -> invoked.set(true));

        findButton(panel, "Einstellungen").doClick();

        assertTrue(invoked.get());
    }

    @Test
    void actionBar_ShouldNotExposeUnitsButton_AsTopLevelAction()
    {
        GlobalActionBarPanel panel = new GlobalActionBarPanel();

        assertNull(findOptionalButton(panel, "Einheiten"));
    }

    private JButton findButton(Container container, String text)
    {
        JButton result = findOptionalButton(container, text);
        if (result == null)
        {
            fail("Button not found: " + text);
        }
        return result;
    }

    private JButton findOptionalButton(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button && text.equals(button.getText()))
            {
                return button;
            }

            if (component instanceof Container child)
            {
                JButton result = findOptionalButton(child, text);
                if (result != null)
                {
                    return result;
                }
            }
        }

        return null;
    }
}
