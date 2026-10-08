package ui.befehle;

import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Inhalt der Befehlssuche: Suchfeld plus Trefferliste. Ohne Fenster testbar. */
public class BefehlsPanel extends JPanel
{
    private static final int MAX_TREFFER = 50;
    private static final String HINWEIS = "Modus, Theme, Funktion oder Konstante …";

    private final List<Befehl> befehle;
    private final Runnable schliessen;
    private final JTextField suchfeld = new JTextField()
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (getText().isEmpty() && theme != null)
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(theme.placeholderForeground());
                Insets rand = getInsets();
                g2.drawString(HINWEIS, rand.left, rand.top + g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        }
    };
    private final DefaultListModel<Befehl> modell = new DefaultListModel<>();
    private final JList<Befehl> liste = new JList<>(modell);
    private final JScrollPane scroll = new JScrollPane(liste);
    private final Zeile renderer = new Zeile();
    private AppTheme theme;

    /** @param schliessen wird vor dem Ausführen eines Befehls aufgerufen (Dialog zumachen). */
    public BefehlsPanel(AppTheme theme, List<Befehl> befehle, Runnable schliessen)
    {
        super(new BorderLayout(0, 8));
        this.befehle = List.copyOf(befehle);
        this.schliessen = schliessen;

        suchfeld.setFont(AppFonts.normal(16));
        liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        liste.setCellRenderer(renderer);
        liste.setVisibleRowCount(10);
        liste.setFocusable(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(suchfeld, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        applyTheme(theme);
        verdrahte();
        aktualisiereListe();
    }

    public void fokussiereSuche()
    {
        suchfeld.requestFocusInWindow();
    }

    /** Führt den markierten Befehl aus; ohne Treffer passiert nichts. */
    public void fuehreAusgewaehltenAus()
    {
        Befehl befehl = liste.getSelectedValue();
        if (befehl == null)
        {
            return;
        }
        schliessen.run();
        befehl.aktion().run();
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.popupBackground());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.modeBorder(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        ModernButtonStyler.styleInput(suchfeld, theme);
        suchfeld.setCaretColor(theme.displayForeground());
        liste.setBackground(theme.popupBackground());
        scroll.getViewport().setBackground(theme.popupBackground());
        repaint();
    }

    private void verdrahte()
    {
        suchfeld.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                aktualisiereListe();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                aktualisiereListe();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                aktualisiereListe();
            }
        });

        binde(KeyEvent.VK_UP, "befehl.hoch", () -> bewegeAuswahl(-1));
        binde(KeyEvent.VK_DOWN, "befehl.runter", () -> bewegeAuswahl(1));
        binde(KeyEvent.VK_ENTER, "befehl.ausfuehren", this::fuehreAusgewaehltenAus);

        liste.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int index = liste.locationToIndex(e.getPoint());
                if (index >= 0 && liste.getCellBounds(index, index).contains(e.getPoint()))
                {
                    liste.setSelectedIndex(index);
                    fuehreAusgewaehltenAus();
                }
            }
        });
    }

    private void binde(int taste, String name, Runnable aktion)
    {
        suchfeld.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(taste, 0), name);
        suchfeld.getActionMap().put(name, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                aktion.run();
            }
        });
    }

    private void bewegeAuswahl(int schritt)
    {
        int anzahl = modell.getSize();
        if (anzahl == 0)
        {
            return;
        }
        int neu = Math.floorMod(liste.getSelectedIndex() + schritt, anzahl);
        liste.setSelectedIndex(neu);
        liste.ensureIndexIsVisible(neu);
    }

    private void aktualisiereListe()
    {
        modell.clear();
        modell.addAll(BefehlsSuche.filtere(befehle, suchfeld.getText(), MAX_TREFFER));
        if (!modell.isEmpty())
        {
            liste.setSelectedIndex(0);
            liste.ensureIndexIsVisible(0);
        }
    }

    private final class Zeile extends JPanel implements ListCellRenderer<Befehl>
    {
        private final JLabel titel = new JLabel();
        private final JLabel kategorie = new JLabel();

        Zeile()
        {
            super(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            titel.setFont(AppFonts.normal(14));
            kategorie.setFont(AppFonts.normal(12));
            add(titel, BorderLayout.CENTER);
            add(kategorie, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Befehl> list, Befehl befehl, int index, boolean ausgewaehlt, boolean fokus)
        {
            titel.setText(befehl.titel());
            kategorie.setText(befehl.kategorie());
            setBackground(ausgewaehlt ? theme.popupSelectedBackground() : theme.popupBackground());
            titel.setForeground(ausgewaehlt ? theme.popupSelectedForeground() : theme.popupForeground());
            kategorie.setForeground(ausgewaehlt ? theme.popupSelectedForeground() : theme.placeholderForeground());
            return this;
        }
    }
}
