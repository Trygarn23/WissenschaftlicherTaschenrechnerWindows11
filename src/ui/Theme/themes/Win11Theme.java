package ui.theme.themes;

import ui.theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class Win11Theme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 48);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 22);

    @Override
    public String getDisplayName()
    {
        return "Win11";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(250, 250, 250); // #FAFAFA
    }

    @Override
    public Color panelBackground()
    {
        return new Color(245, 245, 245); // #F5F5F5
    }

    @Override
    public Color displayBackground()
    {
        return new Color(255, 255, 255); // #FFFFFF
    }

    @Override
    public Color displayForeground()
    {
        return new Color(36, 36, 36); // #242424
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(92, 92, 92); // #5C5C5C
    }

    @Override
    public Color historyBackground()
    {
        return new Color(255, 255, 255); // #FFFFFF
    }

    @Override
    public Color historyForeground()
    {
        return new Color(36, 36, 36); // #242424
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(235, 243, 252); // #EBF3FC
    }

    @Override
    public Color historySearchBackground()
    {
        return new Color(245, 245, 245); // #F5F5F5
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(158, 158, 158); // #9E9E9E
    }

    @Override
    public Color modeBarBackground()
    {
        return new Color(245, 245, 245); // #F5F5F5
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(15, 108, 189); // #0F6CBD
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(235, 235, 235); // #EBEBEB
    }

    @Override
    public Color modeBorder()
    {
        return new Color(209, 209, 209); // #D1D1D1
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(255, 255, 255); // #FFFFFF
    }

    @Override
    public Color numberButtonForeground()
    {
        return new Color(36, 36, 36); // #242424
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(15, 108, 189); // #0F6CBD
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(245, 245, 245); // #F5F5F5
    }

    @Override
    public Color functionButtonForeground()
    {
        return new Color(36, 36, 36); // #242424
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(235, 235, 235); // #EBEBEB
    }

    @Override
    public Color specialButtonForeground()
    {
        return new Color(36, 36, 36); // #242424
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(207, 228, 250); // #CFE4FA
    }

    @Override
    public Color toggleButtonForeground()
    {
        return new Color(17, 94, 163); // #115EA3
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