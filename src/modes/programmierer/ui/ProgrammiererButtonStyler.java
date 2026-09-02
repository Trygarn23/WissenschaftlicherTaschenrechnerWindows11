package modes.programmierer.ui;

import ui.theme.AppTheme;

import java.awt.Color;

final class ProgrammiererButtonStyler
{
    private ProgrammiererButtonStyler()
    {
    }

    static Color buttonBackground(String text, AppTheme theme)
    {
        if (isDigit(text))
        {
            return theme.numberButtonBackground();
        }

        if (isOperator(text) || isBitOperation(text))
        {
            return theme.operatorButtonBackground();
        }

        if (isToggle(text))
        {
            return theme.toggleButtonBackground();
        }

        if (isSpecial(text))
        {
            return theme.specialButtonBackground();
        }

        return theme.functionButtonBackground();
    }

    static Color buttonForeground(String text, AppTheme theme)
    {
        if (isDigit(text))
        {
            return theme.numberButtonForeground();
        }

        if (isOperator(text) || isBitOperation(text))
        {
            return theme.operatorButtonForeground();
        }

        if (isToggle(text))
        {
            return theme.toggleButtonForeground();
        }

        if (isSpecial(text))
        {
            return theme.specialButtonForeground();
        }

        return theme.functionButtonForeground();
    }

    static Color modeButtonBackground(AppTheme theme, boolean active)
    {
        return active ? theme.modeButtonActiveBackground() : theme.modeButtonInactiveBackground();
    }

    static Color modeButtonForeground(AppTheme theme, boolean active)
    {
        return theme.modeButtonForeground(active);
    }

    static Color disabledBackground(AppTheme theme)
    {
        return theme.disabledButtonBackground();
    }

    static Color disabledForeground(AppTheme theme)
    {
        return theme.disabledButtonForeground();
    }

    static Color hoverBackground(Color color, AppTheme theme)
    {
        return theme.hoverBackground(color);
    }

    static Color pressedBackground(Color color, AppTheme theme)
    {
        return theme.pressedBackground(color);
    }

    private static boolean isDigit(String text)
    {
        return text != null && text.matches("[0-9A-F]");
    }

    private static boolean isOperator(String text)
    {
        return "+".equals(text) || "-".equals(text);
    }

    private static boolean isBitOperation(String text)
    {
        return "NOT".equals(text) || "AND".equals(text) || "OR".equals(text) || "XOR".equals(text)
                || "<<".equals(text) || ">>".equals(text) || ">>>".equals(text);
    }

    private static boolean isToggle(String text)
    {
        return "SIGNED".equals(text) || "UNSIGNED".equals(text);
    }

    private static boolean isSpecial(String text)
    {
        return "CLR".equals(text) || "←".equals(text) || "=".equals(text) || "±".equals(text);
    }
}
