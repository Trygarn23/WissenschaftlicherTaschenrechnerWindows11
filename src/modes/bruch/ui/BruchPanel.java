package modes.bruch.ui;

import common.formatting.ZahlenAnzeige;
import common.state.RechnerModus;
import modes.bruch.model.Bruch;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

public class BruchPanel extends JPanel implements ModePanel
{
    // Ab diesem Nenner wird eine Dezimaleingabe wie 0,333333333333 als Näherung gedeutet (→ 1/3).
    private static final long NAEHERUNG_MAX_NENNER = 10_000;

    private final JTextField aField = new JTextField("3/4");
    private final JTextField bField = new JTextField("1/6");
    private final JLabel resultLabel = new JLabel("0");
    private final JLabel dezimalLabel = new JLabel("= 0");
    private final JLabel gemischtLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final List<JButton> buttons = new ArrayList<>();

    private Bruch ergebnis;
    private Consumer<String> ergebnisUebernehmenListener;
    private AppTheme theme;

    public BruchPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
        aField.addActionListener(e -> kuerzen());
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.BRUCH;
    }

    /** Bekommt den Dezimalwert des Ergebnisses als Text (ohne Tausenderpunkte), z. B. „0,75“. */
    public void setErgebnisUebernehmenListener(Consumer<String> listener)
    {
        this.ergebnisUebernehmenListener = listener;
    }

    @Override
    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());

        for (JTextField field : List.of(aField, bField))
        {
            field.setFont(AppFonts.normal(15));
            ModernButtonStyler.styleInput(field, theme);
            field.setCaretColor(theme.displayForeground());
        }
        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        dezimalLabel.setForeground(theme.secondaryDisplayForeground());
        gemischtLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
    }

    private JPanel buildInputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 1, 0, 12));
        fieldsPanel.setOpaque(false);
        fieldsPanel.add(wrapField("Bruch A", aField));
        fieldsPanel.add(wrapField("Bruch B", bField));

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("+", () -> rechne(Bruch::plus, "Addition")));
        controls.add(createButton("−", () -> rechne(Bruch::minus, "Subtraktion")));
        controls.add(createButton("×", () -> rechne(Bruch::mal, "Multiplikation")));
        controls.add(createButton("÷", () -> rechne(Bruch::durch, "Division")));
        controls.add(createButton("Kürzen/Umwandeln", this::kuerzen));
        controls.add(createButton("Ins Display übernehmen", this::uebernehmen));

        panel.add(fieldsPanel, BorderLayout.NORTH);
        panel.add(controls, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapField(String labelText, JTextField field)
    {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(AppFonts.fett(16));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildResultPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JLabel title = new JLabel("Bruch");
        title.setFont(AppFonts.fett(28));

        JPanel resultBox = new JPanel(new GridLayout(0, 1, 0, 10));
        resultBox.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(44));
        dezimalLabel.setFont(AppFonts.normal(18));
        gemischtLabel.setFont(AppFonts.normal(18));
        statusLabel.setFont(AppFonts.normal(14));
        resultBox.add(resultLabel);
        resultBox.add(dezimalLabel);
        resultBox.add(gemischtLabel);
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

    private void rechne(BinaryOperator<Bruch> operation, String status)
    {
        try
        {
            zeige(operation.apply(Bruch.parse(aField.getText()), Bruch.parse(bField.getText())), status);
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void kuerzen()
    {
        try
        {
            Bruch bruch = Bruch.parse(aField.getText());
            if (bruch.nenner() > NAEHERUNG_MAX_NENNER)
            {
                Bruch naeherung = Bruch.ausDezimal(bruch.alsDouble(), NAEHERUNG_MAX_NENNER).orElse(null);
                if (naeherung != null)
                {
                    zeige(naeherung, "Periodischen Bruch erkannt");
                    return;
                }
            }
            zeige(bruch, "Gekürzt");
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void uebernehmen()
    {
        if (ergebnis == null)
        {
            statusAnzeige.zeigeFehler("Noch kein Ergebnis zum Übernehmen.");
            return;
        }
        if (ergebnisUebernehmenListener == null)
        {
            statusAnzeige.zeigeFehler("Übernehmen ist hier nicht verfügbar.");
            return;
        }
        ergebnisUebernehmenListener.accept(dezimalText(ergebnis));
        statusAnzeige.zeigeErfolg("Ergebnis übernommen");
    }

    private void zeige(Bruch bruch, String status)
    {
        ergebnis = bruch;
        resultLabel.setText(bruch.toString());
        dezimalLabel.setText("= " + ZahlenAnzeige.formatiere(bruch.alsDouble()));
        String gemischt = bruch.alsGemischteZahl();
        gemischtLabel.setText(gemischt.equals(bruch.toString()) ? " " : "gemischt: " + gemischt);
        statusAnzeige.zeigeErfolg(status);
    }

    private static String dezimalText(Bruch bruch)
    {
        // Tausenderpunkte raus, damit das Display den Wert wieder einlesen kann.
        return ZahlenAnzeige.formatiere(bruch.alsDouble()).replace(".", "");
    }

    private void applyThemeToChildren(Component component)
    {
        if (theme == null)
        {
            return;
        }

        if (component instanceof JLabel label && label != resultLabel && label != dezimalLabel
                && label != gemischtLabel && label != statusLabel)
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
