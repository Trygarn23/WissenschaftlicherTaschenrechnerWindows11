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
        return blend(colors.displayForeground(), colors.displayBackground(), 0.68);
    }

    @Override
    public Color historyBackground()
    {
        return colors.panelBackground();
    }

    @Override
    public Color historyForeground()
    {
        return contrastFor(colors.panelBackground());
    }

    @Override
    public Color historySelectionBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color historySearchBackground()
    {
        return blend(colors.panelBackground(), Color.BLACK, 0.82);
    }

    @Override
    public Color placeholderForeground()
    {
        return blend(contrastFor(colors.panelBackground()), colors.panelBackground(), 0.55);
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
        return blend(colors.accentBackground(), colors.panelBackground(), 0.55);
    }

    @Override
    public Color numberButtonBackground()
    {
        return colors.numberButtonBackground();
    }

    @Override
    public Color numberButtonForeground()
    {
        return contrastFor(colors.numberButtonBackground());
    }

    @Override
    public Color operatorButtonBackground()
    {
        return colors.operatorButtonBackground();
    }

    @Override
    public Color operatorButtonForeground()
    {
        return contrastFor(colors.operatorButtonBackground());
    }

    @Override
    public Color functionButtonBackground()
    {
        return colors.functionButtonBackground();
    }

    @Override
    public Color functionButtonForeground()
    {
        return contrastFor(colors.functionButtonBackground());
    }

    @Override
    public Color specialButtonBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color specialButtonForeground()
    {
        return contrastFor(colors.accentBackground());
    }

    @Override
    public Color toggleButtonBackground()
    {
        return colors.accentBackground();
    }

    @Override
    public Color toggleButtonForeground()
    {
        return contrastFor(colors.accentBackground());
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

    private Color contrastFor(Color color)
    {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
        return luminance > 0.58 ? Color.BLACK : Color.WHITE;
    }

    private Color blend(Color foreground, Color background, double foregroundWeight)
    {
        double fg = Math.max(0.0, Math.min(1.0, foregroundWeight));
        double bg = 1.0 - fg;
        return new Color(
                (int) Math.round(foreground.getRed() * fg + background.getRed() * bg),
                (int) Math.round(foreground.getGreen() * fg + background.getGreen() * bg),
                (int) Math.round(foreground.getBlue() * fg + background.getBlue() * bg)
        );
    }
}
