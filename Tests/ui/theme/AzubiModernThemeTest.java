package ui.theme;

import org.junit.jupiter.api.Test;
import ui.theme.themes.AzubiModernTheme;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AzubiModernThemeTest
{
    @Test
    void themeType_ShouldContainAzubiModern()
    {
        assertNotNull(ThemeType.AZUBI_MODERN);
    }

    @Test
    void azubiModernTheme_ShouldProvideReadableModernRoles()
    {
        AppTheme theme = new AzubiModernTheme();

        assertNotNull(theme.cardBackground());
        assertNotNull(theme.cardBorder());
        assertNotNull(theme.inputBackground());
        assertNotNull(theme.focusBorder());
        assertNotEquals(theme.numberButtonBackground(), theme.operatorButtonBackground());
        assertNotEquals(theme.canvasBackground(), theme.gridColor());
        assertTrue(contrast(theme.numberButtonBackground(), theme.numberButtonForeground()) >= 4.5);
        assertTrue(contrast(theme.operatorButtonBackground(), theme.operatorButtonForeground()) >= 3.0);
    }

    private double contrast(Color a, Color b)
    {
        double lighter = Math.max(luminance(a), luminance(b));
        double darker = Math.min(luminance(a), luminance(b));
        return (lighter + 0.05) / (darker + 0.05);
    }

    private double luminance(Color color)
    {
        return 0.2126 * channel(color.getRed()) + 0.7152 * channel(color.getGreen()) + 0.0722 * channel(color.getBlue());
    }

    private double channel(int value)
    {
        double normalized = value / 255.0;
        return normalized <= 0.03928
                ? normalized / 12.92
                : Math.pow((normalized + 0.055) / 1.055, 2.4);
    }
}
