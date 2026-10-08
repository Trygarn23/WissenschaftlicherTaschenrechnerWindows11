package modes.graph.ui;

import common.state.WinkelModus;
import modes.graph.formatting.GraphFormatter;
import modes.graph.logic.GraphEvaluator;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/** Wertetabelle, Flächen-Eingabe und Textausgabe der Kurvendiskussion für die aktive Funktion. */
final class AnalyseAusgabePanel extends JPanel
{
    private final GraphEvaluator evaluator;
    private final GraphFormatter formatter = new GraphFormatter();

    private final JLabel analysisTitleLabel = new JLabel("Kurvendiskussion");
    private final JLabel functionValueHeaderLabel = new JLabel("f(x)");
    private final JLabel firstDerivativeHeaderLabel = new JLabel("f'(x)");
    private final JLabel secondDerivativeHeaderLabel = new JLabel("f''(x)");
    private final JTextArea analysisArea = new JTextArea();
    private final JSpinner tableStepSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.25, 10.0, 0.25));
    private final JTextField grenzeA = new JTextField("0", 4);
    private final JTextField grenzeB = new JTextField("1", 4);
    private final List<JLabel> xLabels = new ArrayList<>();
    private final List<JLabel> valueLabels = new ArrayList<>();

    private double tableCenterX;
    private String ausdruck = "";
    private WinkelModus winkelModus = WinkelModus.DEG;

    AnalyseAusgabePanel(GraphEvaluator evaluator, BiConsumer<String, String> flaecheBerechnen, Runnable flaecheEntfernen)
    {
        super(new BorderLayout(0, 8));
        this.evaluator = evaluator;
        setOpaque(false);

        JPanel oben = new JPanel(new BorderLayout(0, 8));
        oben.setOpaque(false);
        oben.add(buildMiniTable(), BorderLayout.NORTH);
        oben.add(buildFlaechenZeile(flaecheBerechnen, flaecheEntfernen), BorderLayout.SOUTH);

        add(oben, BorderLayout.NORTH);
        add(buildAnalysisPanel(), BorderLayout.CENTER);
    }

    void applyTheme(AppTheme theme)
    {
        analysisArea.setFont(AppFonts.festeBreite(12));
        analysisArea.setBackground(theme.cardBackground());
        analysisArea.setForeground(theme.displayForeground());
        analysisArea.setBorder(ModernButtonStyler.cardBorder(theme));
        tableStepSpinner.setFont(AppFonts.normal(12));
        for (JTextField grenze : List.of(grenzeA, grenzeB))
        {
            grenze.setFont(AppFonts.normal(12));
            ModernButtonStyler.styleInput(grenze, theme);
            grenze.setCaretColor(theme.displayForeground());
        }
    }

    void setFunktionsName(String name)
    {
        functionValueHeaderLabel.setText(name + "(x)");
        firstDerivativeHeaderLabel.setText(name + "'(x)");
        secondDerivativeHeaderLabel.setText(name + "''(x)");
        analysisTitleLabel.setText("Kurvendiskussion · " + name + "(x)");
    }

    void zeigeText(String text)
    {
        analysisArea.setText(text);
        analysisArea.setCaretPosition(0);
    }

    void setTabellenMitte(double x)
    {
        tableCenterX = x;
        aktualisiereTabelle();
    }

    void aktualisiereTabelle(String ausdruck, WinkelModus winkelModus)
    {
        this.ausdruck = ausdruck;
        this.winkelModus = winkelModus;
        aktualisiereTabelle();
    }

    private void aktualisiereTabelle()
    {
        for (int i = 0; i < valueLabels.size(); i++)
        {
            int row = i / 3;
            int column = i % 3;
            double step = (Double) tableStepSpinner.getValue();
            double x = tableCenterX + (row - 2) * step;
            if (column == 0)
            {
                xLabels.get(row).setText(formatter.formatiereZahl(x));
            }
            double y = switch (column)
            {
                case 0 -> evaluator.wertOderNaN(ausdruck, x, winkelModus);
                case 1 -> evaluator.ersteAbleitung(ausdruck, x, winkelModus);
                case 2 -> evaluator.zweiteAbleitung(ausdruck, x, winkelModus);
                default -> Double.NaN;
            };
            valueLabels.get(i).setText(Double.isFinite(y) ? formatter.formatiereZahl(y) : "undef.");
        }
    }

    private JPanel buildMiniTable()
    {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JPanel stepRow = new JPanel(new BorderLayout(8, 0));
        stepRow.setOpaque(false);
        stepRow.add(new JLabel("Tabellenschritt"), BorderLayout.WEST);
        stepRow.add(tableStepSpinner, BorderLayout.EAST);
        tableStepSpinner.addChangeListener(e -> aktualisiereTabelle());

        JPanel table = new JPanel(new GridLayout(0, 4, 8, 6));
        table.setOpaque(false);
        table.add(new JLabel("x"));
        table.add(functionValueHeaderLabel);
        table.add(firstDerivativeHeaderLabel);
        table.add(secondDerivativeHeaderLabel);
        for (int row = -2; row <= 2; row++)
        {
            JLabel xLabel = new JLabel(" ");
            xLabels.add(xLabel);
            table.add(xLabel);
            for (int i = 0; i < 3; i++)
            {
                JLabel valueLabel = new JLabel(" ");
                valueLabels.add(valueLabel);
                table.add(valueLabel);
            }
        }

        wrapper.add(stepRow, BorderLayout.NORTH);
        wrapper.add(table, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFlaechenZeile(BiConsumer<String, String> flaecheBerechnen, Runnable flaecheEntfernen)
    {
        Runnable berechnen = () -> flaecheBerechnen.accept(grenzeA.getText(), grenzeB.getText());
        grenzeA.setToolTipText("Untere Grenze a");
        grenzeB.setToolTipText("Obere Grenze b");
        grenzeA.addActionListener(e -> berechnen.run());
        grenzeB.addActionListener(e -> berechnen.run());

        JPanel zeile = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        zeile.setOpaque(false);
        zeile.add(new JLabel("∫ von"));
        zeile.add(grenzeA);
        zeile.add(new JLabel("bis"));
        zeile.add(grenzeB);
        zeile.add(GraphSteuerleiste.kompakterButton("Fläche", "Fläche zwischen Kurve und x-Achse berechnen", berechnen));
        zeile.add(GraphSteuerleiste.kompakterButton("×", "Fläche wieder ausblenden", flaecheEntfernen));
        return zeile;
    }

    private JPanel buildAnalysisPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        analysisTitleLabel.setFont(AppFonts.fett(14));

        analysisArea.setEditable(false);
        analysisArea.setFocusable(false);
        analysisArea.setLineWrap(true);
        analysisArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(analysisArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(analysisTitleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
