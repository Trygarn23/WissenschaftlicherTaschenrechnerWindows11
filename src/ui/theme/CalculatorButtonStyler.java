package ui.theme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

/** Gemeinsame Theme-Zuordnung fuer Standard- und Wissenschaftlich-Buttons. */
public final class CalculatorButtonStyler
{
    private CalculatorButtonStyler()
    {
    }

    public static void stylePanel(Container container, AppTheme theme)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button)
            {
                styleButton(button, theme);
            }
            else if (component instanceof Container child)
            {
                stylePanel(child, theme);
            }
        }
    }

    public static void styleButton(JButton button, AppTheme theme)
    {
        Color background;
        Color foreground;
        String text = button.getText();

        if (text != null && text.matches("\\d"))
        {
            background = theme.numberButtonBackground();
            foreground = theme.numberButtonForeground();
        }
        else if (text != null && "+-\u00d7\u00f7".contains(text))
        {
            background = theme.operatorButtonBackground();
            foreground = theme.operatorButtonForeground();
        }
        else if ("C".equals(text) || "CE".equals(text) || "\u2190".equals(text))
        {
            background = theme.specialButtonBackground();
            foreground = theme.specialButtonForeground();
        }
        else
        {
            background = theme.functionButtonBackground();
            foreground = theme.functionButtonForeground();
        }

        ModernButtonStyler.styleButton(
                button,
                theme,
                background,
                foreground,
                Boolean.TRUE.equals(button.getClientProperty("keyboardFocusable"))
        );
    }
}
