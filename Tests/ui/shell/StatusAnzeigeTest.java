package ui.shell;

import org.junit.jupiter.api.Test;
import ui.theme.themes.DarkTheme;

import javax.swing.JLabel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusAnzeigeTest
{
    @Test
    void zeigeErfolg_ShouldResetWarningColor_AfterAnError()
    {
        // Arrange
        DarkTheme theme = new DarkTheme();
        JLabel label = new JLabel();
        StatusAnzeige anzeige = new StatusAnzeige(label);
        anzeige.setTheme(theme);

        // Act
        anzeige.zeigeFehler(null, "Ungültige Matrixeingabe");
        String fehlerText = label.getText();
        anzeige.zeigeErfolg("Addition erfolgreich");

        // Assert
        assertEquals("Ungültige Matrixeingabe", fehlerText);
        assertEquals("Addition erfolgreich", label.getText());
        assertEquals(theme.secondaryDisplayForeground(), label.getForeground());
    }
}
