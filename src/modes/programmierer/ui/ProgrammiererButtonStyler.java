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
        return active ? readableForeground(theme.modeButtonActiveBackground()) : theme.functionButtonForeground();
    }

    static Color disabledBackground(AppTheme theme)
    {
        return mix(theme.functionButtonBackground(), theme.panelBackground(), 0.65);
    }

    static Color disabledForeground(AppTheme theme)
    {
        return mix(theme.functionButtonForeground(), disabledBackground(theme), 0.45);
    }

    static Color hoverBackground(Color color)
    {
        return adjustForInteraction(color, 0.14);
    }

    static Color pressedBackground(Color color)
    {
        return adjustForInteraction(color, -0.16);
    }

    private static Color adjustForInteraction(Color color, double strength)
    {
        Color target = luminance(color) < 0.5 ? Color.WHITE : Color.BLACK;
        return mix(color, target, Math.abs(strength));
    }

    private static Color readableForeground(Color background)
    {
        return luminance(background) < 0.55 ? Color.WHITE : Color.BLACK;
    }

    private static Color mix(Color base, Color target, double targetWeight)
    {
        double baseWeight = 1.0 - targetWeight;
        return new Color(
                clamp((int) Math.round(base.getRed() * baseWeight + target.getRed() * targetWeight)),
                clamp((int) Math.round(base.getGreen() * baseWeight + target.getGreen() * targetWeight)),
                clamp((int) Math.round(base.getBlue() * baseWeight + target.getBlue() * targetWeight))
        );
    }

    private static int clamp(int value)
    {
        return Math.max(0, Math.min(255, value));
    }

    private static double luminance(Color color)
    {
        return (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255.0;
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
        return "CLR".equals(text) || "â†".equals(text) || "=".equals(text) || "Â±".equals(text);
    }
}
