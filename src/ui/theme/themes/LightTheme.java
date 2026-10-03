package ui.theme.themes;

import java.awt.Color;

public class LightTheme extends BasisTheme
{
    public LightTheme()
    {
        super("Light", "Segoe UI");

        windowBackground = new Color(212, 212, 212);
        panelBackground = new Color(212, 212, 212);
        displayBackground = new Color(212, 212, 212);
        displayForeground = new Color(20, 20, 20);
        secondaryDisplayForeground = new Color(110, 110, 110);
        historyBackground = new Color(212, 212, 212);
        historyForeground = new Color(30, 30, 30);
        historySelectionBackground = new Color(212, 212, 212);
        historySearchBackground = new Color(212, 212, 212);
        placeholderForeground = new Color(140, 140, 140);
        modeBarBackground = new Color(212, 212, 212);
        modeButtonActiveBackground = new Color(250, 161, 81);
        modeButtonInactiveBackground = new Color(130, 207, 197);
        modeBorder = new Color(252, 243, 226);
        numberButtonBackground = new Color(255, 216, 114);
        numberButtonForeground = new Color(25, 25, 25);
        operatorButtonBackground = new Color(250, 161, 81);
        operatorButtonForeground = Color.BLACK;
        functionButtonBackground = new Color(250, 161, 81);
        functionButtonForeground = new Color(25, 25, 25);
        specialButtonBackground = new Color(0, 156, 119);
        specialButtonForeground = Color.WHITE;
        toggleButtonBackground = new Color(130, 207, 197);
        toggleButtonForeground = Color.WHITE;
    }
}
