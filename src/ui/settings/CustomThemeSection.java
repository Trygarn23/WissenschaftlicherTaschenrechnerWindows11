package ui.settings;

import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.theme.custom.CustomThemeColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/** Farbknöpfe für das eigene Theme. Jede Änderung geht über {@code onChange} zurück an den Dialog. */
final class CustomThemeSection
{
    private CustomThemeSection()
    {
    }

    static JPanel create(Component parent, AppTheme theme, Supplier<CustomThemeColors> colors,
                         Consumer<UnaryOperator<CustomThemeColors>> onChange)
    {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                new EmptyBorder(10, 12, 12, 12)
        ));
        section.setBackground(theme.cardBackground());

        JLabel title = new JLabel("Custom Theme");
        title.setFont(AppFonts.fett(14));
        title.setForeground(theme.displayForeground());

        JLabel hint = new JLabel("Farben selber mischen: offiziell erlaubt, optisch auf eigene Gefahr.");
        hint.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 12f));
        hint.setForeground(theme.secondaryDisplayForeground());

        JPanel header = new JPanel(new BorderLayout(0, 3));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(hint, BorderLayout.SOUTH);

        CustomThemeColors c = colors.get();
        JPanel colorsGrid = new JPanel(new GridLayout(0, 2, 8, 8));
        colorsGrid.setOpaque(false);
        colorsGrid.add(colorButton(parent, theme, "Fenster/Panel", c.panelBackground(),
                color -> onChange.accept(old -> old.withPanelBackground(color))));
        colorsGrid.add(colorButton(parent, theme, "Display", c.displayBackground(),
                color -> onChange.accept(old -> old.withDisplayBackground(color))));
        colorsGrid.add(colorButton(parent, theme, "Display-Text", c.displayForeground(),
                color -> onChange.accept(old -> old.withDisplayForeground(color))));
        colorsGrid.add(colorButton(parent, theme, "Zahlen", c.numberButtonBackground(),
                color -> onChange.accept(old -> old.withNumberButtonBackground(color))));
        colorsGrid.add(colorButton(parent, theme, "Operatoren", c.operatorButtonBackground(),
                color -> onChange.accept(old -> old.withOperatorButtonBackground(color))));
        colorsGrid.add(colorButton(parent, theme, "Funktionen", c.functionButtonBackground(),
                color -> onChange.accept(old -> old.withFunctionButtonBackground(color))));
        colorsGrid.add(colorButton(parent, theme, "Akzent/Toggle", c.accentBackground(),
                color -> onChange.accept(old -> old.withAccentBackground(color))));

        section.add(header, BorderLayout.NORTH);
        section.add(colorsGrid, BorderLayout.CENTER);
        return section;
    }

    private static JButton colorButton(Component parent, AppTheme theme, String label, Color initialColor, Consumer<Color> updater)
    {
        JButton button = new JButton(label + " " + formatColor(initialColor));
        style(button, theme, initialColor);
        button.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(parent, label + " auswählen", button.getBackground());
            if (selected == null)
            {
                return;
            }

            updater.accept(selected);
            button.setText(label + " " + formatColor(selected));
            style(button, theme, selected);
        });
        return button;
    }

    private static void style(JButton button, AppTheme theme, Color color)
    {
        button.setFont(AppFonts.normal(12));
        ModernButtonStyler.styleButton(button, theme, color, contrastFor(color));
    }

    private static Color contrastFor(Color color)
    {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
        return luminance > 0.58 ? Color.BLACK : Color.WHITE;
    }

    private static String formatColor(Color color)
    {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}
