package ui.theme.custom;

import java.awt.Color;
import java.util.Objects;

public final class CustomThemeColors
{
    public static final CustomThemeColors DEFAULT = new CustomThemeColors(
            new Color(25, 25, 25),
            new Color(25, 25, 25),
            Color.WHITE,
            new Color(45, 45, 45),
            new Color(232, 89, 147),
            new Color(60, 60, 60),
            new Color(67, 196, 192)
    );

    private final Color panelBackground;
    private final Color displayBackground;
    private final Color displayForeground;
    private final Color numberButtonBackground;
    private final Color operatorButtonBackground;
    private final Color functionButtonBackground;
    private final Color accentBackground;

    public CustomThemeColors(
            Color panelBackground,
            Color displayBackground,
            Color displayForeground,
            Color numberButtonBackground,
            Color operatorButtonBackground,
            Color functionButtonBackground,
            Color accentBackground)
    {
        this.panelBackground = fallback(panelBackground, DEFAULT_PANEL());
        this.displayBackground = fallback(displayBackground, DEFAULT_DISPLAY_BG());
        this.displayForeground = fallback(displayForeground, DEFAULT_DISPLAY_FG());
        this.numberButtonBackground = fallback(numberButtonBackground, DEFAULT_NUMBER_BG());
        this.operatorButtonBackground = fallback(operatorButtonBackground, DEFAULT_OPERATOR_BG());
        this.functionButtonBackground = fallback(functionButtonBackground, DEFAULT_FUNCTION_BG());
        this.accentBackground = fallback(accentBackground, DEFAULT_ACCENT_BG());
    }

    public Color panelBackground()
    {
        return panelBackground;
    }

    public Color displayBackground()
    {
        return displayBackground;
    }

    public Color displayForeground()
    {
        return displayForeground;
    }

    public Color numberButtonBackground()
    {
        return numberButtonBackground;
    }

    public Color operatorButtonBackground()
    {
        return operatorButtonBackground;
    }

    public Color functionButtonBackground()
    {
        return functionButtonBackground;
    }

    public Color accentBackground()
    {
        return accentBackground;
    }

    public CustomThemeColors withPanelBackground(Color color)
    {
        return new CustomThemeColors(color, displayBackground, displayForeground, numberButtonBackground,
                operatorButtonBackground, functionButtonBackground, accentBackground);
    }

    public CustomThemeColors withDisplayBackground(Color color)
    {
        return new CustomThemeColors(panelBackground, color, displayForeground, numberButtonBackground,
                operatorButtonBackground, functionButtonBackground, accentBackground);
    }

    public CustomThemeColors withDisplayForeground(Color color)
    {
        return new CustomThemeColors(panelBackground, displayBackground, color, numberButtonBackground,
                operatorButtonBackground, functionButtonBackground, accentBackground);
    }

    public CustomThemeColors withNumberButtonBackground(Color color)
    {
        return new CustomThemeColors(panelBackground, displayBackground, displayForeground, color,
                operatorButtonBackground, functionButtonBackground, accentBackground);
    }

    public CustomThemeColors withOperatorButtonBackground(Color color)
    {
        return new CustomThemeColors(panelBackground, displayBackground, displayForeground, numberButtonBackground,
                color, functionButtonBackground, accentBackground);
    }

    public CustomThemeColors withFunctionButtonBackground(Color color)
    {
        return new CustomThemeColors(panelBackground, displayBackground, displayForeground, numberButtonBackground,
                operatorButtonBackground, color, accentBackground);
    }

    public CustomThemeColors withAccentBackground(Color color)
    {
        return new CustomThemeColors(panelBackground, displayBackground, displayForeground, numberButtonBackground,
                operatorButtonBackground, functionButtonBackground, color);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof CustomThemeColors that)) return false;
        return panelBackground.equals(that.panelBackground)
                && displayBackground.equals(that.displayBackground)
                && displayForeground.equals(that.displayForeground)
                && numberButtonBackground.equals(that.numberButtonBackground)
                && operatorButtonBackground.equals(that.operatorButtonBackground)
                && functionButtonBackground.equals(that.functionButtonBackground)
                && accentBackground.equals(that.accentBackground);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(panelBackground, displayBackground, displayForeground, numberButtonBackground,
                operatorButtonBackground, functionButtonBackground, accentBackground);
    }

    private static Color fallback(Color color, Color fallback)
    {
        return color == null ? fallback : color;
    }

    private static Color DEFAULT_PANEL()
    {
        return DEFAULT == null ? new Color(25, 25, 25) : DEFAULT.panelBackground;
    }

    private static Color DEFAULT_DISPLAY_BG()
    {
        return DEFAULT == null ? new Color(25, 25, 25) : DEFAULT.displayBackground;
    }

    private static Color DEFAULT_DISPLAY_FG()
    {
        return DEFAULT == null ? Color.WHITE : DEFAULT.displayForeground;
    }

    private static Color DEFAULT_NUMBER_BG()
    {
        return DEFAULT == null ? new Color(45, 45, 45) : DEFAULT.numberButtonBackground;
    }

    private static Color DEFAULT_OPERATOR_BG()
    {
        return DEFAULT == null ? new Color(232, 89, 147) : DEFAULT.operatorButtonBackground;
    }

    private static Color DEFAULT_FUNCTION_BG()
    {
        return DEFAULT == null ? new Color(60, 60, 60) : DEFAULT.functionButtonBackground;
    }

    private static Color DEFAULT_ACCENT_BG()
    {
        return DEFAULT == null ? new Color(67, 196, 192) : DEFAULT.accentBackground;
    }
}
