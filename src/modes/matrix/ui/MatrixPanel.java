package modes.matrix.ui;

import modes.matrix.formatting.MatrixFormatter;
import modes.matrix.logic.MatrixRechnerService;
import modes.matrix.model.Matrix;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixPanel extends JPanel
{
    private static final Integer[] GROESSEN = {1, 2, 3};

    private final MatrixRechnerService service = new MatrixRechnerService();
    private final MatrixFormatter formatter = new MatrixFormatter();

    private final JComboBox<Integer> aZeilenBox = new JComboBox<>(GROESSEN);
    private final JComboBox<Integer> aSpaltenBox = new JComboBox<>(GROESSEN);
    private final JComboBox<Integer> bZeilenBox = new JComboBox<>(GROESSEN);
    private final JComboBox<Integer> bSpaltenBox = new JComboBox<>(GROESSEN);
    private final JTextField skalarField = new JTextField("2");
    private final JTextArea resultArea = new JTextArea("Bereit");
    private final JLabel statusLabel = new JLabel("Matrixmodus bereit");
    private final JPanel matrixAHost = new JPanel(new BorderLayout());
    private final JPanel matrixBHost = new JPanel(new BorderLayout());
    private final List<JTextField> fields = new ArrayList<>();
    private final List<JButton> buttons = new ArrayList<>();
    private final List<JComboBox<Integer>> sizeBoxes = List.of(aZeilenBox, aSpaltenBox, bZeilenBox, bSpaltenBox);

    private JTextField[][] aFields = new JTextField[0][0];
    private JTextField[][] bFields = new JTextField[0][0];
    private AppTheme theme;

    public MatrixPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        aZeilenBox.setSelectedItem(2);
        aSpaltenBox.setSelectedItem(2);
        bZeilenBox.setSelectedItem(2);
        bSpaltenBox.setSelectedItem(2);

        add(buildInputArea(), BorderLayout.CENTER);
        add(buildResultArea(), BorderLayout.EAST);
        rebuildMatrices();
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        applyThemeRecursively(this);
        resultArea.setBackground(theme.displayBackground());
        resultArea.setForeground(theme.displayForeground());
        resultArea.setCaretColor(theme.displayForeground());
        statusLabel.setForeground(theme.secondaryDisplayForeground());

        for (JTextField field : fields)
        {
            styleField(field);
        }

        for (JComboBox<Integer> box : sizeBoxes)
        {
            box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            box.setBackground(theme.toggleButtonBackground());
            box.setForeground(theme.toggleButtonForeground());
            box.setFocusable(false);
        }

        for (JButton button : buttons)
        {
            button.setFont(theme.buttonFont());
            button.setBackground(theme.toggleButtonBackground());
            button.setForeground(theme.toggleButtonForeground());
            button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setOpaque(true);
        }
    }

    private JPanel buildInputArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel matrices = new JPanel(new GridLayout(1, 2, 12, 0));
        matrices.setOpaque(false);
        matrices.add(buildMatrixSection("Matrix A", aZeilenBox, aSpaltenBox, matrixAHost));
        matrices.add(buildMatrixSection("Matrix B", bZeilenBox, bSpaltenBox, matrixBHost));

        JPanel controls = new JPanel(new GridLayout(0, 4, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("A + B", () -> showMatrix(service.addiere(readA(), readB()), "Addition")));
        controls.add(createButton("A - B", () -> showMatrix(service.subtrahiere(readA(), readB()), "Subtraktion")));
        controls.add(createButton("A × B", () -> showMatrix(service.multipliziere(readA(), readB()), "Multiplikation")));
        controls.add(createButton("k × A", () -> showMatrix(service.skalarMultiplizieren(readA(), parse(skalarField)), "Skalarmultiplikation")));
        controls.add(createButton("A^T", () -> showMatrix(service.transponiere(readA()), "Transponieren A")));
        controls.add(createButton("B^T", () -> showMatrix(service.transponiere(readB()), "Transponieren B")));
        controls.add(createButton("spur A", () -> showScalar(service.spur(readA()), "Spur A")));
        controls.add(createButton("spur B", () -> showScalar(service.spur(readB()), "Spur B")));
        controls.add(createButton("rang A", () -> showScalar(service.rang(readA()), "Rang A")));
        controls.add(createButton("rang B", () -> showScalar(service.rang(readB()), "Rang B")));
        controls.add(createButton("det A", () -> showScalar(service.determinante(readA()), "Determinante A")));
        controls.add(createButton("det B", () -> showScalar(service.determinante(readB()), "Determinante B")));
        controls.add(wrapScalarInput());
        controls.add(createButton("Clear", this::clearMatrices));

        panel.add(matrices, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMatrixSection(String title, JComboBox<Integer> zeilenBox, JComboBox<Integer> spaltenBox, JPanel matrixHost)
    {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sizePanel.setOpaque(false);
        sizePanel.add(new JLabel("Zeilen"));
        sizePanel.add(zeilenBox);
        sizePanel.add(new JLabel("Spalten"));
        sizePanel.add(spaltenBox);

        zeilenBox.addActionListener(e -> rebuildMatrices());
        spaltenBox.addActionListener(e -> rebuildMatrices());

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(label, BorderLayout.NORTH);
        top.add(sizePanel, BorderLayout.CENTER);

        matrixHost.setOpaque(false);
        section.add(top, BorderLayout.NORTH);
        section.add(matrixHost, BorderLayout.CENTER);
        return section;
    }

    private JPanel buildResultArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(330, 0));

        JLabel title = new JLabel("Ergebnis");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        resultArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel wrapScalarInput()
    {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        JLabel label = new JLabel("k");
        panel.add(label, BorderLayout.WEST);
        panel.add(skalarField, BorderLayout.CENTER);
        fields.add(skalarField);
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

    private void rebuildMatrices()
    {
        fields.clear();
        fields.add(skalarField);
        aFields = rebuildMatrix(matrixAHost, selected(aZeilenBox), selected(aSpaltenBox), "A");
        bFields = rebuildMatrix(matrixBHost, selected(bZeilenBox), selected(bSpaltenBox), "B");
        if (theme != null)
        {
            applyTheme(theme);
        }
        revalidate();
        repaint();
    }

    private JTextField[][] rebuildMatrix(JPanel host, int zeilen, int spalten, String prefix)
    {
        host.removeAll();
        JPanel grid = new JPanel(new GridLayout(zeilen, spalten, 6, 6));
        grid.setOpaque(false);

        JTextField[][] result = new JTextField[zeilen][spalten];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < spalten; s++)
            {
                JTextField field = new JTextField("0");
                field.setName(prefix + (z + 1) + (s + 1));
                fields.add(field);
                result[z][s] = field;
                grid.add(field);
            }
        }

        host.add(grid, BorderLayout.NORTH);
        return result;
    }

    private Matrix readA()
    {
        return readMatrix(aFields);
    }

    private Matrix readB()
    {
        return readMatrix(bFields);
    }

    private Matrix readMatrix(JTextField[][] input)
    {
        double[][] values = new double[input.length][input[0].length];
        for (int z = 0; z < input.length; z++)
        {
            for (int s = 0; s < input[z].length; s++)
            {
                values[z][s] = parse(input[z][s]);
            }
        }
        return new Matrix(values);
    }

    private double parse(JTextField field)
    {
        return Double.parseDouble(field.getText().trim().replace(',', '.'));
    }

    private int selected(JComboBox<Integer> box)
    {
        Object selected = box.getSelectedItem();
        return selected instanceof Integer value ? value : 2;
    }

    private void runSafely(Runnable action)
    {
        try
        {
            action.run();
        }
        catch (Exception e)
        {
            showError(e.getMessage());
        }
    }

    private void showMatrix(Matrix matrix, String status)
    {
        resultArea.setText(formatter.formatiere(matrix));
        statusLabel.setText(status + " erfolgreich");
    }

    private void showScalar(double value, String status)
    {
        resultArea.setText(formatter.formatiereDouble(value));
        statusLabel.setText(status + " berechnet");
    }

    private void clearMatrices()
    {
        for (JTextField field : fields)
        {
            field.setText(field == skalarField ? "2" : "0");
        }
        resultArea.setText("Bereit");
        statusLabel.setText("Matrixmodus bereit");
    }

    private void showError(String message)
    {
        statusLabel.setText(message == null || message.isBlank() ? "Ungültige Matrixeingabe" : message);
        if (theme != null)
        {
            statusLabel.setForeground(theme.dangerBackground());
        }
        resultArea.setText("Fehler");
    }

    private void styleField(JTextField field)
    {
        if (theme == null)
        {
            return;
        }
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(theme.historySearchBackground());
        field.setForeground(theme.displayForeground());
        field.setCaretColor(theme.displayForeground());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.modeBorder(), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
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
