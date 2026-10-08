package ui.settings;

import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Baut die einheitlich gestylten Zeilen des Einstellungen-Dialogs: Name links, Bedienelement rechts, kurze Erklärung darunter. */
final class SettingsRows
{
    private final AppTheme theme;

    SettingsRows(AppTheme theme)
    {
        this.theme = theme;
    }

    @SuppressWarnings("unchecked")
    <T> JPanel combo(String name, String erklaerung, T[] values, T selected, Consumer<T> listener)
    {
        JComboBox<T> comboBox = new JComboBox<>(values);
        comboBox.setSelectedItem(selected);
        comboBox.addActionListener(e -> listener.accept((T) comboBox.getSelectedItem()));
        comboBox.setFont(AppFonts.normal(13));
        comboBox.setBackground(theme.toggleButtonBackground());
        comboBox.setForeground(theme.toggleButtonForeground());
        comboBox.setFocusable(false);
        return row(name, erklaerung, comboBox);
    }

    JPanel spinner(String name, String erklaerung, int selected, int min, int max, IntConsumer listener)
    {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(selected, min, max, 1));
        spinner.addChangeListener(e -> listener.accept((Integer) spinner.getValue()));
        spinner.setFont(AppFonts.normal(13));
        return row(name, erklaerung, spinner);
    }

    JPanel check(String name, String erklaerung, boolean selected, Consumer<Boolean> listener)
    {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(selected);
        checkBox.setOpaque(false);
        checkBox.setForeground(theme.displayForeground());
        checkBox.getAccessibleContext().setAccessibleName(name);
        checkBox.addActionListener(e -> listener.accept(checkBox.isSelected()));
        return row(name, erklaerung, checkBox);
    }

    JPanel value(String name, String value)
    {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(AppFonts.normal(13));
        valueLabel.setForeground(theme.secondaryDisplayForeground());
        return row(name, null, valueLabel);
    }

    JPanel row(String name, String erklaerung, JComponent control)
    {
        JPanel row = new JPanel(new BorderLayout(12, 2));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                new EmptyBorder(9, 12, 9, 12)
        ));
        row.setBackground(theme.cardBackground());

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(AppFonts.fett(14));
        nameLabel.setForeground(theme.displayForeground());
        nameLabel.setLabelFor(control);

        row.add(nameLabel, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);

        if (erklaerung != null)
        {
            JLabel hint = new JLabel(erklaerung);
            hint.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 12f));
            hint.setForeground(theme.secondaryDisplayForeground());
            row.add(hint, BorderLayout.SOUTH);
            control.setToolTipText(erklaerung);
        }
        return row;
    }

    JLabel sectionTitle(String text)
    {
        JLabel title = new JLabel(text);
        title.setFont(AppFonts.fett(15));
        title.setForeground(theme.displayForeground());
        title.setBorder(new EmptyBorder(6, 2, 0, 0));
        return title;
    }

    JButton button(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        return button;
    }
}
