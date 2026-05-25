package ui.settings;

import ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;

final class SettingsDialogFooterPanel extends JPanel
{
    SettingsDialogFooterPanel(AppTheme theme, Runnable resetAction, Runnable cancelAction, Runnable applyAction, Runnable saveAction)
    {
        super(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        setOpaque(false);

        JButton resetButton = createButton("ZurÃ¼cksetzen", theme, resetAction);
        JButton cancelButton = createButton("Abbrechen", theme, cancelAction);
        JButton applyButton = createButton("Anwenden", theme, applyAction);
        JButton saveButton = createButton("Speichern", theme, saveAction);

        add(resetButton);
        add(cancelButton);
        add(applyButton);
        add(saveButton);
    }

    private JButton createButton(String text, AppTheme theme, Runnable action)
    {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        SettingsDialogStyler.styleButton(button, theme);
        return button;
    }
}
