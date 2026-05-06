import org.junit.jupiter.api.Test;
import ui.shell.DisplayPanel;

import static org.junit.jupiter.api.Assertions.*;

public class DisplayPanelTest
{
    @Test
    void displayPanel_ShouldExposeDisplayTexts_WhenTextsAreSet()
    {
        // Arrange
        DisplayPanel panel = new DisplayPanel();

        // Act
        panel.setMainText("42");
        panel.setSecondaryText("6*7 = 42");
        panel.setStatusText("Modus: Standard | Winkel: DEG | Speicher leer");

        // Assert
        assertEquals("42", panel.getMainText());
        assertEquals("6*7 = 42", panel.getSecondaryText());
        assertEquals("Modus: Standard | Winkel: DEG | Speicher leer", panel.getStatusText());
    }

    @Test
    void displayPanel_ShouldDescribeClipboardShortcutsInTooltip()
    {
        // Arrange
        DisplayPanel panel = new DisplayPanel();

        // Act
        String tooltip = panel.getToolTipText();

        // Assert
        assertTrue(tooltip.contains("Strg+C"));
        assertTrue(tooltip.contains("Strg+V"));
    }
}
