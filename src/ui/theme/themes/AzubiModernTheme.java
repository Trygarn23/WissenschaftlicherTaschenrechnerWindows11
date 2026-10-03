package ui.theme.themes;

import java.awt.Color;
import java.awt.Font;

public class AzubiModernTheme extends BasisTheme
{
    public AzubiModernTheme()
    {
        super("Azubi Modern",
                new Font("Segoe UI", Font.BOLD, 16),
                new Font("Segoe UI Semibold", Font.PLAIN, 50),
                new Font("Segoe UI", Font.PLAIN, 20));

        windowBackground = new Color(244, 247, 251);
        panelBackground = new Color(234, 240, 248);
        displayBackground = new Color(255, 255, 255);
        displayForeground = new Color(30, 38, 54);
        secondaryDisplayForeground = new Color(92, 104, 122);
        historyBackground = new Color(255, 255, 255);
        historyForeground = displayForeground;
        historySelectionBackground = new Color(222, 236, 255);
        historySearchBackground = new Color(248, 250, 253);
        placeholderForeground = new Color(122, 133, 148);
        modeBarBackground = panelBackground;
        modeButtonActiveBackground = new Color(42, 119, 255);
        modeButtonInactiveBackground = new Color(255, 255, 255);
        modeBorder = new Color(202, 214, 230);
        numberButtonBackground = new Color(255, 255, 255);
        numberButtonForeground = displayForeground;
        operatorButtonBackground = new Color(183, 74, 52);
        operatorButtonForeground = Color.WHITE;
        functionButtonBackground = new Color(231, 238, 248);
        functionButtonForeground = displayForeground;
        specialButtonBackground = new Color(255, 214, 102);
        specialButtonForeground = new Color(56, 48, 20);
        toggleButtonBackground = new Color(42, 119, 255);
        toggleButtonForeground = Color.WHITE;
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
}
