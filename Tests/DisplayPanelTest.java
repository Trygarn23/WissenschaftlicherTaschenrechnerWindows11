import org.junit.jupiter.api.Test;
import ui.shell.DisplayPanel;
import ui.theme.themes.DarkTheme;

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

    @Test
    void displayPanel_ShouldReduceMainFontSize_WhenExpressionIsLong()
    {
        // Arrange
        DisplayPanel panel = new DisplayPanel();
        panel.applyTheme(new DarkTheme());
        int normalSize = panel.getMainFontSize();

        // Act
        panel.setMainText("1234567890+1234567890+1234567890+1234567890");

        // Assert
        assertTrue(panel.getMainFontSize() < normalSize);
    }
}
