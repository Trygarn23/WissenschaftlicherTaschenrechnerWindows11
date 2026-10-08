package ui.konstanten;

import common.konstanten.EigeneKonstanten;
import common.konstanten.Konstante;
import common.konstanten.KonstantenFavoriten;
import common.konstanten.KonstantenKatalog;
import common.konstanten.KonstantenKategorie;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/** Inhalt des Konstanten-Dialogs: Suche, Kategorie-Filter, Tabelle mit Favoriten und eigene Konstanten. */
public class KonstantenPanel extends JPanel
{
    private static final String ALLE_KATEGORIEN = "Alle Kategorien";
    private static final String[] SPALTEN = {"Favorit", "Name", "Symbol", "Wert", "Einheit"};

    private final KonstantenFavoriten favoriten;
    private final EigeneKonstanten eigene;
    private final Consumer<String> einfuegen;

    private final JTextField suchfeld = new JTextField();
    private final JComboBox<Object> kategorieBox = new JComboBox<>();
    private final TabellenModell modell = new TabellenModell();
    private final JTable tabelle = new JTable(modell);
    private final JScrollPane scrollPane = new JScrollPane(tabelle);
    private final JLabel beschreibungLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel(" ");
    private final StatusAnzeige status = new StatusAnzeige(statusLabel);
    private final JButton einfuegenButton = new JButton("Einfügen");
    private final JButton hinzufuegenButton = new JButton("Eigene hinzufügen…");
    private final JButton loeschenButton = new JButton("Löschen");

    private List<Konstante> angezeigt = List.of();

    public KonstantenPanel(AppTheme theme, KonstantenFavoriten favoriten, EigeneKonstanten eigene, Consumer<String> einfuegen)
    {
        super(new BorderLayout(0, 10));
        this.favoriten = favoriten;
        this.eigene = eigene;
        this.einfuegen = einfuegen;

        setBorder(new EmptyBorder(14, 14, 14, 14));
        add(createFilterLeiste(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createFussLeiste(), BorderLayout.SOUTH);
        konfiguriereTabelle();

        aktualisiere();
        applyTheme(theme);
    }

    public void fokussiereSuche()
    {
        suchfeld.requestFocusInWindow();
    }

    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.panelBackground());
        for (JComponent feld : List.of(suchfeld, kategorieBox))
        {
            feld.setFont(AppFonts.normal(15));
            ModernButtonStyler.styleInput(feld, theme);
        }
        suchfeld.setCaretColor(theme.displayForeground());

        tabelle.setFont(AppFonts.normal(14));
        tabelle.setBackground(theme.historyBackground());
        tabelle.setForeground(theme.historyForeground());
        tabelle.setSelectionBackground(theme.historySelectionBackground());
        tabelle.setSelectionForeground(theme.contrastForeground(theme.historySelectionBackground()));
        tabelle.setGridColor(theme.gridColor());
        tabelle.getTableHeader().setFont(AppFonts.fett(13));
        tabelle.getTableHeader().setBackground(theme.cardBackground());
        tabelle.getTableHeader().setForeground(theme.displayForeground());
        scrollPane.getViewport().setBackground(theme.historyBackground());
        scrollPane.setBorder(BorderFactory.createLineBorder(theme.cardBorder(), 1, true));

        beschreibungLabel.setFont(AppFonts.normal(14));
        beschreibungLabel.setForeground(theme.displayForeground());
        statusLabel.setFont(AppFonts.normal(13));
        status.setTheme(theme);

