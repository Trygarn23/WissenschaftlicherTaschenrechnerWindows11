package modes.graph.ui;

import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import modes.graph.model.GraphState;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
    private final GraphCanvasPanel canvasPanel = new GraphCanvasPanel(state, evaluator);

    private final JTextField expressionField = new JTextField(state.getHauptfunktion().getAusdruck());
    private final JLabel statusLabel = new JLabel("Bereit");
    private final JLabel swatchLabel = new JLabel(" ");
    private final List<JLabel> valueLabels = new ArrayList<>();

    private AppTheme theme;
    private WinkelModus winkelModus = WinkelModus.DEG;

    public GraphPanel()
    {
        setLayout(new BorderLayout(12, 0));
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));

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

        expressionField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        expressionField.setBackground(theme.historySearchBackground());
        expressionField.setForeground(theme.displayForeground());
        expressionField.setCaretColor(theme.displayForeground());
        expressionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.modeBorder(), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        statusLabel.setForeground(theme.secondaryDisplayForeground());
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

        JLabel title = new JLabel("f(x)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel expressionRow = new JPanel(new BorderLayout(8, 0));
        expressionRow.setOpaque(false);
        swatchLabel.setOpaque(true);
        swatchLabel.setPreferredSize(new Dimension(8, 42));
        swatchLabel.setBackground(state.getHauptfunktion().getFarbe());
        expressionRow.add(swatchLabel, BorderLayout.WEST);
        expressionRow.add(expressionField, BorderLayout.CENTER);

        expressionField.addActionListener(e -> plot());

        form.add(title, BorderLayout.NORTH);
        form.add(expressionRow, BorderLayout.CENTER);
        form.add(statusLabel, BorderLayout.SOUTH);

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("Plot", this::plot));
        controls.add(createButton("Reset", () -> {
            state.resetAnsicht();
            canvasPanel.repaint();
        }));
        controls.add(createButton("Zoom +", () -> {
            state.zoom(0.75);
            canvasPanel.repaint();
        }));
        controls.add(createButton("Zoom -", () -> {
            state.zoom(1.35);
            canvasPanel.repaint();
        }));

        JPanel north = new JPanel(new BorderLayout(0, 18));
        north.setOpaque(false);
        north.add(form, BorderLayout.NORTH);
        north.add(controls, BorderLayout.CENTER);

        sidebar.add(north, BorderLayout.NORTH);
        sidebar.add(buildMiniTable(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildMiniTable()
    {
        JPanel table = new JPanel(new GridLayout(0, 2, 8, 6));
        table.setOpaque(false);
        table.add(new JLabel("x"));
        table.add(new JLabel("f(x)"));
        for (int x = -2; x <= 2; x++)
        {
            table.add(new JLabel(Integer.toString(x)));
            JLabel valueLabel = new JLabel(" ");
            valueLabels.add(valueLabel);
            table.add(valueLabel);
        }
        return table;
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void plot()
    {
        String ausdruck = expressionField.getText().trim();
        if (ausdruck.isBlank())
        {
            setStatus("Bitte Funktion eingeben", false);
            return;
        }

        if (!evaluator.istGueltig(ausdruck, winkelModus))
        {
            setStatus("Ausdruck kann nicht gezeichnet werden", false);
            return;
        }

        state.getHauptfunktion().setAusdruck(ausdruck);
        setStatus("Zeichne " + state.getHauptfunktion().getName() + "(x) = " + ausdruck, true);
        updateMiniTable();
        canvasPanel.repaint();
    }

    private void setStatus(String text, boolean ok)
    {
        statusLabel.setText(text);
        if (theme != null)
        {
            statusLabel.setForeground(ok ? theme.secondaryDisplayForeground() : theme.operatorButtonBackground());
        }
    }

    private void updateMiniTable()
    {
        for (int i = 0; i < valueLabels.size(); i++)
        {
            int x = i - 2;
            try
            {
                double y = evaluator.auswerten(state.getHauptfunktion().getAusdruck(), x, winkelModus);
                valueLabels.get(i).setText(Double.isFinite(y) ? format(y) : "undef.");
            }
            catch (RuntimeException e)
            {
                valueLabels.get(i).setText("undef.");
            }
        }
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

        if (component instanceof JLabel label && component != swatchLabel)
        {
            label.setForeground(theme.displayForeground());
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
