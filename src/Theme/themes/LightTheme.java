package Theme.themes;

import Theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class LightTheme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 48);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 22);

    @Override
    public String getDisplayName()
    {
        return "Light";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color panelBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color displayBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color displayForeground()
    {
        return new Color(20, 20, 20);
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(110, 110, 110);
    }

    @Override
    public Color historyBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color historyForeground()
    {
        return new Color(30, 30, 30);
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color historySearchBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(140, 140, 140);
    }

    @Override
    public Color modeBarBackground()
    {
        return new Color(212, 212, 212);
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(250, 161, 81);
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(130, 207, 197);
    }

    @Override
    public Color modeBorder()
    {
        return new Color(252, 243, 226);
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(255, 216, 114);
    }

    @Override
    public Color numberButtonForeground()
    {
        return new Color(25, 25, 25);
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(250, 161, 81);
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(250, 161, 81);
    }

    @Override
    public Color functionButtonForeground()
    {
        return new Color(25, 25, 25);
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(0, 156, 119);
    }

    @Override
    public Color specialButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(130, 207, 197);
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