package Theme;

import java.awt.Color;
import java.awt.Font;

public interface AppTheme
{
    String getDisplayName();

    Color windowBackground();
    Color panelBackground();

    Color displayBackground();
    Color displayForeground();
    Color secondaryDisplayForeground();

    Color historyBackground();
    Color historyForeground();
    Color historySelectionBackground();
    Color historySearchBackground();
    Color placeholderForeground();

    Color modeBarBackground();
    Color modeButtonActiveBackground();
    Color modeButtonInactiveBackground();
    Color modeBorder();

    Color numberButtonBackground();
    Color numberButtonForeground();

    Color operatorButtonBackground();
    Color operatorButtonForeground();

    Color functionButtonBackground();
    Color functionButtonForeground();

    Color specialButtonBackground();
    Color specialButtonForeground();

    Color toggleButtonBackground();
    Color toggleButtonForeground();

    Font buttonFont();
    Font displayFont();
    Font secondaryDisplayFont();


}