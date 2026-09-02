package modes.statistik.ui;

import common.state.RechnerModus;
import modes.statistik.formatting.StatistikFormatter;
import modes.statistik.logic.StatistikRechnerService;
import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikDiagrammTyp;
import modes.statistik.model.StatistikErgebnis;
import modes.statistik.model.StatistikState;
import ui.animation.AnimationSupport;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.shell.ModePanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class StatistikPanel extends JPanel implements ModePanel
{
    private static final String[] TABLE_COLUMNS = {"x", "y", "Gewicht"};

    private final StatistikState state = new StatistikState();
    private final StatistikRechnerService service = new StatistikRechnerService();
    private final StatistikFormatter formatter = new StatistikFormatter();
    private final JTextArea textInput = new JTextArea("1\n2\n2\n4\n5\n8");
    private final JTextArea resultArea = new JTextArea("Bereit");
    private final JLabel statusLabel = new JLabel("Statistikmodus bereit");
    private final JTextField klassenField = new JTextField("0");
    private final JCheckBox sortierenBox = new JCheckBox("Daten sortieren");
    private final JComboBox<StatistikDiagrammTyp> diagrammBox = new JComboBox<>(StatistikDiagrammTyp.values());
    private final DefaultTableModel tableModel = new DefaultTableModel(TABLE_COLUMNS, 18);
    private final JTable dataTable = new JTable(tableModel);
    private final StatistikDiagrammPanel diagrammPanel = new StatistikDiagrammPanel();
    private final List<JButton> buttons = new ArrayList<>();
    private final List<JTextField> fields = List.of(klassenField);

    private AppTheme theme;
    private StatistikErgebnis aktuellesErgebnis;

    public StatistikPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        add(buildInputArea(), BorderLayout.WEST);
        add(buildResultArea(), BorderLayout.CENTER);
        add(buildDiagrammArea(), BorderLayout.EAST);
        fuelleBeispielTabelle();
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

        textInput.setBackground(theme.inputBackground());
        textInput.setForeground(theme.displayForeground());
        textInput.setCaretColor(theme.displayForeground());
        textInput.setBorder(ModernButtonStyler.cardBorder(theme));
        resultArea.setBackground(theme.displayBackground());
        resultArea.setForeground(theme.displayForeground());
        resultArea.setCaretColor(theme.displayForeground());
        dataTable.setBackground(theme.inputBackground());
        dataTable.setForeground(theme.displayForeground());
        dataTable.setGridColor(theme.modeBorder());
        dataTable.getTableHeader().setBackground(theme.toggleButtonBackground());
        dataTable.getTableHeader().setForeground(theme.toggleButtonForeground());
        statusLabel.setForeground(theme.secondaryDisplayForeground());
        sortierenBox.setForeground(theme.displayForeground());
        sortierenBox.setBackground(theme.panelBackground());
        diagrammBox.setBackground(theme.toggleButtonBackground());
        diagrammBox.setForeground(theme.toggleButtonForeground());
        diagrammPanel.applyTheme(theme);

        for (JTextField field : fields)
        {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ModernButtonStyler.styleInput(field, theme);
            field.setCaretColor(theme.displayForeground());
        }

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
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        textInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        textInput.setLineWrap(false);

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("Text auswerten", this::werteTextAus));
        controls.add(createButton("Tabelle auswerten", this::werteTabelleAus));
        controls.add(createButton("Beispiel", this::beispiel));
        controls.add(createButton("Leeren", this::clear));
        controls.add(wrapField("Klassen", klassenField));
        controls.add(sortierenBox);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(textInput), BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        dataTable.setFillsViewportHeight(true);
        dataTable.setRowHeight(24);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JPanel split = new JPanel(new GridLayout(2, 1, 0, 10));
        split.setOpaque(false);
        split.add(new JScrollPane(dataTable));
        split.add(new JScrollPane(resultArea));

        panel.add(split, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
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
        List<StatistikDatenpunkt> daten = service.parseText(textInput.getText());
        aktualisiereAuswertung(daten, "Textdaten ausgewertet");
        fuelleTabelleAusDaten(daten);
    }

    private void werteTabelleAus()
    {
        aktualisiereAuswertung(readTableData(), "Tabellendaten ausgewertet");
    }

    private void aktualisiereAuswertung(List<StatistikDatenpunkt> daten, String status)
    {
        state.setKlassenAnzahl(parseKlassenAnzahl());
        state.setSortiert(sortierenBox.isSelected());
        state.setDatenpunkte(daten);

        aktuellesErgebnis = service.berechne(state.getDatenpunkte());
        if (state.getKlassenAnzahl() > 0)
        {
            aktuellesErgebnis = new StatistikErgebnis(
                    aktuellesErgebnis.getDatenpunkte(),
                    aktuellesErgebnis.getModalwerte(),
                    service.histogramm(aktuellesErgebnis.getDatenpunkte(), state.getKlassenAnzahl()),
                    aktuellesErgebnis.getAnzahl(),
                    aktuellesErgebnis.getSumme(),
                    aktuellesErgebnis.getMinimum(),
                    aktuellesErgebnis.getMaximum(),
                    aktuellesErgebnis.getMittelwert(),
                    aktuellesErgebnis.getMedian(),
                    aktuellesErgebnis.getQ1(),
                    aktuellesErgebnis.getQ3(),
                    aktuellesErgebnis.getVarianzPopulation(),
                    aktuellesErgebnis.getVarianzStichprobe(),
                    aktuellesErgebnis.getStandardabweichungPopulation(),
                    aktuellesErgebnis.getStandardabweichungStichprobe(),
                    aktuellesErgebnis.getLineareRegression(),
                    aktuellesErgebnis.getQuadratischeRegression()
            );
        }

        resultArea.setText(formatter.formatiereErgebnis(aktuellesErgebnis));
        diagrammPanel.setErgebnis(aktuellesErgebnis);
        statusLabel.setText(status + " | n = " + aktuellesErgebnis.getAnzahl());
        pulseResult(false);
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

    private List<StatistikDatenpunkt> readTableData()
    {
        List<StatistikDatenpunkt> daten = new ArrayList<>();
        int fallbackX = 1;

        for (int row = 0; row < tableModel.getRowCount(); row++)
        {
            String xText = cell(row, 0);
            String yText = cell(row, 1);
            String gewichtText = cell(row, 2);

            if (xText.isBlank() && yText.isBlank() && gewichtText.isBlank())
            {
                continue;
            }

            double x;
            double y;
            if (yText.isBlank())
            {
                x = fallbackX;
                y = parseZahl(xText);
            }
            else
            {
                x = xText.isBlank() ? fallbackX : parseZahl(xText);
                y = parseZahl(yText);
            }

            double gewicht = gewichtText.isBlank() ? 1.0 : parseZahl(gewichtText);
            daten.add(new StatistikDatenpunkt(x, y, gewicht));
            fallbackX++;
        }

        return daten;
    }

    private String cell(int row, int column)
    {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private double parseZahl(String text)
    {
        return Double.parseDouble(text.trim().replace(',', '.'));
    }

    private void beispiel()
    {
        textInput.setText("1\n2\n2\n4\n5\n8");
        fuelleBeispielTabelle();
        werteTextAus();
    }

    private void clear()
    {
        textInput.setText("");
        clearTable(18);
        resultArea.setText("Bereit");
        statusLabel.setText("Statistikmodus bereit");
        aktuellesErgebnis = null;
        diagrammPanel.setErgebnis(null);
    }

    private void fuelleBeispielTabelle()
    {
        clearTable(18);
        double[][] beispiel = {
                {1, 2, 1},
                {2, 3, 1},
                {3, 5, 1},
                {4, 8, 1},
                {5, 13, 1}
        };

        for (int i = 0; i < beispiel.length; i++)
        {
            tableModel.setValueAt(beispiel[i][0], i, 0);
            tableModel.setValueAt(beispiel[i][1], i, 1);
            tableModel.setValueAt(beispiel[i][2], i, 2);
        }
    }

    private void fuelleTabelleAusDaten(List<StatistikDatenpunkt> daten)
    {
        clearTable(Math.max(18, daten.size()));
        for (int i = 0; i < daten.size(); i++)
        {
            StatistikDatenpunkt punkt = daten.get(i);
            tableModel.setValueAt(formatter.formatiereZahl(punkt.x()), i, 0);
            tableModel.setValueAt(formatter.formatiereZahl(punkt.y()), i, 1);
            tableModel.setValueAt(formatter.formatiereZahl(punkt.gewicht()), i, 2);
        }
    }

    private void clearTable(int rows)
    {
        tableModel.setRowCount(rows);
        for (int row = 0; row < tableModel.getRowCount(); row++)
        {
            for (int column = 0; column < tableModel.getColumnCount(); column++)
            {
                tableModel.setValueAt(null, row, column);
            }
        }
    }

    private void runSafely(Runnable action)
    {
        try
        {
            action.run();
        }
        catch (Exception e)
        {
            statusLabel.setText(e.getMessage() == null || e.getMessage().isBlank()
                    ? "Ungültige Statistikdaten"
                    : e.getMessage());
            resultArea.setText("Fehler");
            diagrammPanel.setErgebnis(null);
            pulseResult(true);
        }
    }

    private void pulseResult(boolean error)
    {
        if (theme != null)
        {
            AnimationSupport.pulseBackground(resultArea, error ? theme.errorPulseColor() : theme.successPulseColor(), 200);
            AnimationSupport.pulseBackground(diagrammPanel, error ? theme.errorPulseColor() : theme.softAccentBackground(), 200);
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
