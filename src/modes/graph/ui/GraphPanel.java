package modes.graph.ui;

import common.state.RechnerModus;
import common.state.WinkelModus;
import modes.graph.formatting.GraphFormatter;
import modes.graph.logic.GraphEvaluator;
import modes.graph.logic.GraphIntersectionService;
import modes.graph.logic.KurvendiskussionService;
import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import modes.graph.model.KurvendiskussionResult;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;

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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel implements ModePanel
{
    private final GraphState state = new GraphState();
    private final GraphEvaluator evaluator = new GraphEvaluator();
    private final KurvendiskussionService kurvendiskussionService = new KurvendiskussionService(evaluator);
    private final GraphIntersectionService intersectionService = new GraphIntersectionService(evaluator);
    private final GraphCanvasPanel canvasPanel = new GraphCanvasPanel(state, evaluator);
    private final GraphFormatter formatter = new GraphFormatter();

    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final JLabel analysisTitleLabel = new JLabel("Kurvendiskussion");
    private final JLabel functionValueHeaderLabel = new JLabel("f(x)");
    private final JLabel firstDerivativeHeaderLabel = new JLabel("f'(x)");
    private final JLabel secondDerivativeHeaderLabel = new JLabel("f''(x)");
    private final JTextArea analysisArea = new JTextArea();
    private final JSpinner tableStepSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.25, 10.0, 0.25));
    private final JPanel expressionRows = new JPanel(new GridLayout(0, 1, 0, 8));
    private final JScrollPane functionScrollPane = new JScrollPane(expressionRows);
    private final List<JLabel> xLabels = new ArrayList<>();
    private final List<JLabel> valueLabels = new ArrayList<>();
    private final List<JTextField> expressionFields = new ArrayList<>();
    private final List<JCheckBox> visibleChecks = new ArrayList<>();
    private final List<JButton> functionButtons = new ArrayList<>();
    private final List<JButton> removeButtons = new ArrayList<>();

    private AppTheme theme;
    private WinkelModus winkelModus = WinkelModus.DEG;
    private double tableCenterX;

    public GraphPanel()
    {
        setLayout(new BorderLayout(12, 0));
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        evaluator.setFunktionen(state.getFunktionen());
        canvasPanel.setViewportChangedListener(this::updateAnalysis);
        canvasPanel.setFunctionSelectionListener(this::selectFunction);
        canvasPanel.setPointSelectionListener(this::useAnalysisPoint);

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

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.GRAPH;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        canvasPanel.applyTheme(theme);

        for (JTextField expressionField : expressionFields)
        {
            expressionField.setFont(AppFonts.normal(16));
            ModernButtonStyler.styleInput(expressionField, theme);
            expressionField.setCaretColor(theme.displayForeground());
        }

        analysisArea.setFont(AppFonts.festeBreite(12));
        analysisArea.setBackground(theme.cardBackground());
        analysisArea.setForeground(theme.displayForeground());
        analysisArea.setBorder(ModernButtonStyler.cardBorder(theme));
        tableStepSpinner.setFont(AppFonts.normal(12));
        functionScrollPane.setBorder(BorderFactory.createLineBorder(theme.cardBorder()));
        functionScrollPane.getViewport().setBackground(theme.panelBackground());
        applyThemeToChildren(this);
        statusAnzeige.setTheme(theme);
        updateFunctionSelectionStyles();

        repaint();
    }

    private JPanel buildSidebar()
    {
        JPanel sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(300, 0));

        JPanel form = new JPanel(new BorderLayout(0, 6));
        form.setOpaque(false);

        JLabel title = new JLabel("Funktionen");
        title.setFont(AppFonts.fett(19));

        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setOpaque(false);
        titleRow.add(title, BorderLayout.WEST);
        JButton addFunctionButton = createCompactButton(
                "+ Funktion",
                "Noch eine Funktion hinzufügen",
                this::addFunction
        );
        titleRow.add(addFunctionButton, BorderLayout.EAST);

        expressionRows.setOpaque(false);
        rebuildFunctionRows();

        functionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        functionScrollPane.setOpaque(false);
        functionScrollPane.getViewport().setOpaque(false);
        functionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        functionScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        functionScrollPane.setPreferredSize(new Dimension(300, 126));

        form.add(titleRow, BorderLayout.NORTH);
        form.add(functionScrollPane, BorderLayout.CENTER);
        form.add(statusLabel, BorderLayout.SOUTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        controls.setOpaque(false);
        controls.add(createCompactButton("Zeichnen", "Funktionen neu zeichnen", this::plot));
        controls.add(createCompactButton("Reset", "Graphansicht zurücksetzen", () -> {
            state.resetAnsicht();
            updateAnalysis();
            canvasPanel.repaint();
        }));
        controls.add(createCompactButton("+", "In den Graphen hineinzoomen", () -> {
            state.zoom(0.75);
            updateAnalysis();
            canvasPanel.repaint();
        }));
        controls.add(createCompactButton("−", "Aus dem Graphen herauszoomen", () -> {
            state.zoom(1.35);
            updateAnalysis();
            canvasPanel.repaint();
        }));

        JPanel north = new JPanel(new BorderLayout(0, 10));
        north.setOpaque(false);
        north.add(form, BorderLayout.NORTH);
        north.add(controls, BorderLayout.CENTER);

        sidebar.add(north, BorderLayout.NORTH);
        sidebar.add(buildBottomPanel(), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildBottomPanel()
    {
        JPanel bottom = new JPanel(new BorderLayout(0, 8));
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

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton createCompactButton(String text, String tooltip, Runnable action)
    {
        JButton button = createButton(text, action);
        button.putClientProperty("compactGraphControl", Boolean.TRUE);
        button.setToolTipText(tooltip);
        button.setMargin(new java.awt.Insets(3, 8, 3, 8));
        return button;
    }

    private JPanel buildFunctionRow(int index)
    {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JButton swatch = new JButton(state.getFunktion(index).getName());
        swatch.setHorizontalAlignment(JLabel.CENTER);
        swatch.setOpaque(true);
        swatch.setPreferredSize(new Dimension(28, 36));
        swatch.setBackground(state.getFunktion(index).getFarbe());
        swatch.setForeground(Color.WHITE);
        swatch.putClientProperty("graphSwatch", Boolean.TRUE);
        swatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        swatch.setToolTipText("Kurvendiskussion für diese Funktion anzeigen");
        swatch.addActionListener(e -> selectFunction(index));
        functionButtons.add(swatch);

        JTextField field = new JTextField(state.getFunktion(index).getAusdruck());
        field.setToolTipText("Andere Funktionen gehen als f(x) oder kurz als f");
        field.addActionListener(e -> plot());
        field.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                selectFunction(index);
            }
        });
        expressionFields.add(field);

        JCheckBox visible = new JCheckBox();
        visible.setSelected(state.getFunktion(index).isSichtbar());
        visible.setOpaque(false);
        visible.addActionListener(e -> {
            state.getFunktion(index).setSichtbar(visible.isSelected());
            plot();
        });
        visibleChecks.add(visible);

        JButton remove = new JButton("×");
        remove.setFocusable(false);
        remove.putClientProperty("compactGraphControl", Boolean.TRUE);
        remove.setMargin(new java.awt.Insets(2, 6, 2, 6));
        remove.setToolTipText("Funktion entfernen");
        remove.addActionListener(e -> removeFunction(index));
        removeButtons.add(remove);

        JPanel rowActions = new JPanel(new BorderLayout(4, 0));
        rowActions.setOpaque(false);
        rowActions.add(visible, BorderLayout.WEST);
        rowActions.add(remove, BorderLayout.EAST);

        row.add(swatch, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.add(rowActions, BorderLayout.EAST);
        return row;
    }

    private void rebuildFunctionRows()
    {
        expressionFields.clear();
        visibleChecks.clear();
        functionButtons.clear();
        removeButtons.clear();
        expressionRows.removeAll();

        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            expressionRows.add(buildFunctionRow(index));
        }

        expressionRows.revalidate();
        expressionRows.repaint();
        updateFunctionSelectionStyles();
    }

    private void addFunction()
    {
        syncFunctionsFromUi();
        state.fuegeFunktionHinzu("x");
        rebuildFunctionRows();
        if (theme != null)
        {
            applyTheme(theme);
        }
        plot();
        expressionFields.get(state.getAktiveFunktionIndex()).requestFocusInWindow();
    }

    private void removeFunction(int index)
    {
        syncFunctionsFromUi();
        if (!state.entferneFunktion(index))
        {
            statusAnzeige.zeigeFehler("Eine Funktion muss bleiben");
            return;
        }

        rebuildFunctionRows();
        if (theme != null)
        {
            applyTheme(theme);
        }
        plot();
    }

    private void selectFunction(int index)
    {
        syncFunctionsFromUi();
        state.setAktiveFunktion(index);
        updateFunctionSelectionStyles();
        updateTableHeaders();
        plot();
    }

    private void updateFunctionSelectionStyles()
    {
        for (int index = 0; index < functionButtons.size(); index++)
        {
            JButton button = functionButtons.get(index);
            Color farbe = state.getFunktion(index).getFarbe();
            boolean aktiv = index == state.getAktiveFunktionIndex();
            Color rahmen = theme == null
                    ? (aktiv ? Color.WHITE : farbe.darker())
                    : (aktiv ? theme.focusBorder() : theme.cardBorder());
            button.setBackground(farbe);
            button.setForeground(theme == null ? Color.WHITE : theme.contrastForeground(farbe));
            button.setBorder(BorderFactory.createLineBorder(rahmen, aktiv ? 3 : 1));
        }

        boolean kannEntfernen = state.getFunktionen().size() > 1;
        for (JButton removeButton : removeButtons)
        {
            removeButton.setEnabled(kannEntfernen);
        }
    }

    private void updateTableHeaders()
    {
        String name = state.getAktiveFunktion().getName();
        functionValueHeaderLabel.setText(name + "(x)");
        firstDerivativeHeaderLabel.setText(name + "'(x)");
        secondDerivativeHeaderLabel.setText(name + "''(x)");
        analysisTitleLabel.setText("Kurvendiskussion · " + name + "(x)");
    }

    private void useAnalysisPoint(GraphPunkt punkt)
    {
        tableCenterX = punkt.getX();
        updateMiniTable();
        statusAnzeige.zeigeErfolg("Punkt " + formatter.formatierePunkt(punkt) + " in die Wertetabelle übernommen");
    }

    private void plot()
    {
        syncFunctionsFromUi();

        updateTableHeaders();
        updateFunctionSelectionStyles();
        String ausdruck = state.getAktiveFunktion().getAusdruck();
        if (ausdruck.isBlank())
        {
            statusAnzeige.zeigeFehler("Bitte Funktion eingeben");
            analysisArea.setText("Gib mir eine Funktion, ich mal dir was.");
            canvasPanel.setKurvendiskussionResult(null);
            canvasPanel.repaint();
            return;
        }

        if (!evaluator.istGueltig(ausdruck, winkelModus))
        {
            statusAnzeige.zeigeFehler("Ausdruck kann nicht gezeichnet werden");
            analysisArea.setText("Kurvendiskussion nicht möglich.");
            canvasPanel.setKurvendiskussionResult(null);
            canvasPanel.repaint();
            return;
        }

        statusAnzeige.zeigeErfolg("Zeichne " + state.getAktiveFunktion().getName() + "(x) = " + ausdruck);
        updateMiniTable();
        updateAnalysis();
        canvasPanel.pulseRefresh();
        canvasPanel.repaint();
    }

    private void syncFunctionsFromUi()
    {
        for (int i = 0; i < expressionFields.size(); i++)
        {
            state.getFunktion(i).setAusdruck(expressionFields.get(i).getText().trim());
            state.getFunktion(i).setSichtbar(visibleChecks.get(i).isSelected());
        }
        evaluator.setFunktionen(state.getFunktionen());
    }

    private void updateMiniTable()
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
                case 0 -> evaluator.wertOderNaN(state.getAktiveFunktion().getAusdruck(), x, winkelModus);
                case 1 -> evaluator.ersteAbleitung(state.getAktiveFunktion().getAusdruck(), x, winkelModus);
                case 2 -> evaluator.zweiteAbleitung(state.getAktiveFunktion().getAusdruck(), x, winkelModus);
                default -> Double.NaN;
            };
            valueLabels.get(i).setText(Double.isFinite(y) ? formatter.formatiereZahl(y) : "undef.");
        }
    }

    private void updateAnalysis()
    {
        KurvendiskussionResult result = kurvendiskussionService.analysiere(
                state.getAktiveFunktion().getAusdruck(),
                state.getXMin(),
                state.getXMax(),
                winkelModus
        );

        analysisArea.setText(formatter.formatiereKurvendiskussion(result, intersections()));
        canvasPanel.setKurvendiskussionResult(result);
        analysisArea.setCaretPosition(0);
    }

    private List<GraphPunkt> intersections()
    {
        if (state.getFunktionen().size() < 2 || !state.getAktiveFunktion().isSichtbar())
        {
            return List.of();
        }

        List<GraphPunkt> punkte = new ArrayList<>();
        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            if (index == state.getAktiveFunktionIndex() || !state.getFunktion(index).isSichtbar())
            {
                continue;
            }

            // Eine ungültige Nebenfunktion liefert einfach keine Schnittpunkte (NaN), statt alles zu blockieren.
            punkte.addAll(intersectionService.findeSchnittpunkte(
                    state.getAktiveFunktion().getAusdruck(),
                    state.getFunktion(index).getAusdruck(),
                    state.getXMin(),
                    state.getXMax(),
                    winkelModus
            ));
        }
        return punkte;
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
            if (!Boolean.TRUE.equals(button.getClientProperty("graphSwatch")))
            {
                ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
                if (Boolean.TRUE.equals(button.getClientProperty("compactGraphControl")))
                {
                    styleCompactGraphButton(button);
                }
            }
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

    }

    private void styleCompactGraphButton(JButton button)
    {
        button.setFont(theme.buttonFont().deriveFont(12f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
}
