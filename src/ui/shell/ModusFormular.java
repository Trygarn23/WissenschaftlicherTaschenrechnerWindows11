package ui.shell;

import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.function.Consumer;

/** Gemeinsame Layout- und Theme-Bausteine der einfachen Formular-Modi (Bruch, Datum, Finanz, Vektor …). */
public final class ModusFormular
{
    private ModusFormular()
    {
    }

    /** Beschriftung oben, Eingabe darunter. */
    public static JPanel labelUeber(String labelText, JComponent eingabe)
    {
        return labelUeber(labelText, eingabe, 4);
    }

    public static JPanel labelUeber(String labelText, JComponent eingabe, int abstand)
    {
        JPanel panel = new JPanel(new BorderLayout(0, abstand));
        panel.setOpaque(false);
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(eingabe, BorderLayout.CENTER);
        return panel;
    }

    public static void styleEingabe(JTextComponent eingabe, AppTheme theme)
    {
        eingabe.setFont(AppFonts.normal(15));
        ModernButtonStyler.styleInput(eingabe, theme);
        eingabe.setCaretColor(theme.displayForeground());
    }

    public static void styleAuswahl(JComboBox<?> auswahl, AppTheme theme)
    {
        auswahl.setFont(AppFonts.normal(14));
        auswahl.setBackground(theme.inputBackground());
        auswahl.setForeground(theme.displayForeground());
    }

    public static void faerbeKinder(JComponent wurzel, AppTheme theme, Component... eigeneLabels)
    {
        faerbeKinder(wurzel, theme, komponente -> { }, eigeneLabels);
    }

    /**
     * Färbt Labels (außer {@code eigeneLabels}, die das Panel selbst färbt) und Unterpanels rekursiv ein;
     * alle übrigen Komponenten gehen an {@code sonstige}.
     */
    public static void faerbeKinder(JComponent wurzel, AppTheme theme, Consumer<Component> sonstige, Component... eigeneLabels)
    {
        faerbe(wurzel, wurzel, theme, sonstige, eigeneLabels);
    }

    private static void faerbe(Component komponente, JComponent wurzel, AppTheme theme, Consumer<Component> sonstige,
                               Component[] eigeneLabels)
    {
        if (komponente instanceof JLabel label && !istEigenes(label, eigeneLabels))
        {
            label.setForeground(theme.displayForeground());
        }
        else if (komponente instanceof JPanel panel && panel != wurzel)
        {
            panel.setBackground(theme.panelBackground());
        }
        else
        {
            sonstige.accept(komponente);
        }

        if (komponente instanceof Container container)
        {
            for (Component kind : container.getComponents())
            {
                faerbe(kind, wurzel, theme, sonstige, eigeneLabels);
            }
        }
    }

    private static boolean istEigenes(Component komponente, Component[] eigeneLabels)
    {
        for (Component eigenes : eigeneLabels)
        {
            if (eigenes == komponente)
            {
                return true;
            }
        }
        return false;
    }
}
