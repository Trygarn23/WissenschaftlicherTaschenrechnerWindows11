package modes.gleichung.ui;

import common.formatting.ZahlenEingabe;
import common.state.RechnerModus;
import modes.gleichung.logic.GleichungsLoeser;
import modes.gleichung.model.GleichungsErgebnis;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GleichungPanel extends JPanel implements ModePanel
{
    private final GleichungsLoeser loeser = new GleichungsLoeser();

    private final JTextField aField = new JTextField("1");
    private final JTextField bField = new JTextField("0");
    private final JTextField cField = new JTextField("-4");
    private final JTextField gleichungField = new JTextField("x^2 - 4 = 0");
    private final JLabel resultLabel = new JLabel("–");
    private final JLabel detailLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final List<JButton> buttons = new ArrayList<>();
    private final List<JTextField> fields = List.of(aField, bField, cField, gleichungField);

    private AppTheme theme;

    public GleichungPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.GLEICHUNG;
    }

    @Override
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
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

        Runnable loeseKoeffizienten = () -> loese(() -> loeser.loese(lese(aField), lese(bField), lese(cField)));
        Runnable loeseText = () -> loese(() -> loeser.loese(gleichungField.getText()));

        JPanel koeffizienten = new JPanel(new BorderLayout(0, 8));
        koeffizienten.setOpaque(false);
        JLabel formel = new JLabel("ax² + bx + c = 0");
        formel.setFont(AppFonts.fett(18));
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        row.add(wrapField("a", aField, loeseKoeffizienten));
        row.add(wrapField("b", bField, loeseKoeffizienten));
        row.add(wrapField("c", cField, loeseKoeffizienten));
        koeffizienten.add(formel, BorderLayout.NORTH);
        koeffizienten.add(row, BorderLayout.CENTER);
        koeffizienten.add(createButton("Lösen", loeseKoeffizienten), BorderLayout.SOUTH);

        JPanel freitext = new JPanel(new BorderLayout(0, 8));
        freitext.setOpaque(false);
        JLabel freitextTitel = new JLabel("Oder als Gleichung mit x");
        freitextTitel.setFont(AppFonts.fett(18));
        freitext.add(freitextTitel, BorderLayout.NORTH);
        freitext.add(wrapField("z. B. 2x + 3 = 7 oder x^2 = 2x + 3", gleichungField, loeseText), BorderLayout.CENTER);
        freitext.add(createButton("Gleichung lösen", loeseText), BorderLayout.SOUTH);

        panel.add(koeffizienten, BorderLayout.NORTH);
        panel.add(freitext, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapField(String labelText, JTextField field, Runnable enterAktion)
    {
        field.addActionListener(e -> enterAktion.run());
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

        JLabel title = new JLabel("Gleichung");
        title.setFont(AppFonts.fett(28));

        JPanel resultBox = new JPanel(new BorderLayout(0, 10));
        resultBox.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(34));
        detailLabel.setFont(AppFonts.normal(16));
        detailLabel.setVerticalAlignment(SwingConstants.TOP);
        statusLabel.setFont(AppFonts.normal(14));
        resultBox.add(resultLabel, BorderLayout.NORTH);
        resultBox.add(detailLabel, BorderLayout.CENTER);
        resultBox.add(statusLabel, BorderLayout.SOUTH);

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

    private void loese(Supplier<GleichungsErgebnis> berechnung)
    {
        try
        {
            GleichungsErgebnis ergebnis = berechnung.get();
            resultLabel.setText(ergebnis.anzeige());
            detailLabel.setText("<html>" + String.join("<br>", ergebnis.rechenweg().stream().map(GleichungPanel::escape).toList()) + "</html>");
            statusAnzeige.zeigeErfolg("Gelöst");
        }
        catch (IllegalArgumentException e)
        {
            resultLabel.setText("–");
            detailLabel.setText(" ");
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private static double lese(JTextField field)
    {
        return ZahlenEingabe.lese(field.getText());
    }

    private static String escape(String text)
    {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void applyThemeToChildren(Component component)
    {
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
