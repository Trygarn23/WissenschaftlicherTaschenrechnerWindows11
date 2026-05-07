package modes.komplex.ui;

import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.logic.KomplexRechnerService;
import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexState;
import modes.komplex.model.KomplexeZahl;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class KomplexPanel extends JPanel
{
    private final KomplexState state = new KomplexState();
    private final KomplexRechnerService service = new KomplexRechnerService();
    private final KomplexFormatter formatter = new KomplexFormatter();

    private final JTextField aRealField = new JTextField("0");
    private final JTextField aImagField = new JTextField("0");
    private final JTextField bRealField = new JTextField("0");
    private final JTextField bImagField = new JTextField("0");
    private final JComboBox<KomplexDarstellung> darstellungBox = new JComboBox<>(KomplexDarstellung.values());
    private final JLabel resultLabel = new JLabel("0 + 0i");
    private final JLabel detailLabel = new JLabel("|z| = 0 | arg = 0°");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final List<JButton> buttons = new ArrayList<>();
    private final List<JTextField> fields = List.of(aRealField, aImagField, bRealField, bImagField);

    private AppTheme theme;

    public KomplexPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
        refresh();
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());

        for (JTextField field : fields)
        {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            field.setBackground(theme.historySearchBackground());
            field.setForeground(theme.displayForeground());
            field.setCaretColor(theme.displayForeground());
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme.modeBorder(), 1),
                    new EmptyBorder(9, 10, 9, 10)
            ));
        }

        darstellungBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        darstellungBox.setBackground(theme.toggleButtonBackground());
        darstellungBox.setForeground(theme.toggleButtonForeground());

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

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setForeground(theme.secondaryDisplayForeground());
    }

    private JPanel buildInputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 1, 0, 12));
        fieldsPanel.setOpaque(false);
        fieldsPanel.add(buildNumberInput("z1", aRealField, aImagField));
        fieldsPanel.add(buildNumberInput("z2", bRealField, bImagField));

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("+", () -> calculateBinary(service::addiere, "Addition")));
        controls.add(createButton("-", () -> calculateBinary(service::subtrahiere, "Subtraktion")));
        controls.add(createButton("×", () -> calculateBinary(service::multipliziere, "Multiplikation")));
        controls.add(createButton("÷", () -> calculateBinary(service::dividiere, "Division")));
        controls.add(createButton("conj z1", () -> calculateUnary(service::konjugiert, "Konjugation")));
        controls.add(createButton("Copy", this::copyResult));

        darstellungBox.addActionListener(e -> {
            state.setDarstellung((KomplexDarstellung) darstellungBox.getSelectedItem());
            refresh();
        });

        panel.add(fieldsPanel, BorderLayout.NORTH);
        panel.add(controls, BorderLayout.CENTER);
        panel.add(darstellungBox, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildNumberInput(String title, JTextField realField, JTextField imagField)
    {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel row = new JPanel(new GridLayout(1, 2, 8, 0));
        row.setOpaque(false);
        row.add(wrapField("Real", realField));
        row.add(wrapField("Imaginär", imagField));

        panel.add(label, BorderLayout.NORTH);
        panel.add(row, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapField(String labelText, JTextField field)
    {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildResultPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JLabel title = new JLabel("Komplex");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JPanel resultBox = new JPanel(new GridLayout(0, 1, 0, 10));
        resultBox.setOpaque(false);
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 44));
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultBox.add(resultLabel);
        resultBox.add(detailLabel);
        resultBox.add(statusLabel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(resultBox, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(e -> action.run());
        buttons.add(button);
        return button;
    }

    private void calculateBinary(ComplexOperation operation, String status)
    {
        try
        {
            readInputs();
            state.setErgebnis(operation.apply(state.getErsteZahl(), state.getZweiteZahl()));
            state.setStatus(status);
            refresh();
        }
        catch (Exception e)
        {
            showError(e.getMessage());
        }
    }

    private void calculateUnary(ComplexUnaryOperation operation, String status)
    {
        try
        {
            readInputs();
            state.setErgebnis(operation.apply(state.getErsteZahl()));
            state.setStatus(status);
            refresh();
        }
        catch (Exception e)
        {
            showError(e.getMessage());
        }
    }

    private void readInputs()
    {
        state.setErsteZahl(new KomplexeZahl(parse(aRealField), parse(aImagField)));
        state.setZweiteZahl(new KomplexeZahl(parse(bRealField), parse(bImagField)));
    }

    private double parse(JTextField field)
    {
        return Double.parseDouble(field.getText().trim().replace(',', '.'));
    }

    private void refresh()
    {
        resultLabel.setText(formatter.formatiere(state.getErgebnis(), state.getDarstellung()));
        detailLabel.setText("|z| = " + formatter.formatiereDouble(state.getErgebnis().betrag())
                + " | arg = " + formatter.formatiereDouble(state.getErgebnis().phaseDeg()) + "°");
        statusLabel.setText(state.getStatus());
    }

    private void copyResult()
    {
        StringSelection selection = new StringSelection(resultLabel.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        state.setStatus("Ergebnis kopiert");
        refresh();
    }

    private void showError(String message)
    {
        state.setStatus(message == null || message.isBlank() ? "Ungültige Eingabe" : message);
        if (theme != null)
        {
            statusLabel.setForeground(theme.operatorButtonBackground());
        }
        else
        {
            statusLabel.setForeground(Color.RED);
        }
        statusLabel.setText(state.getStatus());
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

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeToChildren(child);
            }
        }
    }

    @FunctionalInterface
    private interface ComplexOperation
    {
        KomplexeZahl apply(KomplexeZahl a, KomplexeZahl b);
    }

    @FunctionalInterface
    private interface ComplexUnaryOperation
    {
        KomplexeZahl apply(KomplexeZahl zahl);
    }
}
