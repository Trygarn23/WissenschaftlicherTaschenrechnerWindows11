package ui.theme.themes;

import java.awt.Color;

public class MatrixTheme extends BasisTheme
{
    public MatrixTheme()
    {
        super("Matrix", "Consolas");

        windowBackground = new Color(0, 0, 0);
        panelBackground = new Color(6, 14, 6);
        displayBackground = new Color(0, 0, 0);
        displayForeground = new Color(0, 255, 110);
        secondaryDisplayForeground = new Color(0, 170, 80);
        historyBackground = new Color(2, 10, 2);
        historyForeground = new Color(0, 255, 110);
        historySelectionBackground = new Color(0, 90, 35);
        historySearchBackground = new Color(8, 18, 8);
        placeholderForeground = new Color(0, 110, 45);
        modeBarBackground = new Color(4, 12, 4);
        modeButtonActiveBackground = new Color(0, 120, 45);
        modeButtonInactiveBackground = new Color(10, 24, 10);
        modeBorder = new Color(0, 85, 30);
        numberButtonBackground = new Color(12, 24, 12);
        numberButtonForeground = new Color(0, 255, 110);
        operatorButtonBackground = new Color(0, 140, 55);
        operatorButtonForeground = Color.BLACK;
        functionButtonBackground = new Color(8, 18, 8);
        functionButtonForeground = new Color(0, 230, 100);
        specialButtonBackground = new Color(0, 110, 45);
        specialButtonForeground = Color.BLACK;
        toggleButtonBackground = new Color(0, 95, 38);
        toggleButtonForeground = new Color(180, 255, 200);
    }
}
