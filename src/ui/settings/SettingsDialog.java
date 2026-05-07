package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.AppTheme;
import ui.theme.ThemeType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public final class SettingsDialog extends JDialog
{
    private final AppTheme theme;
    private final AppSettings workingSettings;
    private final Consumer<AppSettings> settingsListener;

    private SettingsDialog(Frame owner, AppTheme theme, AppSettings settings, Consumer<AppSettings> settingsListener)
    {
        super(owner, "Einstellungen", false);
        this.theme = theme;
        this.workingSettings = settings.copy();
        this.settingsListener = settingsListener;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(createContent());
        setMinimumSize(new Dimension(520, 430));
        pack();
        setLocationRelativeTo(owner);
    }

    public static void showDialog(Frame owner, AppTheme theme, AppSettings settings, Consumer<AppSettings> settingsListener)
    {
        new SettingsDialog(owner, theme, settings, settingsListener).setVisible(true);
    }

    private JPanel createContent()
    {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setBackground(theme.windowBackground());

        JLabel title = new JLabel("Einstellungen");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(theme.displayForeground());

        JPanel settingsGrid = new JPanel(new GridLayout(0, 1, 0, 10));
        settingsGrid.setOpaque(false);
        settingsGrid.add(createComboRow("Theme", ThemeType.values(), workingSettings.getThemeType(), value -> {
            workingSettings.setThemeType(value);
            publish();
        }));
        settingsGrid.add(createComboRow("Startmodus", RechnerModus.values(), workingSettings.getStartModus(), value -> {
            workingSettings.setStartModus(value);
            publish();
        }));
        settingsGrid.add(createComboRow("Winkelmodus", WinkelModus.values(), workingSettings.getWinkelModus(), value -> {
            workingSettings.setWinkelModus(value);
            publish();
        }));
        settingsGrid.add(createSpinnerRow("Präzision", workingSettings.getNachkommastellen()));
        settingsGrid.add(createComboRow("Zahlenformat", ZahlenFormatModus.values(), workingSettings.getZahlenFormatModus(), value -> {
            workingSettings.setZahlenFormatModus(value);
            publish();
        }));
        settingsGrid.add(createCheckRow("Verlauf speichern", workingSettings.isHistoryEnabled()));
        settingsGrid.add(createValueRow("Version", AppSettings.VERSION));

        JButton closeButton = new JButton("Schließen");
        closeButton.addActionListener(e -> dispose());
        styleButton(closeButton);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(closeButton);

        content.add(title, BorderLayout.NORTH);
        content.add(settingsGrid, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        return content;
    }

    @SuppressWarnings("unchecked")
    private <T> JPanel createComboRow(String name, T[] values, T selected, Consumer<T> listener)
    {
        JComboBox<T> comboBox = new JComboBox<>(values);
        comboBox.setSelectedItem(selected);
        comboBox.addActionListener(e -> listener.accept((T) comboBox.getSelectedItem()));
        styleComboBox(comboBox);
        return createSettingRow(name, comboBox);
    }

    private JPanel createSpinnerRow(String name, int selected)
    {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(selected, 2, 15, 1));
        spinner.addChangeListener(e -> {
            workingSettings.setNachkommastellen((Integer) spinner.getValue());
            publish();
        });
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return createSettingRow(name, spinner);
    }

    private JPanel createCheckRow(String name, boolean selected)
    {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(selected);
        checkBox.setOpaque(false);
        checkBox.setForeground(theme.displayForeground());
        checkBox.addActionListener(e -> {
            workingSettings.setHistoryEnabled(checkBox.isSelected());
            publish();
        });
        return createSettingRow(name, checkBox);
    }

    private JPanel createValueRow(String name, String value)
    {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(theme.secondaryDisplayForeground());
        return createSettingRow(name, valueLabel);
    }

    private JPanel createSettingRow(String name, JComponent control)
    {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.modeBorder(), 1),
                new EmptyBorder(9, 12, 9, 12)
        ));
        row.setBackground(theme.panelBackground());

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(theme.displayForeground());

        row.add(nameLabel, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);
        return row;
    }

    private void publish()
    {
        settingsListener.accept(workingSettings.copy());
    }

    private void styleComboBox(JComboBox<?> comboBox)
    {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(theme.toggleButtonBackground());
        comboBox.setForeground(theme.toggleButtonForeground());
        comboBox.setFocusable(false);
    }

    private void styleButton(JButton button)
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
}
