package ui.theme.themes;

import java.awt.Color;

public class DarkTheme extends BasisTheme
{
    public DarkTheme()
    {
        super("Dark", "Segoe UI");

        windowBackground = new Color(25, 25, 25);
        panelBackground = new Color(25, 25, 25);
        displayBackground = new Color(25, 25, 25);
        displayForeground = Color.WHITE;
        secondaryDisplayForeground = new Color(180, 180, 180);
        historyBackground = new Color(25, 25, 25);
        historyForeground = Color.WHITE;
        historySelectionBackground = new Color(55, 55, 55);
        historySearchBackground = new Color(35, 35, 35);
        placeholderForeground = new Color(140, 140, 140);
        modeBarBackground = new Color(18, 22, 30);
        modeButtonActiveBackground = new Color(0, 145, 210);
        modeButtonInactiveBackground = new Color(34, 39, 52);
        modeBorder = new Color(58, 66, 84);
        numberButtonBackground = new Color(45, 45, 45);
        numberButtonForeground = Color.WHITE;
        operatorButtonBackground = new Color(232, 89, 147);
        operatorButtonForeground = Color.BLACK;
        functionButtonBackground = new Color(60, 60, 60);
        functionButtonForeground = Color.WHITE;
        specialButtonBackground = new Color(31, 137, 138);
        specialButtonForeground = Color.WHITE;
        toggleButtonBackground = new Color(67, 196, 192);
        toggleButtonForeground = Color.WHITE;
    }
}
