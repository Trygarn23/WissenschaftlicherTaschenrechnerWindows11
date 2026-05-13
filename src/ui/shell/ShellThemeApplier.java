package ui.shell;

import common.state.WinkelModus;
import modes.graph.ui.GraphPanel;
import modes.komplex.ui.KomplexPanel;
import modes.matrix.ui.MatrixPanel;
import modes.programmierer.ui.ProgrammiererPanel;
import modes.statistik.ui.StatistikPanel;
import modes.wissenschaftlich.ui.WissenschaftlichPanel;
import ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

final class ShellThemeApplier
{
    private ShellThemeApplier()
    {
    }

    static void applyThemeRecursively(Component component, AppTheme theme, WinkelModus winkelModus)
    {
        if (component instanceof ProgrammiererPanel programmiererPanel)
        {
            programmiererPanel.applyTheme(theme);
            return;
        }

        if (component instanceof GraphPanel graphPanel)
        {
            graphPanel.setWinkelModus(winkelModus);
            graphPanel.applyTheme(theme);
            return;
        }

        if (component instanceof KomplexPanel komplexPanel)
        {
            komplexPanel.applyTheme(theme);
            return;
        }

        if (component instanceof MatrixPanel matrixPanel)
        {
            matrixPanel.applyTheme(theme);
            return;
        }

        if (component instanceof StatistikPanel statistikPanel)
        {
            statistikPanel.applyTheme(theme);
            return;
        }

        if (component instanceof WissenschaftlichPanel wissenschaftlichPanel)
        {
            wissenschaftlichPanel.applyTheme(theme);
        }

        if (component instanceof JButton button)
        {
            styleButton(button, theme);
        }
        else if (component instanceof JLabel label)
        {
            label.setForeground(theme.displayForeground());
        }
        else if (component instanceof JPanel panel)
        {
            panel.setBackground(theme.panelBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeRecursively(child, theme, winkelModus);
            }
        }
    }

    static void styleButton(JButton button, AppTheme theme)
    {
        button.setFont(theme.buttonFont());
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFocusable(Boolean.TRUE.equals(button.getClientProperty("keyboardFocusable")));

        ButtonColors colors = colorsFor(button.getText(), theme);
        button.setBackground(colors.background());
        button.setForeground(colors.foreground());
    }

    private static ButtonColors colorsFor(String text, AppTheme theme)
    {
        if (text != null && text.matches("\\d"))
        {
            return new ButtonColors(theme.numberButtonBackground(), theme.numberButtonForeground());
        }

        if (text != null && "+-\u00d7\u00f7".contains(text))
        {
            return new ButtonColors(theme.operatorButtonBackground(), theme.operatorButtonForeground());
        }

        if ("C".equals(text) || "CE".equals(text) || "\u2190".equals(text))
        {
            return new ButtonColors(theme.specialButtonBackground(), theme.specialButtonForeground());
        }

        return new ButtonColors(theme.functionButtonBackground(), theme.functionButtonForeground());
    }

    private record ButtonColors(Color background, Color foreground)
    {
    }
}
