package Theme.themes;

import Theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

public class Win95Theme implements AppTheme
{
    private static final Font BUTTON_FONT = new Font("Dialog", Font.PLAIN, 18);
    private static final Font DISPLAY_FONT = new Font("Dialog", Font.PLAIN, 48);
    private static final Font SECONDARY_DISPLAY_FONT = new Font("Dialog", Font.PLAIN, 22);

    @Override
    public String getDisplayName()
    {
        return "Win95";
    }

    @Override
    public Color windowBackground()
    {
        return new Color(192, 192, 192);
    }

    @Override
    public Color panelBackground()
    {
        return new Color(192, 192, 192);
    }

    @Override
    public Color displayBackground()
    {
        return new Color(255, 255, 255);
    }

    @Override
    public Color displayForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color secondaryDisplayForeground()
    {
        return new Color(80, 80, 80);
    }

    @Override
    public Color historyBackground()
    {
        return new Color(212, 208, 200);
    }

    @Override
    public Color historyForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color historySelectionBackground()
    {
        return new Color(10, 36, 106);
    }

    @Override
    public Color historySearchBackground()
    {
        return Color.WHITE;
    }

    @Override
    public Color placeholderForeground()
    {
        return new Color(120, 120, 120);
    }

    @Override
    public Color modeBarBackground()
    {
        return new Color(192, 192, 192);
    }

    @Override
    public Color modeButtonActiveBackground()
    {
        return new Color(10, 36, 106);
    }

    @Override
    public Color modeButtonInactiveBackground()
    {
        return new Color(212, 208, 200);
    }

    @Override
    public Color modeBorder()
    {
        return new Color(128, 128, 128);
    }

    @Override
    public Color numberButtonBackground()
    {
        return new Color(212, 208, 200);
    }

    @Override
    public Color numberButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color operatorButtonBackground()
    {
        return new Color(160, 160, 160);
    }

    @Override
    public Color operatorButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color functionButtonBackground()
    {
        return new Color(212, 208, 200);
    }

    @Override
    public Color functionButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color specialButtonBackground()
    {
        return new Color(180, 180, 180);
    }

    @Override
    public Color specialButtonForeground()
    {
        return Color.BLACK;
    }

    @Override
    public Color toggleButtonBackground()
    {
        return new Color(212, 208, 200);
    }

    @Override
    public Color toggleButtonForeground()
    {
        return Color.BLACK;
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