        Color akzent = theme.modeButtonActiveBackground();
        ModernButtonStyler.styleButton(einfuegenButton, theme, akzent, theme.contrastForeground(akzent));
        ModernButtonStyler.styleButton(hinzufuegenButton, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        ModernButtonStyler.styleButton(loeschenButton, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        repaint();
    }

    private JPanel createFilterLeiste()
    {
        suchfeld.setToolTipText("Name, Symbol oder Beschreibung suchen");
        suchfeld.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                aktualisiere();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                aktualisiere();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                aktualisiere();
            }
        });
        suchfeld.addActionListener(e -> fuegeAusgewaehlteEin());

        kategorieBox.addItem(ALLE_KATEGORIEN);
        for (KonstantenKategorie kategorie : KonstantenKategorie.values())
        {
            kategorieBox.addItem(kategorie);
        }
        kategorieBox.setFocusable(false);
        kategorieBox.addActionListener(e -> aktualisiere());

        JPanel leiste = new JPanel(new BorderLayout(10, 0));
        leiste.setOpaque(false);
        leiste.add(suchfeld, BorderLayout.CENTER);
        leiste.add(kategorieBox, BorderLayout.EAST);
        return leiste;
    }

    private JPanel createFussLeiste()
    {
        einfuegenButton.addActionListener(e -> fuegeAusgewaehlteEin());
        hinzufuegenButton.addActionListener(e -> zeigeHinzufuegenDialog());
        loeschenButton.addActionListener(e -> loescheAusgewaehlte());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(hinzufuegenButton);
        buttons.add(loeschenButton);
        buttons.add(einfuegenButton);

        JPanel texte = new JPanel(new GridLayout(2, 1, 0, 4));
        texte.setOpaque(false);
        texte.add(beschreibungLabel);
        texte.add(statusLabel);

        JPanel fuss = new JPanel(new BorderLayout(0, 10));
        fuss.setOpaque(false);
        fuss.add(texte, BorderLayout.CENTER);
        fuss.add(buttons, BorderLayout.SOUTH);
        return fuss;
    }

    private void konfiguriereTabelle()
    {
        tabelle.setRowHeight(26);
        tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelle.setFillsViewportHeight(true);
        tabelle.getTableHeader().setReorderingAllowed(false);
        tabelle.getColumnModel().getColumn(0).setMaxWidth(70);
        tabelle.getColumnModel().getColumn(1).setPreferredWidth(240);
        tabelle.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabelle.getSelectionModel().addListSelectionListener(e -> zeigeDetails());

        tabelle.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "einfuegen");
        tabelle.getActionMap().put("einfuegen", new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                fuegeAusgewaehlteEin();
            }
        });
        tabelle.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2 && tabelle.columnAtPoint(e.getPoint()) != 0)
                {
                    fuegeAusgewaehlteEin();
                }
            }
        });
    }

    private void aktualisiere()
    {
        String gewaehlt = ausgewaehlte().map(Konstante::name).orElse(null);

        List<Konstante> quelle = new ArrayList<>(KonstantenKatalog.STANDARD);
        quelle.addAll(eigene.alle());
        Object auswahl = kategorieBox.getSelectedItem();
        KonstantenKategorie kategorie = auswahl instanceof KonstantenKategorie k ? k : null;
        angezeigt = KonstantenKatalog.suche(quelle, suchfeld.getText(), kategorie).stream()
                .sorted(Comparator.comparing(k -> !favoriten.istFavorit(k.name())))
                .toList();
        modell.fireTableDataChanged();

        int zeile = 0;
        for (int i = 0; i < angezeigt.size(); i++)
        {
            if (angezeigt.get(i).name().equals(gewaehlt))
            {
                zeile = i;
            }
        }
        if (!angezeigt.isEmpty())
        {
            tabelle.setRowSelectionInterval(zeile, zeile);
            tabelle.scrollRectToVisible(tabelle.getCellRect(zeile, 0, true));
        }
        zeigeDetails();
    }

    private Optional<Konstante> ausgewaehlte()
    {
        int zeile = tabelle.getSelectedRow();
        return zeile >= 0 && zeile < angezeigt.size() ? Optional.of(angezeigt.get(zeile)) : Optional.empty();
    }

    private void zeigeDetails()
    {
        Optional<Konstante> konstante = ausgewaehlte();
        beschreibungLabel.setText(konstante
                .map(k -> k.beschreibung() + " · " + k.kategorie().getLabel())
                .orElse(angezeigt.isEmpty() ? "Keine passende Konstante gefunden." : " "));
        loeschenButton.setEnabled(konstante.map(k -> k.kategorie() == KonstantenKategorie.EIGENE).orElse(false));
        einfuegenButton.setEnabled(konstante.isPresent());
    }

    private void fuegeAusgewaehlteEin()
    {
        ausgewaehlte().ifPresentOrElse(
                k -> einfuegen.accept(k.alsEingabe()),
                () -> status.zeigeFehler("Bitte zuerst eine Konstante auswählen."));
    }

    private void zeigeHinzufuegenDialog()
    {
        JTextField name = new JTextField();
        JTextField symbol = new JTextField();
        JTextField wert = new JTextField();
        JTextField einheit = new JTextField();
        JPanel formular = new JPanel(new GridLayout(0, 2, 8, 6));
        formular.add(new JLabel("Name:"));
        formular.add(name);
        formular.add(new JLabel("Symbol:"));
        formular.add(symbol);
        formular.add(new JLabel("Wert:"));
        formular.add(wert);
        formular.add(new JLabel("Einheit:"));
        formular.add(einheit);

        int antwort = JOptionPane.showConfirmDialog(this, formular, "Eigene Konstante hinzufügen",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (antwort != JOptionPane.OK_OPTION)
        {
            return;
        }

        try
        {
            Konstante neu = eigene.fuegeHinzu(name.getText(), symbol.getText(), wert.getText(), einheit.getText());
            suchfeld.setText("");
            kategorieBox.setSelectedItem(KonstantenKategorie.EIGENE);
            waehleAus(neu.name());
            status.zeigeErfolg("„" + neu.name() + "“ hinzugefügt.");
        }
        catch (IllegalArgumentException e)
        {
            status.zeigeFehler(e.getMessage());
        }
    }

    private void loescheAusgewaehlte()
    {
        Optional<Konstante> konstante = ausgewaehlte().filter(k -> k.kategorie() == KonstantenKategorie.EIGENE);
        if (konstante.isEmpty())
        {
            return;
        }

        String name = konstante.get().name();
        int antwort = JOptionPane.showConfirmDialog(this, "„" + name + "“ wirklich löschen?", "Eigene Konstante löschen",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (antwort != JOptionPane.YES_OPTION)
        {
            return;
        }

        eigene.entferne(name);
        if (favoriten.istFavorit(name))
        {
            favoriten.umschalten(name);
        }
        aktualisiere();
        status.zeigeErfolg("„" + name + "“ gelöscht.");
    }

    private void waehleAus(String name)
    {
        for (int i = 0; i < angezeigt.size(); i++)
        {
            if (angezeigt.get(i).name().equals(name))
            {
                tabelle.setRowSelectionInterval(i, i);
                tabelle.scrollRectToVisible(tabelle.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private final class TabellenModell extends AbstractTableModel
    {
        @Override
        public int getRowCount()
        {
            return angezeigt.size();
        }

        @Override
        public int getColumnCount()
        {
            return SPALTEN.length;
        }

        @Override
        public String getColumnName(int spalte)
        {
            return SPALTEN[spalte];
        }

        @Override
        public Class<?> getColumnClass(int spalte)
        {
            return spalte == 0 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int zeile, int spalte)
        {
            return spalte == 0;
        }

        @Override
        public Object getValueAt(int zeile, int spalte)
        {
            Konstante k = angezeigt.get(zeile);
            return switch (spalte)
            {
                case 0 -> favoriten.istFavorit(k.name());
                case 1 -> k.name();
                case 2 -> k.symbol();
                case 3 -> k.alsEingabe();
                default -> k.einheit();
            };
        }

        @Override
        public void setValueAt(Object wert, int zeile, int spalte)
        {
            Konstante k = angezeigt.get(zeile);
            favoriten.umschalten(k.name());
            // Favoriten wandern nach oben; die Auswahl bleibt auf der umgeschalteten Konstante.
            tabelle.setRowSelectionInterval(zeile, zeile);
            SwingUtilities.invokeLater(KonstantenPanel.this::aktualisiere);
        }
    }
}
