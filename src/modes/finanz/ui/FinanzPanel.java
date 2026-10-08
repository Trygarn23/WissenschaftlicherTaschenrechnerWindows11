package modes.finanz.ui;

import common.formatting.ZahlenEingabe;
import common.state.RechnerModus;
import modes.finanz.logic.FinanzRechnung;
import modes.finanz.model.FinanzErgebnis;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinanzPanel extends JPanel implements ModePanel
{
    private static final int MAX_FELDER = 3;

    private final JComboBox<FinanzRechnung> rechnungBox = new JComboBox<>(FinanzRechnung.values());
    private final List<JLabel> feldLabels = new ArrayList<>();
    private final List<JTextField> fields = new ArrayList<>();
    private final JButton berechnenButton = new JButton("Berechnen");
    private final JLabel resultLabel = new JLabel(" ");
    private final JLabel rechenwegLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);

    private AppTheme theme;

    public FinanzPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
        zeigeRechnung();
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.FINANZ;
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

        rechnungBox.setFont(AppFonts.normal(14));
        rechnungBox.setBackground(theme.inputBackground());
        rechnungBox.setForeground(theme.displayForeground());
        ModernButtonStyler.styleButton(berechnenButton, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        rechenwegLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
    }

    private JPanel buildInputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        fieldsPanel.setOpaque(false);
        fieldsPanel.add(rechnungBox);
        for (int i = 0; i < MAX_FELDER; i++)
        {
            JLabel label = new JLabel(" ");
            JTextField field = new JTextField();
            field.addActionListener(e -> berechne());
            feldLabels.add(label);
            fields.add(field);
            fieldsPanel.add(label);
            fieldsPanel.add(field);
        }

        berechnenButton.setFocusable(false);
        berechnenButton.addActionListener(e -> berechne());
        rechnungBox.addActionListener(e -> zeigeRechnung());

        panel.add(fieldsPanel, BorderLayout.NORTH);
        panel.add(berechnenButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JLabel title = new JLabel("Finanzen");
        title.setFont(AppFonts.fett(28));

        JPanel resultBox = new JPanel(new GridLayout(0, 1, 0, 10));
        resultBox.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(40));
        rechenwegLabel.setFont(AppFonts.normal(16));
        statusLabel.setFont(AppFonts.normal(14));
        resultBox.add(resultLabel);
        resultBox.add(rechenwegLabel);
        resultBox.add(statusLabel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(resultBox, BorderLayout.CENTER);
        return panel;
    }

    private FinanzRechnung gewaehlteRechnung()
    {
        return (FinanzRechnung) rechnungBox.getSelectedItem();
    }

    private void zeigeRechnung()
    {
        FinanzRechnung rechnung = gewaehlteRechnung();
        for (int i = 0; i < MAX_FELDER; i++)
        {
            boolean sichtbar = i < rechnung.felder().size();
            feldLabels.get(i).setVisible(sichtbar);
            fields.get(i).setVisible(sichtbar);
            if (sichtbar)
            {
                feldLabels.get(i).setText(rechnung.felder().get(i));
                fields.get(i).setText(rechnung.vorgaben().get(i));
            }
        }
        berechne();
    }

    private void berechne()
    {
        FinanzRechnung rechnung = gewaehlteRechnung();
        try
        {
            List<BigDecimal> werte = new ArrayList<>();
            for (int i = 0; i < rechnung.felder().size(); i++)
            {
                werte.add(lese(fields.get(i)));
            }
            FinanzErgebnis ergebnis = rechnung.berechne(werte);
            resultLabel.setText(ergebnis.anzeige());
            rechenwegLabel.setText("<html>" + String.join("<br>", ergebnis.rechenweg()) + "</html>");
            statusAnzeige.zeigeErfolg(rechnung.toString());
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            resultLabel.setText("–");
            rechenwegLabel.setText(" ");
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private BigDecimal lese(JTextField field)
    {
        double zahl = ZahlenEingabe.lese(field.getText());
        if (!Double.isFinite(zahl))
        {
            throw new IllegalArgumentException("„" + field.getText().trim() + "“ ist keine gültige Zahl.");
        }
        return BigDecimal.valueOf(zahl);
    }

    private void applyThemeToChildren(Component component)
    {
        if (component instanceof JLabel label && label != resultLabel && label != rechenwegLabel && label != statusLabel)
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
