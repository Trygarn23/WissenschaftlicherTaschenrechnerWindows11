package modes.logik.ui;

import common.state.RechnerModus;
import modes.logik.logic.TabellenAuswertung;
import modes.logik.logic.WahrheitstabellenRechner;
import modes.logik.model.KvDiagramm;
import modes.logik.model.Wahrheitstabelle;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LogikPanel extends JPanel implements ModePanel
{
    private static final String BEISPIEL = "A ∧ (B ∨ ¬C)";

    private final WahrheitstabellenRechner rechner = new WahrheitstabellenRechner();
    private final TabellenAuswertung auswertung = new TabellenAuswertung();

    private final JTextField ausdruckField = new JTextField(BEISPIEL);
    private final DefaultTableModel tabellenModel = nurLesbaresModel();
    private final JTable wahrheitsTable = new JTable(tabellenModel);
    private final DefaultTableModel kvModel = nurLesbaresModel();
    private final JTable kvTable = new JTable(kvModel);
    private final JTextArea normalformArea = new JTextArea();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel resultLabel = new JLabel(" ");
    private final JLabel detailLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final List<JButton> buttons = new ArrayList<>();

    private AppTheme theme;

    public LogikPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
        auswerten();
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.LOGIK;
    }

    @Override
    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());

        ausdruckField.setFont(AppFonts.symbole(18));
        ModernButtonStyler.styleInput(ausdruckField, theme);
        ausdruckField.setCaretColor(theme.displayForeground());

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
            button.setFont(AppFonts.symbole(16));
        }

        for (JTable table : List.of(wahrheitsTable, kvTable))
        {
            table.setBackground(theme.inputBackground());
            table.setForeground(theme.displayForeground());
            table.setGridColor(theme.modeBorder());
            table.getTableHeader().setBackground(theme.toggleButtonBackground());
            table.getTableHeader().setForeground(theme.toggleButtonForeground());
        }
        normalformArea.setBackground(theme.inputBackground());
        normalformArea.setForeground(theme.displayForeground());
        normalformArea.setFont(AppFonts.symbole(14));
        tabs.setBackground(theme.panelBackground());
        tabs.setForeground(theme.displayForeground());

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
        repaint();
    }

    private JPanel buildInputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

        JPanel eingabe = new JPanel(new BorderLayout(0, 6));
        eingabe.setOpaque(false);
        eingabe.add(new JLabel("Ausdruck (z. B. A && !B, A -> B, A XOR B)"), BorderLayout.NORTH);
        eingabe.add(ausdruckField, BorderLayout.CENTER);
        ausdruckField.addActionListener(e -> auswerten());

        JPanel symbole = new JPanel(new GridLayout(0, 4, 8, 8));
        symbole.setOpaque(false);
        for (String symbol : List.of("∧", "∨", "¬", "⊕", "→", "↔", "(", ")"))
        {
            boolean binaer = !List.of("¬", "(", ")").contains(symbol);
            String einfuegen = binaer ? " " + symbol + " " : symbol;
            symbole.add(createButton(symbol, () -> einfuegen(einfuegen)));
        }

        JPanel aktionen = new JPanel(new GridLayout(1, 2, 8, 0));
        aktionen.setOpaque(false);
        aktionen.add(createButton("Auswerten", this::auswerten));
        aktionen.add(createButton("Leeren", this::leeren));

        JPanel unten = new JPanel(new BorderLayout(0, 14));
        unten.setOpaque(false);
        unten.add(symbole, BorderLayout.NORTH);
        unten.add(aktionen, BorderLayout.SOUTH);

        panel.add(eingabe, BorderLayout.NORTH);
        panel.add(unten, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildResultPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel title = new JLabel("Logik");
        title.setFont(AppFonts.fett(28));

        JPanel resultBox = new JPanel(new GridLayout(0, 1, 0, 6));
        resultBox.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(30));
        detailLabel.setFont(AppFonts.normal(16));
        statusLabel.setFont(AppFonts.normal(14));
        resultBox.add(resultLabel);
        resultBox.add(detailLabel);
        resultBox.add(statusLabel);

        JPanel kopf = new JPanel(new BorderLayout(0, 8));
        kopf.setOpaque(false);
        kopf.add(title, BorderLayout.NORTH);
        kopf.add(resultBox, BorderLayout.CENTER);

        richteTabelleEin(wahrheitsTable);
        richteTabelleEin(kvTable);
        wahrheitsTable.setDefaultRenderer(Object.class, new HervorhebungsRenderer(false));
        kvTable.setDefaultRenderer(Object.class, new HervorhebungsRenderer(true));
        normalformArea.setEditable(false);
        normalformArea.setLineWrap(true);
        normalformArea.setWrapStyleWord(true);
        normalformArea.setFont(AppFonts.normal(15));
        normalformArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        tabs.setFocusable(false);
        tabs.addTab("Wahrheitstabelle", new JScrollPane(wahrheitsTable));
        tabs.addTab("KV-Diagramm", new JScrollPane(kvTable));
        tabs.addTab("DNF / KNF", new JScrollPane(normalformArea));

        panel.add(kopf, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private static DefaultTableModel nurLesbaresModel()
    {
        return new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
    }

    private static void richteTabelleEin(JTable table)
    {
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setFont(AppFonts.festeBreite(15));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(e -> action.run());
        buttons.add(button);
        return button;
    }

    private void einfuegen(String text)
    {
        ausdruckField.replaceSelection(text);
        ausdruckField.requestFocusInWindow();
    }

    private void leeren()
    {
        ausdruckField.setText("");
        ausdruckField.requestFocusInWindow();
        statusAnzeige.zeigeErfolg("Eingabe geleert");
    }

    private void auswerten()
    {
        try
        {
            Wahrheitstabelle tabelle = rechner.erstelle(ausdruckField.getText());
            zeigeTabelle(tabelle);
            zeigeKvDiagramm(tabelle);
            normalformArea.setText("Kanonische DNF:\n" + auswertung.dnf(tabelle)
                    + "\n\nKanonische KNF:\n" + auswertung.knf(tabelle));
            normalformArea.setCaretPosition(0);

            resultLabel.setText(tabelle.beschreibung());
            detailLabel.setText(tabelle.variablen().isEmpty()
                    ? "Keine Variablen"
                    : "Variablen: " + String.join(", ", tabelle.variablen().stream().map(String::valueOf).toList())
                      + " · " + tabelle.anzahlZeilen() + " Zeilen");
            statusAnzeige.zeigeErfolg("Wahrheitstabelle erstellt");
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void zeigeTabelle(Wahrheitstabelle tabelle)
    {
        List<String> spalten = new ArrayList<>();
        tabelle.variablen().forEach(v -> spalten.add(String.valueOf(v)));
        spalten.add("Ergebnis");

        Object[][] daten = new Object[tabelle.anzahlZeilen()][spalten.size()];
        for (int zeile = 0; zeile < tabelle.anzahlZeilen(); zeile++)
        {
            for (int spalte = 0; spalte < tabelle.variablen().size(); spalte++)
            {
                daten[zeile][spalte] = bit(tabelle.wert(zeile, spalte));
            }
            daten[zeile][spalten.size() - 1] = bit(tabelle.ergebnis(zeile));
        }
        tabellenModel.setDataVector(daten, spalten.toArray());
    }

    private void zeigeKvDiagramm(Wahrheitstabelle tabelle)
    {
        int n = tabelle.variablen().size();
        if (n < 2 || n > 4)
        {
            kvModel.setDataVector(new Object[][]{{"KV-Diagramm nur für 2 bis 4 Variablen."}}, new Object[]{"Hinweis"});
            return;
        }

        KvDiagramm kv = auswertung.kvDiagramm(tabelle);
        List<String> spalten = new ArrayList<>();
        spalten.add(kv.zeilenVariablen() + " \\ " + kv.spaltenVariablen());
        spalten.addAll(kv.spaltenKoepfe());

        Object[][] daten = new Object[kv.zeilenKoepfe().size()][spalten.size()];
        for (int zeile = 0; zeile < daten.length; zeile++)
        {
            daten[zeile][0] = kv.zeilenKoepfe().get(zeile);
            for (int spalte = 0; spalte < kv.spaltenKoepfe().size(); spalte++)
            {
                daten[zeile][spalte + 1] = bit(kv.wert(zeile, spalte));
            }
        }
        kvModel.setDataVector(daten, spalten.toArray());
    }

    private static String bit(boolean wert)
    {
        return wert ? "1" : "0";
    }

    /** Hebt die Ergebnisspalte (Wahrheitstabelle) bzw. die Kopfspalte (KV-Diagramm) hervor. */
    private class HervorhebungsRenderer extends DefaultTableCellRenderer
    {
        private final boolean ersteSpalte;

        HervorhebungsRenderer(boolean ersteSpalte)
        {
            this.ersteSpalte = ersteSpalte;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column)
        {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            boolean hervorheben = ersteSpalte ? column == 0 : column == table.getColumnCount() - 1;
            setFont(hervorheben ? table.getFont().deriveFont(Font.BOLD) : table.getFont());
            if (theme != null)
            {
                setBackground(hervorheben ? theme.softAccentBackground() : table.getBackground());
                setForeground(theme.displayForeground());
            }
            else
            {
                setBackground(hervorheben ? new Color(220, 230, 255) : table.getBackground());
            }
            return this;
        }
    }

    private void applyThemeToChildren(Component component)
    {
        if (theme == null)
        {
            return;
        }

        if (component instanceof JLabel label && label != resultLabel && label != detailLabel && label != statusLabel)
        {
            label.setForeground(theme.displayForeground());
        }
        else if (component instanceof JPanel panel && panel != this)
        {
            panel.setBackground(theme.panelBackground());
        }
        else if (component instanceof JScrollPane scrollPane)
        {
            scrollPane.getViewport().setBackground(theme.inputBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeToChildren(child);
            }
        }
    }
}
