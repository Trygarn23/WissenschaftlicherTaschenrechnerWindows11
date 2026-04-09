package ui.shell;

import Theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class GlobalActionBarPanel extends JPanel
{
    private final JLabel titleLabel = new JLabel("Taschenrechner");
    private final JButton angleModeButton = new JButton("DEG");
    private final JButton themeToggleButton = new JButton("Dark");
    private final JButton menuButton = new JButton("⋮");

    public GlobalActionBarPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsPanel.setOpaque(false);

        angleModeButton.setFocusable(false);
        themeToggleButton.setFocusable(false);
        menuButton.setFocusable(false);

        actionsPanel.add(angleModeButton);
        actionsPanel.add(themeToggleButton);
        actionsPanel.add(menuButton);

        add(titleLabel, BorderLayout.WEST);
        add(actionsPanel, BorderLayout.EAST);
    }

    public void setAngleModeText(String text)
    {
        angleModeButton.setText(text);
    }

    public void setThemeButtonText(String text)
    {
        themeToggleButton.setText(text);
    }

    public void setAngleModeListener(ActionListener listener)
    {
        angleModeButton.addActionListener(listener);
    }

    public void setThemeToggleListener(ActionListener listener)
    {
        themeToggleButton.addActionListener(listener);
    }

    public void setMenuListener(ActionListener listener)
    {
        menuButton.addActionListener(listener);
    }

    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.windowBackground());

        titleLabel.setForeground(theme.displayForeground());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        styleActionButton(angleModeButton, theme);
        styleActionButton(themeToggleButton, theme);
        styleActionButton(menuButton, theme);
    }

    private void styleActionButton(JButton button, AppTheme theme)
    {
        button.setFont(theme.buttonFont());
        button.setBackground(theme.toggleButtonBackground());
        button.setForeground(theme.toggleButtonForeground());
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }
}