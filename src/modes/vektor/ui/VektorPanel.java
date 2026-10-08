package modes.vektor.ui;

import common.formatting.ZahlenAnzeige;
import common.formatting.ZahlenEingabe;
import common.state.RechnerModus;
import modes.vektor.logic.VektorRechner;
import modes.vektor.model.Vektor;
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

public class VektorPanel extends JPanel implements ModePanel
{
    private final JComboBox<String> dimensionBox = new JComboBox<>(new String[] {"2D", "3D"});
    private final JTextField aX = new JTextField("0");
    private final JTextField aY = new JTextField("0");
    private final JTextField aZ = new JTextField("0");
    private final JTextField bX = new JTextField("0");
    private final JTextField bY = new JTextField("0");
    private final JTextField bZ = new JTextField("0");
    private final JTextField skalarField = new JTextField("1");
    private final List<JTextField> fields = List.of(aX, aY, aZ, bX, bY, bZ, skalarField);
    private final JLabel resultLabel = new JLabel("(0 | 0)");
    private final JLabel detailLabel = new JLabel("Vektoren eingeben und Rechnung wählen");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final List<JButton> buttons = new ArrayList<>();
    private JButton kreuzButton;

    private AppTheme theme;

    public VektorPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);
        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
        aktualisiereDimension();
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.VEKTOR;
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

        dimensionBox.setFont(AppFonts.normal(14));
        dimensionBox.setBackground(theme.inputBackground());
        dimensionBox.setForeground(theme.displayForeground());

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
        repaint();
    }

    private JPanel buildInputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(360, 0));

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.add(dimensionBox);
        fieldsPanel.add(buildVektorInput("a", aX, aY, aZ));
        fieldsPanel.add(buildVektorInput("b", bX, bY, bZ));
        fieldsPanel.add(wrapField("Skalar k", skalarField));

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("a + b", () -> zeigeVektor(() -> VektorRechner.addiere(a(), b()), "Addition a + b")));
        controls.add(createButton("a − b", () -> zeigeVektor(() -> VektorRechner.subtrahiere(a(), b()), "Subtraktion a − b")));
        controls.add(createButton("k · a", () -> zeigeVektor(() -> VektorRechner.skaliere(a(), skalar()), "Skalierung k · a")));
        controls.add(createButton("a · b", () -> zeigeZahl(() -> VektorRechner.skalarprodukt(a(), b()), "Skalarprodukt a · b")));
        controls.add(createButton("|a|", () -> zeigeZahl(() -> VektorRechner.betrag(a()), "Betrag (Länge) von a")));
        controls.add(createButton("Winkel", this::zeigeWinkel));
        kreuzButton = createButton("a × b", () -> zeigeVektor(() -> VektorRechner.kreuzprodukt(a(), b()), "Kreuzprodukt a × b (senkrecht auf a und b)"));
        controls.add(kreuzButton);
        controls.add(createButton("Abstand", () -> zeigeZahl(() -> VektorRechner.abstand(a(), b()), "Abstand der Punkte A und B")));
        controls.add(createButton("Mittelpunkt", () -> zeigeVektor(() -> VektorRechner.mittelpunkt(a(), b()), "Mittelpunkt der Strecke AB")));
        controls.add(createButton("Steigung", () -> zeigeZahl(() -> VektorRechner.steigung(a(), b()), "Steigung der Geraden durch A und B")));

        dimensionBox.addActionListener(e -> aktualisiereDimension());
        for (JTextField field : fields)
        {
            field.addActionListener(e -> buttons.getFirst().doClick());
        }

        panel.add(fieldsPanel, BorderLayout.NORTH);
        panel.add(controls, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildVektorInput(String name, JTextField x, JTextField y, JTextField z)
    {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel("Vektor / Punkt " + name);
        label.setFont(AppFonts.fett(16));

        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        row.add(wrapField("x", x));
        row.add(wrapField("y", y));
        row.add(wrapField("z", z));

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

        JLabel title = new JLabel("Vektor");
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

    private void aktualisiereDimension()
    {
        boolean dreiD = istDreiD();
        aZ.setEnabled(dreiD);
        bZ.setEnabled(dreiD);
        kreuzButton.setEnabled(dreiD);
        kreuzButton.setToolTipText(dreiD ? null : "Das Kreuzprodukt gibt es nur für 3D-Vektoren.");
    }

    private boolean istDreiD()
    {
        return "3D".equals(dimensionBox.getSelectedItem());
    }

    private Vektor a()
    {
        return lese(aX, aY, aZ);
    }

    private Vektor b()
    {
        return lese(bX, bY, bZ);
    }

    private Vektor lese(JTextField x, JTextField y, JTextField z)
    {
        if (istDreiD())
        {
            return Vektor.dreiD(zahl(x), zahl(y), zahl(z));
        }
        return Vektor.zweiD(zahl(x), zahl(y));
    }

    private double skalar()
    {
        return zahl(skalarField);
    }

    private double zahl(JTextField field)
    {
        return ZahlenEingabe.lese(field.getText());
    }

    private void zeigeVektor(Supplier<Vektor> rechnung, String erklaerung)
    {
        zeige(() -> rechnung.get().alsText(), erklaerung);
    }

    private void zeigeZahl(Supplier<Double> rechnung, String erklaerung)
    {
        zeige(() -> ZahlenAnzeige.formatiere(rechnung.get()), erklaerung);
    }

    private void zeigeWinkel()
    {
        zeige(() -> ZahlenAnzeige.formatiere(VektorRechner.winkelInGrad(a(), b())) + "°", "Winkel zwischen a und b");
    }

    private void zeige(Supplier<String> ergebnis, String erklaerung)
    {
        try
        {
            resultLabel.setText(ergebnis.get());
            detailLabel.setText(erklaerung);
            statusAnzeige.zeigeErfolg("Berechnet");
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
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
