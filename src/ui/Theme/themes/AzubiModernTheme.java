package ui.theme.themes;

import ui.theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class AzubiModernTheme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font DISPLAY_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 50);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Segoe UI", Font.PLAIN, 20);

    @Override
    public String getDisplayName()
    {
        return "Azubi Modern";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(244, 247, 251);
    }

    @Override
    public Color panelBackground()
    {
        return new Color(234, 240, 248);
    }

    @Override
    public Color displayBackground()
    {
        return new Color(255, 255, 255);
    }

    @Override
    public Color displayForeground()
    {
        return new Color(30, 38, 54);
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(92, 104, 122);
    }

    @Override
    public Color historyBackground()
    {
        return new Color(255, 255, 255);
    }

    @Override
    public Color historyForeground()
    {
        return displayForeground();
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(222, 236, 255);
    }

    @Override
    public Color historySearchBackground()
    {
        return new Color(248, 250, 253);
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(122, 133, 148);
    }

    @Override
    public Color modeBarBackground()
    {
        return panelBackground();
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(42, 119, 255);
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(255, 255, 255);
    }

    @Override
    public Color modeBorder()
    {
        return new Color(202, 214, 230);
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(255, 255, 255);
    }

    @Override
    public Color numberButtonForeground()
    {
        return displayForeground();
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(183, 74, 52);
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(231, 238, 248);
    }

    @Override
    public Color functionButtonForeground()
    {
        return displayForeground();
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(255, 214, 102);
    }

    @Override
    public Color specialButtonForeground()
    {
        return new Color(56, 48, 20);
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(42, 119, 255);
    }

    @Override
    public Color toggleButtonForeground()
    {
        return Color.WHITE;
    }

    @Override
    public Color dangerBackground()
    {
        return new Color(220, 67, 83);
    }

    @Override
    public Color canvasBackground()
    {
        return new Color(251, 253, 255);
    }

    @Override
    public Color gridColor()
    {
        return new Color(218, 229, 242);
    }

    @Override
    public Color graphNullstelleColor()
    {
        return new Color(18, 150, 112);
    }

    @Override
    public Color graphExtremumColor()
    {
        return new Color(240, 145, 48);
    }

    @Override
    public Color graphWendestelleColor()
    {
        return new Color(126, 93, 238);
    }

    @Override
    public Color graphYAchseColor()
    {
        return new Color(42, 119, 255);
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
