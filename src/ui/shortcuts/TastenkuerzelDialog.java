package ui.shortcuts;

import common.state.RechnerModus;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Übersicht aller Tastenkürzel, aufrufbar über F1 oder den Button in der Aktionsleiste. */
public final class TastenkuerzelDialog extends JDialog
{
    public record Eintrag(String taste, String beschreibung)
    {
    }

    private final AppTheme theme;

    private TastenkuerzelDialog(Frame owner, AppTheme theme)
    {
        super(owner, "Tastenkürzel", true);
        this.theme = theme;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(createContent());
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setSize(Math.max(getWidth(), 520), Math.min(getHeight(), 640));
        setLocationRelativeTo(owner);
    }

    public static void showDialog(Frame owner, AppTheme theme)
    {
        new TastenkuerzelDialog(owner, theme).setVisible(true);
    }

    /** Inhalt der Übersicht, nach Abschnitten gruppiert. Aus denselben Quellen wie die echte Tastenbelegung. */
    public static Map<String, List<Eintrag>> abschnitte()
    {
        Map<String, List<Eintrag>> abschnitte = new LinkedHashMap<>();

        List<Eintrag> modi = new ArrayList<>();
        for (RechnerModus modus : RechnerModus.values())
        {
            modi.add(new Eintrag(Tastenkuerzel.strgText(Tastenkuerzel.modusNummer(modus)), modus.getLabel()));
        }
        modi.add(new Eintrag(Tastenkuerzel.strgText(Tastenkuerzel.einheitenNummer()), "Einheiten ein-/ausblenden"));
        abschnitte.put("Modus wechseln", modi);

        List<Eintrag> rechnen = new ArrayList<>();
        rechnen.add(new Eintrag("0 – 9", "Ziffern eingeben, auch über den Nummernblock"));
        for (Tastenkuerzel kuerzel : Tastenkuerzel.values())
        {
            rechnen.add(new Eintrag(kuerzel.getAnzeigeText(), ButtonTooltips.beschreibungFor(kuerzel.getAktion())));
        }
        abschnitte.put("Rechnen (Standard und Wissenschaftlich)", rechnen);

        abschnitte.put("Sonstiges", List.of(
                new Eintrag("Strg+C", "Anzeige kopieren (Display vorher anklicken)"),
                new Eintrag("Strg+V", "Ausdruck einfügen (Display vorher anklicken)"),
                new Eintrag(Tastenkuerzel.PROGRAMMIERER_CLR_TEXT, "Suche verlassen, im PRG-Modus CLR, sonst Fenster schließen"),
                new Eintrag(Tastenkuerzel.HILFE_TEXT, "Diese Übersicht öffnen")
        ));
        return abschnitte;
    }

    private JPanel createContent()
    {
        JPanel liste = new JPanel();
        liste.setLayout(new BoxLayout(liste, BoxLayout.Y_AXIS));
        liste.setBackground(theme.windowBackground());

        for (Map.Entry<String, List<Eintrag>> abschnitt : abschnitte().entrySet())
        {
            liste.add(createUeberschrift(abschnitt.getKey()));
            liste.add(createTabelle(abschnitt.getValue()));
        }

        JScrollPane scrollPane = new JScrollPane(liste);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(theme.windowBackground());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JLabel title = new JLabel("Tastenkürzel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(theme.displayForeground());

        JButton schliessen = new JButton("Schließen");
        schliessen.addActionListener(e -> dispose());
        ModernButtonStyler.styleButton(schliessen, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(schliessen);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setBackground(theme.windowBackground());
        content.add(title, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        return content;
    }

    private JLabel createUeberschrift(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(theme.displayForeground());
        label.setBorder(new EmptyBorder(12, 0, 6, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createTabelle(List<Eintrag> eintraege)
    {
        JPanel tabelle = new JPanel(new GridBagLayout());
        tabelle.setOpaque(false);
        tabelle.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3, 0, 3, 16);
        for (int zeile = 0; zeile < eintraege.size(); zeile++)
        {
            Eintrag eintrag = eintraege.get(zeile);
            c.gridy = zeile;

            c.gridx = 0;
            c.weightx = 0;
            JLabel taste = new JLabel(eintrag.taste());
            taste.setFont(new Font("Segoe UI", Font.BOLD, 13));
            taste.setForeground(theme.displayForeground());
            tabelle.add(taste, c);

            c.gridx = 1;
            c.weightx = 1;
            JLabel beschreibung = new JLabel(eintrag.beschreibung());
            beschreibung.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            beschreibung.setForeground(theme.secondaryDisplayForeground());
            tabelle.add(beschreibung, c);
        }
        return tabelle;
    }
}
