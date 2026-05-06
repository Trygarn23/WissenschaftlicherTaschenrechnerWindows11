import modes.programmierer.ui.ProgrammiererPanel;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.*;

public class ProgrammiererPanelTest
{
    @Test
    void panel_ShouldDisableDigitsThatAreInvalidForBinaryMode()
    {
        // Arrange
        ProgrammiererPanel panel = new ProgrammiererPanel();

        // Act
        findButton(panel, "BIN").doClick();

        // Assert
        assertTrue(findButton(panel, "0").isEnabled());
        assertTrue(findButton(panel, "1").isEnabled());
        assertFalse(findButton(panel, "2").isEnabled());
        assertFalse(findButton(panel, "A").isEnabled());
    }

    @Test
    void panel_ShouldShowProgrammerStatus_WhenOptionsChange()
    {
        // Arrange
        ProgrammiererPanel panel = new ProgrammiererPanel();

        // Act
        findButton(panel, "HEX").doClick();
        findButton(panel, "BYTE").doClick();
        findButton(panel, "SIGNED").doClick();

        // Assert
        assertNotNull(findLabelContaining(panel, "Basis: HEX | Wortbreite: BYTE | UNSIGNED"));
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

    private JLabel findLabelContaining(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JLabel label && label.getText().contains(text))
            {
                return label;
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findLabelContaining(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Label not found: " + text);
        return null;
    }
}
