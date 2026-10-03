package ui.theme.themes;

import java.awt.Color;

public class NeonTheme extends BasisTheme
{
    public NeonTheme()
    {
        super("Neon", "Segoe UI");

        windowBackground = new Color(12, 10, 20);
        panelBackground = new Color(18, 16, 30);
        displayBackground = new Color(12, 10, 20);
        displayForeground = new Color(245, 245, 255);
        secondaryDisplayForeground = new Color(145, 235, 255);
        historyBackground = new Color(14, 12, 24);
        historyForeground = new Color(240, 240, 255);
        historySelectionBackground = new Color(255, 0, 140);
        historySearchBackground = new Color(28, 24, 40);
        placeholderForeground = new Color(150, 150, 180);
        modeBarBackground = new Color(16, 18, 34);
        modeButtonActiveBackground = new Color(0, 200, 255);
        modeButtonInactiveBackground = new Color(38, 30, 56);
        modeBorder = new Color(90, 70, 130);
        numberButtonBackground = new Color(42, 36, 58);
        numberButtonForeground = Color.WHITE;
        operatorButtonBackground = new Color(255, 0, 140);
        operatorButtonForeground = Color.WHITE;
        functionButtonBackground = new Color(64, 54, 88);
        functionButtonForeground = new Color(240, 240, 255);
        specialButtonBackground = new Color(0, 184, 212);
        specialButtonForeground = Color.WHITE;
        toggleButtonBackground = new Color(110, 64, 170);
        toggleButtonForeground = Color.WHITE;
    }
}
