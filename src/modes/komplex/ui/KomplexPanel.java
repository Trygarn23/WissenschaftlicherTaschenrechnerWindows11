package modes.komplex.ui;

import common.formatting.ZahlenEingabe;
import common.state.RechnerModus;
import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.logic.KomplexRechnerService;
import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexState;
import modes.komplex.model.KomplexeZahl;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class KomplexPanel extends JPanel implements ModePanel
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
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
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

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.KOMPLEX;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());

        for (JTextField field : fields)
        {
            field.setFont(AppFonts.normal(15));
            ModernButtonStyler.styleInput(field, theme);
            field.setCaretColor(theme.displayForeground());
        }

        darstellungBox.setFont(AppFonts.normal(14));
        darstellungBox.setBackground(theme.inputBackground());
        darstellungBox.setForeground(theme.displayForeground());

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
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
        controls.add(createButton("Kopieren", this::copyResult));

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
        label.setFont(AppFonts.fett(18));

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
        title.setFont(AppFonts.fett(28));

        JPanel resultBox = new JPanel(new GridLayout(0, 1, 0, 10));
        resultBox.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(44));
        detailLabel.setFont(AppFonts.normal(18));
        statusLabel.setFont(AppFonts.normal(14));
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

    private void calculateBinary(BinaryOperator<KomplexeZahl> operation, String status)
    {
        try
        {
            readInputs();
            state.setErgebnis(operation.apply(state.getErsteZahl(), state.getZweiteZahl()));
            state.setStatus(status);
            refresh();
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
            state.setStatus(statusLabel.getText());
        }
    }

    private void calculateUnary(UnaryOperator<KomplexeZahl> operation, String status)
    {
        calculateBinary((erste, zweite) -> operation.apply(erste), status);
    }

    private void readInputs()
    {
        state.setErsteZahl(new KomplexeZahl(parse(aRealField), parse(aImagField)));
        state.setZweiteZahl(new KomplexeZahl(parse(bRealField), parse(bImagField)));
    }

    private double parse(JTextField field)
    {
        return ZahlenEingabe.lese(field.getText());
    }

    private void refresh()
    {
        resultLabel.setText(formatter.formatiere(state.getErgebnis(), state.getDarstellung()));
        detailLabel.setText("|z| = " + formatter.formatiereDouble(state.getErgebnis().betrag())
                + " | arg = " + formatter.formatiereDouble(state.getErgebnis().phaseDeg()) + "°");
        statusAnzeige.zeigeErfolg(state.getStatus());
    }

    private void copyResult()
    {
        StringSelection selection = new StringSelection(resultLabel.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        state.setStatus("Ergebnis kopiert");
        refresh();
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
}
