package ui.shell;

import common.state.WinkelModus;
import ui.theme.AppTheme;
import ui.theme.CalculatorButtonStyler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Container;

final class ShellThemeApplier
{
    private ShellThemeApplier()
    {
    }

    static void applyThemeRecursively(Component component, AppTheme theme, WinkelModus winkelModus)
    {
        if (component instanceof ModePanel modePanel)
        {
            modePanel.setWinkelModus(winkelModus);
            modePanel.applyTheme(theme);
            return;
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
        CalculatorButtonStyler.styleButton(button, theme);
    }
}
