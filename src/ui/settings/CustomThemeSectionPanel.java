package ui.settings;

import ui.theme.AppTheme;
import ui.theme.custom.CustomThemeColors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;

final class CustomThemeSectionPanel extends JPanel
{
    private CustomThemeColors colors;

    CustomThemeSectionPanel(
            Component dialogParent,
            AppTheme theme,
            CustomThemeColors colors,
            Consumer<CustomThemeColors> colorsChanged,
            Runnable customThemeSelected)
    {
        super(new BorderLayout(0, 10));
        this.colors = colors;

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.modeBorder(), 1),
                new EmptyBorder(10, 12, 12, 12)
        ));
        setBackground(theme.panelBackground());

        JLabel title = new JLabel("Custom Theme");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(theme.displayForeground());

        JPanel colorsGrid = new JPanel(new GridLayout(0, 2, 8, 8));
        colorsGrid.setOpaque(false);
        colorsGrid.add(createColorButton(dialogParent, theme, "Fenster/Panel", this.colors.panelBackground(),
                color -> this.colors = this.colors.withPanelBackground(color), colorsChanged, customThemeSelected));
        colorsGrid.add(createColorButton(dialogParent, theme, "Display", this.colors.displayBackground(),
                color -> this.colors = this.colors.withDisplayBackground(color), colorsChanged, customThemeSelected));
        colorsGrid.add(createColorButton(dialogParent, theme, "Display-Text", this.colors.displayForeground(),
                color -> this.colors = this.colors.withDisplayForeground(color), colorsChanged, customThemeSelected));
        colorsGrid.add(createColorButton(dialogParent, theme, "Zahlen", this.colors.numberButtonBackground(),
                color -> this.colors = this.colors.withNumberButtonBackground(color), colorsChanged, customThemeSelected));
        colorsGrid.add(createColorButton(dialogParent, theme, "Operatoren", this.colors.operatorButtonBackground(),
                color -> this.colors = this.colors.withOperatorButtonBackground(color), colorsChanged, customThemeSelected));
        colorsGrid.add(createColorButton(dialogParent, theme, "Funktionen", this.colors.functionButtonBackground(),
                color -> this.colors = this.colors.withFunctionButtonBackground(color), colorsChanged, customThemeSelected));
        colorsGrid.add(createColorButton(dialogParent, theme, "Akzent/Toggle", this.colors.accentBackground(),
                color -> this.colors = this.colors.withAccentBackground(color), colorsChanged, customThemeSelected));

        add(title, BorderLayout.NORTH);
        add(colorsGrid, BorderLayout.CENTER);
    }

    private JButton createColorButton(
            Component dialogParent,
            AppTheme theme,
            String label,
            Color initialColor,
            Consumer<Color> updater,
            Consumer<CustomThemeColors> colorsChanged,
            Runnable customThemeSelected)
    {
        JButton button = new JButton(label + " " + formatColor(initialColor));
        SettingsDialogStyler.styleColorButton(button, initialColor, theme);
        button.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(dialogParent, label + " auswÃ¤hlen", button.getBackground());
            if (selected == null)
            {
                return;
            }

            updater.accept(selected);
            customThemeSelected.run();
            colorsChanged.accept(colors);
            button.setText(label + " " + formatColor(selected));
            SettingsDialogStyler.styleColorButton(button, selected, theme);
        });
        return button;
    }

    private String formatColor(Color color)
    {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}
