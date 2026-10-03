package ui.theme;

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

    default Color cardBackground()
    {
        return blend(panelBackground(), displayBackground(), 0.55);
    }

    default Color cardBorder()
    {
        return blend(panelBackground(), displayForeground(), 0.20);
    }

    default Color inputBackground()
    {
        return historySearchBackground();
    }

    default Color inputBorder()
    {
        return blend(inputBackground(), displayForeground(), 0.20);
    }

    default Color focusBorder()
    {
        return modeButtonActiveBackground();
    }

    default Color softAccentBackground()
    {
        return blend(panelBackground(), modeButtonActiveBackground(), 0.20);
    }

    default Color dangerSoftBackground()
    {
        return blend(panelBackground(), dangerBackground(), 0.22);
    }

    default Color successPulseColor()
    {
        return blend(panelBackground(), graphNullstelleColor(), 0.35);
    }

    default Color errorPulseColor()
    {
        return blend(panelBackground(), dangerBackground(), 0.35);
    }

    default Color menuHoverBackground()
    {
        return hoverBackground(popupOptionBackground());
    }

    default Color menuActiveBackground()
    {
        return popupSelectedBackground();
    }

    default Color disabledButtonBackground()
    {
        return blend(functionButtonBackground(), panelBackground(), 0.65);
    }

    default Color disabledButtonForeground()
    {
        return blend(functionButtonForeground(), disabledButtonBackground(), 0.45);
    }

    default Color hoverBackground(Color base)
    {
        return blend(base, contrastForeground(base), 0.14);
    }

    default Color pressedBackground(Color base)
    {
        return blend(base, contrastForeground(base), 0.16);
    }

    default Color dangerBackground()
    {
        return operatorButtonBackground();
    }

    default Color dangerForeground()
    {
        return operatorButtonForeground();
    }

    default Color canvasBackground()
    {
        return displayBackground();
    }

    default Color gridColor()
    {
        return blend(canvasBackground(), displayForeground(), 0.18);
    }

    default Color graphNullstelleColor()
    {
        return new Color(30, 190, 120);
    }

    default Color graphExtremumColor()
    {
        return new Color(255, 190, 60);
    }

    default Color graphWendestelleColor()
    {
        return new Color(190, 120, 255);
    }

    default Color graphYAchseColor()
    {
        return new Color(70, 190, 255);
    }

    default Color popupBackground()
    {
        return panelBackground();
    }

    default Color popupForeground()
    {
        return displayForeground();
    }

    default Color popupOptionBackground()
    {
        return functionButtonBackground();
    }

    default Color popupOptionForeground()
    {
        return functionButtonForeground();
    }

    default Color popupSelectedBackground()
    {
        return modeButtonActiveBackground();
    }

    default Color popupSelectedForeground()
    {
        return contrastForeground(popupSelectedBackground());
    }

    default Color modeButtonForeground(boolean active)
    {
        return active ? contrastForeground(modeButtonActiveBackground()) : contrastForeground(modeButtonInactiveBackground());
    }

    default Color contrastForeground(Color background)
    {
        double luminance = (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255.0;
        return luminance > 0.58 ? Color.BLACK : Color.WHITE;
    }

    default Color blend(Color base, Color target, double targetWeight)
    {
        double targetPart = Math.max(0.0, Math.min(1.0, targetWeight));
        double basePart = 1.0 - targetPart;
        return new Color(
                clampColor((int) Math.round(base.getRed() * basePart + target.getRed() * targetPart)),
                clampColor((int) Math.round(base.getGreen() * basePart + target.getGreen() * targetPart)),
                clampColor((int) Math.round(base.getBlue() * basePart + target.getBlue() * targetPart))
        );
    }

    Font buttonFont();
    Font displayFont();
    Font secondaryDisplayFont();

    private static int clampColor(int value)
    {
        return Math.max(0, Math.min(255, value));
    }

}
