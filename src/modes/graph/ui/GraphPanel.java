package modes.graph.ui;

import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import modes.graph.logic.GraphIntersectionService;
import modes.graph.logic.KurvendiskussionService;
import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import modes.graph.model.KurvendiskussionResult;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel
{
    private final GraphState state = new GraphState();
    private final GraphEvaluator evaluator = new GraphEvaluator();
    private final KurvendiskussionService kurvendiskussionService = new KurvendiskussionService(evaluator);
    private final GraphIntersectionService intersectionService = new GraphIntersectionService(evaluator);
    private final GraphCanvasPanel canvasPanel = new GraphCanvasPanel(state, evaluator);

    private final JLabel statusLabel = new JLabel("Bereit");
    private final JTextArea analysisArea = new JTextArea();
    private final JSpinner tableStepSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.25, 10.0, 0.25));
    private final List<JLabel> xLabels = new ArrayList<>();
    private final List<JLabel> valueLabels = new ArrayList<>();
    private final List<JTextField> expressionFields = new ArrayList<>();
    private final List<JCheckBox> visibleChecks = new ArrayList<>();

    private AppTheme theme;
    private WinkelModus winkelModus = WinkelModus.DEG;

    public GraphPanel()
    {
        setLayout(new BorderLayout(12, 0));
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        canvasPanel.setViewportChangedListener(this::updateAnalysis);

        add(buildSidebar(), BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);
        plot();
    }

    public void setWinkelModus(WinkelModus winkelModus)
    {
        this.winkelModus = winkelModus;
        canvasPanel.setWinkelModus(winkelModus);
        plot();
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        canvasPanel.applyTheme(theme);

        for (JTextField expressionField : expressionFields)
        {
            expressionField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            expressionField.setBackground(theme.historySearchBackground());
            expressionField.setForeground(theme.displayForeground());
            expressionField.setCaretColor(theme.displayForeground());
            expressionField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme.modeBorder(), 1),
                    new EmptyBorder(10, 12, 10, 12)
            ));
        }

        statusLabel.setForeground(theme.secondaryDisplayForeground());
        analysisArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        analysisArea.setBackground(theme.historySearchBackground());
        analysisArea.setForeground(theme.displayForeground());
        analysisArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableStepSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        applyThemeToChildren(this);

        repaint();
    }

    private JPanel buildSidebar()
    {
        JPanel sidebar = new JPanel(new BorderLayout(0, 14));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(300, 0));

        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setOpaque(false);

        JLabel title = new JLabel("Funktionen");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel expressionRows = new JPanel(new GridLayout(0, 1, 0, 8));
        expressionRows.setOpaque(false);
        for (int i = 0; i < state.getFunktionen().size(); i++)
        {
            expressionRows.add(buildFunctionRow(i));
        }

        form.add(title, BorderLayout.NORTH);
        form.add(expressionRows, BorderLayout.CENTER);
        form.add(statusLabel, BorderLayout.SOUTH);

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("Plot", this::plot));
        controls.add(createButton("Reset", () -> {
            state.resetAnsicht();
            updateAnalysis();
            canvasPanel.repaint();
        }));
        controls.add(createButton("Zoom +", () -> {
            state.zoom(0.75);
            updateAnalysis();
            canvasPanel.repaint();
        }));
        controls.add(createButton("Zoom -", () -> {
            state.zoom(1.35);
            updateAnalysis();
            canvasPanel.repaint();
        }));

        JPanel north = new JPanel(new BorderLayout(0, 18));
        north.setOpaque(false);
        north.add(form, BorderLayout.NORTH);
        north.add(controls, BorderLayout.CENTER);

        sidebar.add(north, BorderLayout.NORTH);
        sidebar.add(buildBottomPanel(), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildBottomPanel()
    {
        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);
        bottom.add(buildMiniTable(), BorderLayout.NORTH);
        bottom.add(buildAnalysisPanel(), BorderLayout.CENTER);
        return bottom;
    }

    private JPanel buildMiniTable()
    {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JPanel stepRow = new JPanel(new BorderLayout(8, 0));
        stepRow.setOpaque(false);
        stepRow.add(new JLabel("Tabellenschritt"), BorderLayout.WEST);
        stepRow.add(tableStepSpinner, BorderLayout.EAST);
        tableStepSpinner.addChangeListener(e -> updateMiniTable());

        JPanel table = new JPanel(new GridLayout(0, 4, 8, 6));
        table.setOpaque(false);
        table.add(new JLabel("x"));
        table.add(new JLabel("f(x)"));
        table.add(new JLabel("f'(x)"));
        table.add(new JLabel("f''(x)"));
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

    private JPanel buildAnalysisPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel title = new JLabel("Kurvendiskussion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        analysisArea.setEditable(false);
        analysisArea.setFocusable(false);
        analysisArea.setLineWrap(true);
        analysisArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(analysisArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JPanel buildFunctionRow(int index)
    {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JLabel swatch = new JLabel(state.getFunktion(index).getName());
        swatch.setHorizontalAlignment(JLabel.CENTER);
        swatch.setOpaque(true);
        swatch.setPreferredSize(new Dimension(28, 42));
        swatch.setBackground(state.getFunktion(index).getFarbe());
        swatch.setForeground(Color.WHITE);
        swatch.putClientProperty("graphSwatch", Boolean.TRUE);

        JTextField field = new JTextField(state.getFunktion(index).getAusdruck());
        field.addActionListener(e -> plot());
        expressionFields.add(field);

        JCheckBox visible = new JCheckBox();
        visible.setSelected(state.getFunktion(index).isSichtbar());
        visible.setOpaque(false);
        visible.addActionListener(e -> {
            state.getFunktion(index).setSichtbar(visible.isSelected());
            plot();
        });
        visibleChecks.add(visible);

        row.add(swatch, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.add(visible, BorderLayout.EAST);
        return row;
    }

    private void plot()
    {
        syncFunctionsFromUi();

        String ausdruck = state.getHauptfunktion().getAusdruck();
        if (ausdruck.isBlank())
        {
            setStatus("Bitte Funktion eingeben", false);
            analysisArea.setText("Kurvendiskussion wartet auf eine Funktion.");
            return;
        }

        if (!evaluator.istGueltig(ausdruck, winkelModus))
        {
            setStatus("Ausdruck kann nicht gezeichnet werden", false);
            analysisArea.setText("Kurvendiskussion nicht möglich.");
            return;
        }

        setStatus("Zeichne " + state.getHauptfunktion().getName() + "(x) = " + ausdruck, true);
        updateMiniTable();
        updateAnalysis();
        canvasPanel.repaint();
    }

    private void syncFunctionsFromUi()
    {
        for (int i = 0; i < expressionFields.size(); i++)
        {
            state.getFunktion(i).setAusdruck(expressionFields.get(i).getText().trim());
            state.getFunktion(i).setSichtbar(visibleChecks.get(i).isSelected());
        }
    }

    private void setStatus(String text, boolean ok)
    {
        statusLabel.setText(text);
        if (theme != null)
        {
            statusLabel.setForeground(ok ? theme.secondaryDisplayForeground() : theme.dangerBackground());
        }
    }

    private void updateMiniTable()
    {
        for (int i = 0; i < valueLabels.size(); i++)
        {
            int row = i / 3;
            int column = i % 3;
            double step = (Double) tableStepSpinner.getValue();
            double x = (row - 2) * step;
            if (column == 0)
            {
                xLabels.get(row).setText(format(x));
            }
            try
            {
                double y = switch (column)
                {
                    case 0 -> evaluator.auswerten(state.getHauptfunktion().getAusdruck(), x, winkelModus);
                    case 1 -> evaluator.ersteAbleitung(state.getHauptfunktion().getAusdruck(), x, winkelModus);
                    case 2 -> evaluator.zweiteAbleitung(state.getHauptfunktion().getAusdruck(), x, winkelModus);
                    default -> Double.NaN;
                };
                valueLabels.get(i).setText(Double.isFinite(y) ? format(y) : "undef.");
            }
            catch (RuntimeException e)
            {
                valueLabels.get(i).setText("undef.");
            }
        }
    }

    private void updateAnalysis()
    {
        try
        {
            KurvendiskussionResult result = kurvendiskussionService.analysiere(
                    state.getHauptfunktion().getAusdruck(),
                    state.getXMin(),
                    state.getXMax(),
                    winkelModus
            );

            analysisArea.setText(formatAnalysis(result));
            canvasPanel.setKurvendiskussionResult(result);
            analysisArea.setCaretPosition(0);
        }
        catch (RuntimeException e)
        {
            canvasPanel.setKurvendiskussionResult(null);
            analysisArea.setText("Kurvendiskussion nicht möglich.");
        }
    }

    private String formatAnalysis(KurvendiskussionResult result)
    {
        return "Y-Achse: " + formatPoint(result.getYAchsenSchnittpunkt()) + "\n"
                + "Nullstellen: " + formatPoints(result.getNullstellen()) + "\n"
                + "Extrema: " + formatPoints(result.getExtremstellen()) + "\n"
                + "Wendestellen: " + formatPoints(result.getWendestellen()) + "\n"
                + "Schnitt f/g: " + formatPoints(intersections()) + "\n"
                + "Hinweis: numerische Näherung im sichtbaren x-Bereich.";
    }

    private List<GraphPunkt> intersections()
    {
        if (state.getFunktionen().size() < 2 || !state.getFunktion(0).isSichtbar() || !state.getFunktion(1).isSichtbar())
        {
            return List.of();
        }

        return intersectionService.findeSchnittpunkte(
                state.getFunktion(0).getAusdruck(),
                state.getFunktion(1).getAusdruck(),
                state.getXMin(),
                state.getXMax(),
                winkelModus
        );
    }

    private String formatPoints(List<GraphPunkt> punkte)
    {
        if (punkte.isEmpty())
        {
            return "keine gefunden";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < punkte.size(); i++)
        {
            if (i > 0)
            {
                builder.append(", ");
            }
            builder.append(formatPoint(punkte.get(i)));
        }
        return builder.toString();
    }

    private String formatPoint(GraphPunkt punkt)
    {
        if (punkt == null)
        {
            return "nicht definiert";
        }
        return "(" + format(punkt.getX()) + " | " + format(punkt.getY()) + ")";
    }

    private String format(double value)
    {
        if (Math.abs(value) < 1e-9)
        {
            return "0";
        }
        if (Math.abs(value - Math.rint(value)) < 1e-9)
        {
            return Long.toString(Math.round(value));
        }
        return String.format("%.3f", value).replaceAll("0+$", "").replaceAll("[,.]$", "");
    }

    private void applyThemeToChildren(Component component)
    {
        if (theme == null)
        {
            return;
        }

        if (component instanceof JLabel label)
        {
            if (!Boolean.TRUE.equals(label.getClientProperty("graphSwatch")))
            {
                label.setForeground(theme.displayForeground());
            }
        }
        else if (component instanceof JButton button)
        {
            button.setFont(theme.buttonFont());
            button.setBackground(theme.toggleButtonBackground());
            button.setForeground(theme.toggleButtonForeground());
            button.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setOpaque(true);
        }
        else if (component instanceof JPanel panel && panel != this)
        {
            panel.setBackground(theme.panelBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeToChildren(child);
            }
        }

        statusLabel.setForeground(theme.secondaryDisplayForeground());
    }
}
