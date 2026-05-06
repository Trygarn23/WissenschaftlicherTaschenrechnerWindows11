import modes.programmierer.ui.ProgrammiererPanel;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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

    @Test
    void panel_ShouldRegisterKeyboardActionsForDigitsAndHexLetters()
    {
        // Arrange
        ProgrammiererPanel panel = new ProgrammiererPanel();

        // Act
        Object digitAction = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
        Object hexAction = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));

        // Assert
        assertNotNull(digitAction);
        assertNotNull(hexAction);
        assertNotNull(panel.getActionMap().get(digitAction));
        assertNotNull(panel.getActionMap().get(hexAction));
    }

    @Test
    void panelKeyboardAction_ShouldIgnoreInput_WhenPanelIsNotShowing()
    {
        // Arrange
        ProgrammiererPanel panel = new ProgrammiererPanel();
        Object digitAction = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));

        // Act
        panel.getActionMap().get(digitAction).actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, ""));

        // Assert
        assertNotNull(findLabelContaining(panel, "DEC: 0"));
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
