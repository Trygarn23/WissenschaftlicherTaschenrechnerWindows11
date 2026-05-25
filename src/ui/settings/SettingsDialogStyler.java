package ui.settings;

import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

final class SettingsDialogStyler
{
    private SettingsDialogStyler()
    {
    }

    static void styleComboBox(JComboBox<?> comboBox, AppTheme theme)
    {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(theme.toggleButtonBackground());
        comboBox.setForeground(theme.toggleButtonForeground());
        comboBox.setFocusable(false);
    }

    static void styleButton(JButton button, AppTheme theme)
    {
        button.setFont(theme.buttonFont());
        button.setBackground(theme.toggleButtonBackground());
        button.setForeground(theme.toggleButtonForeground());
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    static void styleColorButton(JButton button, Color color, AppTheme theme)
    {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(theme.contrastForeground(color));
        button.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
