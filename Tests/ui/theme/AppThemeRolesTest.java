package ui.theme;

import org.junit.jupiter.api.Test;
import ui.theme.themes.DarkTheme;
import ui.theme.themes.LightTheme;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppThemeRolesTest
{
    @Test
    void defaultRoles_ShouldProvideStateCanvasAndPopupColors()
    {
        AppTheme theme = new DarkTheme();

        assertNotNull(theme.disabledButtonBackground());
        assertNotNull(theme.disabledButtonForeground());
        assertNotNull(theme.hoverBackground(theme.numberButtonBackground()));
        assertNotNull(theme.pressedBackground(theme.numberButtonBackground()));
        assertEquals(theme.operatorButtonBackground(), theme.dangerBackground());
        assertEquals(theme.displayBackground(), theme.canvasBackground());
        assertEquals(theme.panelBackground(), theme.popupBackground());
    }

    @Test
    void gridColor_ShouldBeDerivedFromCanvasAndForeground()
    {
        AppTheme theme = new DarkTheme();

        assertEquals(theme.blend(theme.canvasBackground(), theme.displayForeground(), 0.18), theme.gridColor());
    }

    @Test
    void contrastForeground_ShouldChooseReadableTextForDarkAndLightBackgrounds()
    {
        AppTheme theme = new LightTheme();

        assertEquals(Color.WHITE, theme.contrastForeground(Color.BLACK));
        assertEquals(Color.BLACK, theme.contrastForeground(Color.WHITE));
    }

    @Test
    void interactionColors_ShouldDifferFromBaseColor()
    {
        AppTheme theme = new DarkTheme();
        Color base = theme.numberButtonBackground();

        assertNotEquals(base, theme.hoverBackground(base));
        assertNotEquals(base, theme.pressedBackground(base));
    }
}
