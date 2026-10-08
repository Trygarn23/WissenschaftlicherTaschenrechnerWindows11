package modes.statistik.ui;

import common.state.RechnerModus;
import modes.statistik.logic.StatistikRechnerService;
import modes.statistik.logic.StatistikTabellenText;
import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikDiagrammTyp;
import modes.statistik.model.StatistikErgebnis;
import modes.statistik.model.StatistikState;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.shell.ModePanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StatistikPanel extends JPanel implements ModePanel
{
    private final StatistikState state = new StatistikState();
    private final StatistikRechnerService service = new StatistikRechnerService();
    private final StatistikTabellenText tabellenText = new StatistikTabellenText();
    private final JTextArea textInput = new JTextArea("1\n2\n2\n4\n5\n8");
    private final JTextField klassenField = new JTextField("0");
    private final JCheckBox sortierenBox = new JCheckBox("Daten sortieren");
    private final JComboBox<StatistikDiagrammTyp> diagrammBox = new JComboBox<>(StatistikDiagrammTyp.values());
    private final StatistikDiagrammPanel diagrammPanel = new StatistikDiagrammPanel();
    private final StatistikTabellenPanel tabellenPanel = new StatistikTabellenPanel();
    private final StatistikErgebnisPanel ergebnisPanel = new StatistikErgebnisPanel(diagrammPanel);
    private final VerteilungsPanel verteilungsPanel = new VerteilungsPanel();
    private final JTabbedPane tabs = new JTabbedPane();
    private final List<JButton> buttons = new ArrayList<>();

    private AppTheme theme;

    public StatistikPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(true);

        JPanel datenTab = new JPanel(new BorderLayout(14, 0));
        datenTab.setOpaque(false);
        datenTab.add(buildInputArea(), BorderLayout.WEST);
        datenTab.add(buildResultArea(), BorderLayout.CENTER);
        datenTab.add(buildDiagrammArea(), BorderLayout.EAST);

        tabs.setFocusable(false);
        tabs.addTab("Daten", datenTab);
        tabs.addTab("Verteilungen", verteilungsPanel);
        add(tabs, BorderLayout.CENTER);

        tabellenPanel.fuelleBeispiel();
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.STATISTIK;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        applyThemeRecursively(this);

        tabs.setBackground(theme.panelBackground());
        tabs.setForeground(theme.displayForeground());
        textInput.setBackground(theme.inputBackground());
        textInput.setForeground(theme.displayForeground());
        textInput.setCaretColor(theme.displayForeground());
        textInput.setBorder(ModernButtonStyler.cardBorder(theme));
        tabellenPanel.applyTheme(theme);
        ergebnisPanel.applyTheme(theme);
        verteilungsPanel.applyTheme(theme);
        sortierenBox.setForeground(theme.displayForeground());
        sortierenBox.setBackground(theme.panelBackground());
        diagrammBox.setBackground(theme.toggleButtonBackground());
        diagrammBox.setForeground(theme.toggleButtonForeground());
        diagrammPanel.applyTheme(theme);

        klassenField.setFont(AppFonts.normal(14));
        ModernButtonStyler.styleInput(klassenField, theme);
        klassenField.setCaretColor(theme.displayForeground());

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }
    }

    private JPanel buildInputArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(360, 0));

        JLabel title = new JLabel("Statistik");
        title.setFont(AppFonts.fett(24));

        textInput.setFont(AppFonts.festeBreite(14));
        textInput.setLineWrap(false);

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("Text auswerten", this::werteTextAus));
        controls.add(createButton("Tabelle auswerten", this::werteTabelleAus));
        controls.add(createButton("Beispiel", this::beispiel));
        controls.add(createButton("Leeren", this::clear));
        controls.add(createButton("Als Tabelle kopieren", this::kopiereTabelle));
        controls.add(createButton("CSV importieren", this::importiereCsv));
        controls.add(wrapField("Klassen", klassenField));
        controls.add(sortierenBox);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(textInput), BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultArea()
    {
        JPanel split = new JPanel(new GridLayout(2, 1, 0, 10));
        split.setOpaque(false);
        split.add(tabellenPanel);
        split.add(ergebnisPanel);
        return split;
    }

    private JPanel buildDiagrammArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(440, 0));

        diagrammBox.addActionListener(e -> {
            diagrammPanel.setDiagrammTyp((StatistikDiagrammTyp) diagrammBox.getSelectedItem());
            diagrammPanel.repaint();
        });

        panel.add(diagrammBox, BorderLayout.NORTH);
        panel.add(diagrammPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapField(String labelText, JTextField field)
    {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        panel.add(new JLabel(labelText), BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(e -> runSafely(action));
        buttons.add(button);
        return button;
    }

    private void werteTextAus()
    {
        werteAusUndZeige(service.parseText(textInput.getText()), "Textdaten ausgewertet");
    }

    private void werteTabelleAus()
    {
        aktualisiereAuswertung(tabellenPanel.leseDaten(), "Tabellendaten ausgewertet");
    }

    private void werteAusUndZeige(List<StatistikDatenpunkt> daten, String status)
    {
        aktualisiereAuswertung(daten, status);
        tabellenPanel.zeigeDaten(daten);
    }

    private void aktualisiereAuswertung(List<StatistikDatenpunkt> daten, String status)
    {
        state.setKlassenAnzahl(parseKlassenAnzahl());
        state.setSortiert(sortierenBox.isSelected());
        state.setDatenpunkte(daten);

        StatistikErgebnis ergebnis = service.berechne(state.getDatenpunkte(), state.getKlassenAnzahl());
        ergebnisPanel.zeigeErgebnis(ergebnis, status);
        tabellenPanel.markiereAusreisser(ergebnis);
        diagrammPanel.setErgebnis(ergebnis);
    }

    private int parseKlassenAnzahl()
    {
        try
        {
            return Integer.parseInt(klassenField.getText().trim());
        }
        catch (NumberFormatException ignored)
        {
            return 0;
        }
    }

    private void kopiereTabelle()
    {
        List<StatistikDatenpunkt> daten = tabellenPanel.leseDaten();
        if (daten.isEmpty())
        {
            throw new IllegalArgumentException("Die Tabelle ist leer.");
        }

        String text = tabellenText.alsTabellenText(daten);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        ergebnisPanel.zeigeStatus(daten.size() + " Zeilen als Tabelle kopiert");
    }

    private void importiereCsv()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Statistikdaten importieren");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV- oder Textdatei", "csv", "txt", "tsv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        String inhalt;
        try
        {
            // Bytes statt readString: kaputte Umlaute in einer ANSI-Kopfzeile sollen den Import nicht abbrechen.
            inhalt = new String(Files.readAllBytes(chooser.getSelectedFile().toPath()), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }

        List<StatistikDatenpunkt> daten = tabellenText.liesCsv(inhalt);
        werteAusUndZeige(daten, chooser.getSelectedFile().getName() + " importiert");
    }

    private void beispiel()
    {
        textInput.setText("1\n2\n2\n4\n5\n8");
        tabellenPanel.fuelleBeispiel();
        werteTextAus();
    }

    private void clear()
    {
        textInput.setText("");
        tabellenPanel.leeren();
        tabellenPanel.markiereAusreisser(null);
        ergebnisPanel.leeren();
        diagrammPanel.setErgebnis(null);
    }

    private void runSafely(Runnable action)
    {
        try
        {
            action.run();
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            ergebnisPanel.zeigeFehler(e.getMessage());
            tabellenPanel.markiereAusreisser(null);
            diagrammPanel.setErgebnis(null);
        }
        catch (UncheckedIOException e)
        {
            ergebnisPanel.zeigeFehler("Datei konnte nicht gelesen werden: " + e.getCause().getMessage());
        }
        catch (IllegalStateException e)
        {
            ergebnisPanel.zeigeFehler("Zwischenablage ist gerade nicht verfügbar.");
        }
    }

    private void applyThemeRecursively(Component component)
    {
        if (theme == null)
        {
            return;
        }

        if (component instanceof JLabel label)
        {
            label.setForeground(theme.displayForeground());
        }
        else if (component instanceof JPanel panel && panel != this)
        {
            panel.setBackground(theme.panelBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeRecursively(child);
            }
        }
    }
}
