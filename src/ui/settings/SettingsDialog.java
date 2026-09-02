package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.animation.AnimationSupport;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.theme.ThemeType;
import ui.theme.custom.CustomThemeColors;
import ui.theme.custom.CustomThemePersistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public final class SettingsDialog extends JDialog
{
    private final AppTheme theme;
    private AppSettings workingSettings;
    private AppSettings appliedSettings;
    private final Consumer<AppSettings> settingsListener;
    private final Runnable sessionSaveListener;
    private final Runnable sessionLoadListener;
    private final CustomThemePersistence customThemePersistence = new CustomThemePersistence();
    private CustomThemeColors customThemeColors;
    private CustomThemeColors appliedCustomThemeColors;

    SettingsDialog(
            Frame owner,
            AppTheme theme,
            AppSettings settings,
            Consumer<AppSettings> settingsListener,
            Runnable sessionSaveListener,
            Runnable sessionLoadListener)
    {
        super(owner, "Einstellungen", false);
        this.theme = theme;
        this.workingSettings = settings.copy();
        this.appliedSettings = settings.copy();
        this.settingsListener = settingsListener;
        this.sessionSaveListener = sessionSaveListener;
        this.sessionLoadListener = sessionLoadListener;
        this.customThemeColors = customThemePersistence.lade();
        this.appliedCustomThemeColors = customThemeColors;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(createContent());
        setMinimumSize(new Dimension(520, 430));
        pack();
        setLocationRelativeTo(owner);
    }

    public static void showDialog(Frame owner, AppTheme theme, AppSettings settings, Consumer<AppSettings> settingsListener)
    {
        showDialog(owner, theme, settings, settingsListener, null, null);
    }

    public static void showDialog(
            Frame owner,
            AppTheme theme,
            AppSettings settings,
            Consumer<AppSettings> settingsListener,
            Runnable sessionSaveListener,
            Runnable sessionLoadListener)
    {
        new SettingsDialog(owner, theme, settings, settingsListener, sessionSaveListener, sessionLoadListener).setVisible(true);
    }

    private JPanel createContent()
    {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setBackground(theme.windowBackground());

        JLabel title = new JLabel("Einstellungen");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(theme.displayForeground());

        JLabel hint = new JLabel("Optik ist Geschmackssache. Außer Neon. Neon ist eine Entscheidung.");
        hint.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));
        hint.setForeground(theme.secondaryDisplayForeground());

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(hint, BorderLayout.SOUTH);

        JPanel settingsGrid = new JPanel(new GridLayout(0, 1, 0, 10));
        settingsGrid.setOpaque(false);
        settingsGrid.add(createComboRow("Theme", ThemeType.values(), workingSettings.getThemeType(),
                workingSettings::setThemeType));
        settingsGrid.add(createCustomThemeSection());
        settingsGrid.add(createComboRow("Startmodus", RechnerModus.values(), workingSettings.getStartModus(),
                workingSettings::setStartModus));
        settingsGrid.add(createComboRow("Winkelmodus", WinkelModus.values(), workingSettings.getWinkelModus(),
                workingSettings::setWinkelModus));
        settingsGrid.add(createSpinnerRow("Präzision", workingSettings.getNachkommastellen()));
        settingsGrid.add(createComboRow("Zahlenformat", ZahlenFormatModus.values(), workingSettings.getZahlenFormatModus(),
                workingSettings::setZahlenFormatModus));
        settingsGrid.add(createCheckRow("Verlauf speichern", workingSettings.isHistoryEnabled()));
        settingsGrid.add(createSessionRow());
        settingsGrid.add(createValueRow("Version", AppSettings.VERSION));

        content.add(header, BorderLayout.NORTH);
        content.add(settingsGrid, BorderLayout.CENTER);
        content.add(createFooter(), BorderLayout.SOUTH);
        return content;
    }

    private JPanel createFooter()
    {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);

        JButton resetButton = new JButton("Zurücksetzen");
        resetButton.addActionListener(e -> resetToAppliedValues());
        styleButton(resetButton);

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.addActionListener(e -> dispose());
        styleButton(cancelButton);

        JButton applyButton = new JButton("Anwenden");
        applyButton.addActionListener(e -> {
            publish();
            AnimationSupport.pulseBackground(applyButton, theme.successPulseColor(), 180);
        });
        styleButton(applyButton);

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> {
            publish();
            dispose();
        });
        styleButton(saveButton);

        footer.add(resetButton);
        footer.add(cancelButton);
        footer.add(applyButton);
        footer.add(saveButton);
        return footer;
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
        spinner.addChangeListener(e -> workingSettings.setNachkommastellen((Integer) spinner.getValue()));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return createSettingRow(name, spinner);
    }

    private JPanel createCheckRow(String name, boolean selected)
    {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(selected);
        checkBox.setOpaque(false);
        checkBox.setForeground(theme.displayForeground());
        checkBox.addActionListener(e -> workingSettings.setHistoryEnabled(checkBox.isSelected()));
        return createSettingRow(name, checkBox);
    }

    private JPanel createCustomThemeSection()
    {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                new EmptyBorder(10, 12, 12, 12)
        ));
        section.setBackground(theme.cardBackground());

        JLabel title = new JLabel("Custom Theme");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(theme.displayForeground());

        JLabel hint = new JLabel("Farben selber mischen: offiziell erlaubt, optisch auf eigene Gefahr.");
        hint.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 12f));
        hint.setForeground(theme.secondaryDisplayForeground());

        JPanel header = new JPanel(new BorderLayout(0, 3));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(hint, BorderLayout.SOUTH);

        JPanel colorsGrid = new JPanel(new GridLayout(0, 2, 8, 8));
        colorsGrid.setOpaque(false);
        colorsGrid.add(createColorButton("Fenster/Panel", customThemeColors.panelBackground(),
                color -> customThemeColors = customThemeColors.withPanelBackground(color)));
        colorsGrid.add(createColorButton("Display", customThemeColors.displayBackground(),
                color -> customThemeColors = customThemeColors.withDisplayBackground(color)));
        colorsGrid.add(createColorButton("Display-Text", customThemeColors.displayForeground(),
                color -> customThemeColors = customThemeColors.withDisplayForeground(color)));
        colorsGrid.add(createColorButton("Zahlen", customThemeColors.numberButtonBackground(),
                color -> customThemeColors = customThemeColors.withNumberButtonBackground(color)));
        colorsGrid.add(createColorButton("Operatoren", customThemeColors.operatorButtonBackground(),
                color -> customThemeColors = customThemeColors.withOperatorButtonBackground(color)));
        colorsGrid.add(createColorButton("Funktionen", customThemeColors.functionButtonBackground(),
                color -> customThemeColors = customThemeColors.withFunctionButtonBackground(color)));
        colorsGrid.add(createColorButton("Akzent/Toggle", customThemeColors.accentBackground(),
                color -> customThemeColors = customThemeColors.withAccentBackground(color)));

        section.add(header, BorderLayout.NORTH);
        section.add(colorsGrid, BorderLayout.CENTER);
        return section;
    }

    private JButton createColorButton(String label, Color initialColor, Consumer<Color> updater)
    {
        JButton button = new JButton(label + " " + formatColor(initialColor));
        styleColorButton(button, initialColor);
        button.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(this, label + " auswählen", button.getBackground());
            if (selected == null)
            {
                return;
            }

            updater.accept(selected);
            workingSettings.setThemeType(ThemeType.CUSTOM);
            button.setText(label + " " + formatColor(selected));
            styleColorButton(button, selected);
        });
        return button;
    }

    private void styleColorButton(JButton button, Color color)
    {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ModernButtonStyler.styleButton(button, theme, color, contrastFor(color));
    }

    private Color contrastFor(Color color)
    {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
        return luminance > 0.58 ? Color.BLACK : Color.WHITE;
    }

    private String formatColor(Color color)
    {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    private JPanel createValueRow(String name, String value)
    {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(theme.secondaryDisplayForeground());
        return createSettingRow(name, valueLabel);
    }

    private JPanel createSessionRow()
    {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton saveButton = new JButton("Session speichern");
        saveButton.addActionListener(e -> {
            if (sessionSaveListener != null)
            {
                sessionSaveListener.run();
            }
        });
        styleButton(saveButton);

        JButton loadButton = new JButton("Session laden");
        loadButton.addActionListener(e -> {
            if (sessionLoadListener != null)
            {
                sessionLoadListener.run();
            }
        });
        styleButton(loadButton);

        actions.add(saveButton);
        actions.add(loadButton);
        return createSettingRow("Session", actions);
    }

    private JPanel createSettingRow(String name, JComponent control)
    {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                new EmptyBorder(9, 12, 9, 12)
        ));
        row.setBackground(theme.cardBackground());

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(theme.displayForeground());

        row.add(nameLabel, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);
        return row;
    }

    private void publish()
    {
        customThemePersistence.speichere(customThemeColors);
        appliedSettings = workingSettings.copy();
        appliedCustomThemeColors = customThemeColors;
        settingsListener.accept(appliedSettings.copy());
    }

    private void resetToAppliedValues()
    {
        workingSettings = appliedSettings.copy();
        customThemeColors = appliedCustomThemeColors;
        setContentPane(createContent());
        pack();
        revalidate();
        repaint();
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
        ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
    }
}
