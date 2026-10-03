package ui.theme.themes;

import java.awt.Color;

public class Win11Theme extends BasisTheme
{
    public Win11Theme()
    {
        super("Win11", "Segoe UI");

        windowBackground = new Color(250, 250, 250); // #FAFAFA;
        panelBackground = new Color(245, 245, 245); // #F5F5F5;
        displayBackground = new Color(255, 255, 255); // #FFFFFF;
        displayForeground = new Color(36, 36, 36); // #242424;
        secondaryDisplayForeground = new Color(92, 92, 92); // #5C5C5C;
        historyBackground = new Color(255, 255, 255); // #FFFFFF;
        historyForeground = new Color(36, 36, 36); // #242424;
        historySelectionBackground = new Color(235, 243, 252); // #EBF3FC;
        historySearchBackground = new Color(245, 245, 245); // #F5F5F5;
        placeholderForeground = new Color(158, 158, 158); // #9E9E9E;
        modeBarBackground = new Color(245, 245, 245); // #F5F5F5;
        modeButtonActiveBackground = new Color(15, 108, 189); // #0F6CBD;
        modeButtonInactiveBackground = new Color(235, 235, 235); // #EBEBEB;
        modeBorder = new Color(209, 209, 209); // #D1D1D1;
        numberButtonBackground = new Color(255, 255, 255); // #FFFFFF;
        numberButtonForeground = new Color(36, 36, 36); // #242424;
        operatorButtonBackground = new Color(15, 108, 189); // #0F6CBD;
        operatorButtonForeground = Color.WHITE;
        functionButtonBackground = new Color(245, 245, 245); // #F5F5F5;
        functionButtonForeground = new Color(36, 36, 36); // #242424;
        specialButtonBackground = new Color(235, 235, 235); // #EBEBEB;
        specialButtonForeground = new Color(36, 36, 36); // #242424;
        toggleButtonBackground = new Color(207, 228, 250); // #CFE4FA;
        toggleButtonForeground = new Color(17, 94, 163); // #115EA3;
    }
}
