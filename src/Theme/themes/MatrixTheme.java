package Theme.themes;

import Theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class MatrixTheme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Consolas", Font.PLAIN, 18);
    private static final Font DISPLAY_FONT = new Font("Consolas", Font.PLAIN, 48);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Consolas", Font.PLAIN, 22);

    @Override
    public String getDisplayName()
    {
        return "Matrix";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(0, 0, 0);
    }

    @Override
    public Color panelBackground()
    {
        return new Color(6, 14, 6);
    }

    @Override
    public Color displayBackground()
    {
        return new Color(0, 0, 0);
    }

    @Override
    public Color displayForeground()
    {
        return new Color(0, 255, 110);
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(0, 170, 80);
    }

    @Override
    public Color historyBackground()
    {
        return new Color(2, 10, 2);
    }

    @Override
    public Color historyForeground()
    {
        return new Color(0, 255, 110);
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(0, 90, 35);
    }

    @Override
    public Color historySearchBackground()
    {
        return new Color(8, 18, 8);
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(0, 110, 45);
    }

    @Override
    public Color modeBarBackground()
    {
        return new Color(4, 12, 4);
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(0, 120, 45);
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(10, 24, 10);
    }

    @Override
    public Color modeBorder()
    {
        return new Color(0, 85, 30);
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(12, 24, 12);
    }

    @Override
    public Color numberButtonForeground()
    {
        return new Color(0, 255, 110);
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(0, 140, 55);
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(8, 18, 8);
    }

    @Override
    public Color functionButtonForeground()
    {
        return new Color(0, 230, 100);
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(0, 110, 45);
    }

    @Override
    public Color specialButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(0, 95, 38);
    }

    @Override
    public Color toggleButtonForeground()
    {
        return new Color(180, 255, 200);
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