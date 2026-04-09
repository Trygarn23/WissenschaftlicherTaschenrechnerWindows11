package Theme.themes;

import Theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class DarkTheme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 48);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 22);

    @Override
    public String getDisplayName()
    {
        return "Dark";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(25, 25, 25);
    }

    @Override
    public Color panelBackground()
    {
        return new Color(25, 25, 25);
    }

    @Override
    public Color displayBackground()
    {
        return new Color(25, 25, 25);
    }

    @Override
    public Color displayForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(180, 180, 180);
    }

    @Override
    public Color historyBackground()
    {
        return new Color(25, 25, 25);
    }

    @Override
    public Color historyForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(55, 55, 55);
    }

    @Override
    public Color historySearchBackground()
    {
        return new Color(35, 35, 35);
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(140, 140, 140);
    }

    @Override
    public Color modeBarBackground()
    {
        return new Color(18, 22, 30);
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(0, 145, 210);
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(34, 39, 52);
    }

    @Override
    public Color modeBorder()
    {
        return new Color(58, 66, 84);
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(45, 45, 45);
    }

    @Override
    public Color numberButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(232, 89, 147);
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(60, 60, 60);
    }

    @Override
    public Color functionButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(31, 137, 138);
    }

    @Override
    public Color specialButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(67, 196, 192);
    }

    @Override
    public Color toggleButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Font buttonFont()
    {
        return BUTTON_FONT;
    }

    @Override
    public Font displayFont()
    {
        return DISPLAY_FONT;
    }

    @Override
    public Font secondaryDisplayFont()
    {
        return SECONDARY_DISPLAY_FONT;
    }
}
