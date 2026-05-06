import modes.standard.ui.StandardPanel;
import modes.programmierer.ui.ProgrammiererPanel;
import modes.wissenschaftlich.ui.WissenschaftlichPanel;
import org.junit.jupiter.api.Test;
import ui.tooltips.ButtonTooltips;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.*;

public class ButtonTooltipsTest
{
    @Test
    void textFor_ShouldIncludeShortcut_WhenShortcutExists()
    {
        // Act
        String tooltip = ButtonTooltips.textFor("=");

        // Assert
        assertEquals("Berechnet das Ergebnis (Taste: Enter)", tooltip);
    }

    @Test
    void standardPanel_ShouldAssignTooltipsToAllButtons()
    {
        // Arrange
        StandardPanel panel = new StandardPanel();

        // Act & Assert
        assertAllButtonsHaveTooltips(panel);
        assertEquals("Löscht das letzte Zeichen (Taste: Backspace)", findButton(panel, "←").getToolTipText());
    }

    @Test
    void wissenschaftlichPanel_ShouldAssignTooltipsToVisibleButtons()
    {
        // Arrange
        WissenschaftlichPanel panel = new WissenschaftlichPanel();

        // Act & Assert
        assertAllButtonsHaveTooltips(panel);
        assertEquals("Sinusfunktion", findButton(panel, "sin").getToolTipText());
        assertEquals("Öffnet weitere trigonometrische Funktionen", findButton(panel, "f(x) ▼").getToolTipText());
    }

    @Test
    void programmiererPanel_ShouldAssignTooltipsToAllButtons()
    {
        // Arrange
        ProgrammiererPanel panel = new ProgrammiererPanel();

        // Act & Assert
        assertAllButtonsHaveTooltips(panel);
        assertEquals("Verschiebt die Bits logisch nach rechts", findButton(panel, ">>>").getToolTipText());
        assertEquals("Schaltet zwischen vorzeichenbehafteter und vorzeichenloser Darstellung um",
                findButton(panel, "SIGNED").getToolTipText());
    }

    private void assertAllButtonsHaveTooltips(Container container)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button)
            {
                assertNotNull(button.getToolTipText(), "Missing tooltip for " + button.getText());
                assertFalse(button.getToolTipText().isBlank(), "Blank tooltip for " + button.getText());
            }

            if (component instanceof Container child)
            {
                assertAllButtonsHaveTooltips(child);
            }
        }
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
