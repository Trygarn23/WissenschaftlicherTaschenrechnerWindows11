package Theme.themes;

import Theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class NeonTheme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 48);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 22);

    @Override
    public String getDisplayName()
    {
        return "Neon";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(12, 10, 20);
    }

    @Override
    public Color panelBackground()
    {
        return new Color(18, 16, 30);
    }

    @Override
    public Color displayBackground()
    {
        return new Color(12, 10, 20);
    }

    @Override
    public Color displayForeground()
    {
        return new Color(245, 245, 255);
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(145, 235, 255);
    }

    @Override
    public Color historyBackground()
    {
        return new Color(14, 12, 24);
    }

    @Override
    public Color historyForeground()
    {
        return new Color(240, 240, 255);
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(255, 0, 140);
    }

    @Override
    public Color historySearchBackground()
    {
        return new Color(28, 24, 40);
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(150, 150, 180);
    }

    @Override
    public Color modeBarBackground()
    {
        return new Color(16, 18, 34);
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(0, 200, 255);
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(38, 30, 56);
    }

    @Override
    public Color modeBorder()
    {
        return new Color(90, 70, 130);
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(42, 36, 58);
    }

    @Override
    public Color numberButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(255, 0, 140);
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(64, 54, 88);
    }

    @Override
    public Color functionButtonForeground()
    {
        return new Color(240, 240, 255);
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(0, 184, 212);
    }

    @Override
    public Color specialButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(110, 64, 170);
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