package ui.theme.custom;

import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import java.awt.Color;
import java.awt.Font;

public class CustomTheme implements AppTheme
{
    private final CustomThemeColors colors;
    private final AppTheme fallback = new DarkTheme();

    public CustomTheme(CustomThemeColors colors)
    {
        this.colors = colors == null ? CustomThemeColors.DEFAULT : colors;
    }

    @Override
    public String getDisplayName()
    {
        return "Custom";
    }

    @Override
    public Color windowBackground()
    {
        return colors.panelBackground();
    }

    @Override
    public Color panelBackground()
    {
        return colors.panelBackground();
    }

    @Override
    public Color displayBackground()
    {
        return colors.displayBackground();
    }

    @Override
    public Color displayForeground()
    {
        return colors.displayForeground();
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return blend(colors.displayBackground(), colors.displayForeground(), 0.68);
    }

    @Override
    public Color historyBackground()
    {
        return colors.panelBackground();
    }

    @Override
    public Color historyForeground()
    {
        return contrastForeground(colors.panelBackground());
    }

    @Override
    public Color historySelectionBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color historySearchBackground()
    {
        return blend(colors.panelBackground(), Color.BLACK, 0.18);
    }

    @Override
    public Color placeholderForeground()
    {
        return blend(colors.panelBackground(), contrastForeground(colors.panelBackground()), 0.55);
    }

    @Override
    public Color modeBarBackground()
    {
        return colors.panelBackground();
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return colors.functionButtonBackground();
    }

    @Override
    public Color modeBorder()
    {
        return blend(colors.panelBackground(), colors.accentBackground(), 0.55);
    }

    @Override
    public Color numberButtonBackground()
    {
        return colors.numberButtonBackground();
    }

    @Override
    public Color numberButtonForeground()
    {
        return contrastForeground(colors.numberButtonBackground());
    }

    @Override
    public Color operatorButtonBackground()
    {
        return colors.operatorButtonBackground();
    }

    @Override
    public Color operatorButtonForeground()
    {
        return contrastForeground(colors.operatorButtonBackground());
    }

    @Override
    public Color functionButtonBackground()
    {
        return colors.functionButtonBackground();
    }

    @Override
    public Color functionButtonForeground()
    {
        return contrastForeground(colors.functionButtonBackground());
    }

    @Override
    public Color specialButtonBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color specialButtonForeground()
    {
        return contrastForeground(colors.accentBackground());
    }

    @Override
    public Color toggleButtonBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color toggleButtonForeground()
    {
        return contrastForeground(colors.accentBackground());
    }

    @Override
    public Font buttonFont()
    {
        return fallback.buttonFont();
    }

    @Override
    public Font displayFont()
    {
        return fallback.displayFont();
    }

    @Override
    public Font secondaryDisplayFont()
    {
        return fallback.secondaryDisplayFont();
    }

}
