package ui.theme.themes;

import java.awt.Color;

public class Win95Theme extends BasisTheme
{
    public Win95Theme()
    {
        super("Win95", "Dialog");

        windowBackground = new Color(192, 192, 192);
        panelBackground = new Color(192, 192, 192);
        displayBackground = new Color(255, 255, 255);
        displayForeground = Color.BLACK;
        secondaryDisplayForeground = new Color(80, 80, 80);
        historyBackground = new Color(212, 208, 200);
        historyForeground = Color.BLACK;
        historySelectionBackground = new Color(10, 36, 106);
        historySearchBackground = Color.WHITE;
        placeholderForeground = new Color(120, 120, 120);
        modeBarBackground = new Color(192, 192, 192);
        modeButtonActiveBackground = new Color(10, 36, 106);
        modeButtonInactiveBackground = new Color(212, 208, 200);
        modeBorder = new Color(128, 128, 128);
        numberButtonBackground = new Color(212, 208, 200);
        numberButtonForeground = Color.BLACK;
        operatorButtonBackground = new Color(160, 160, 160);
        operatorButtonForeground = Color.BLACK;
        functionButtonBackground = new Color(212, 208, 200);
        functionButtonForeground = Color.BLACK;
        specialButtonBackground = new Color(180, 180, 180);
        specialButtonForeground = Color.BLACK;
        toggleButtonBackground = new Color(212, 208, 200);
        toggleButtonForeground = Color.BLACK;
    }
}
