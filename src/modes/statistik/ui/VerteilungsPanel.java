package modes.statistik.ui;

import common.formatting.ZahlenAnzeige;
import common.formatting.ZahlenEingabe;
import modes.statistik.logic.Verteilungen;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** Kleine Rechner für Binomial- und Normalverteilung. */
public class VerteilungsPanel extends JPanel
{
    private final JTextField nField = new JTextField("10");
    private final JTextField pField = new JTextField("0,5");
    private final JTextField kField = new JTextField("5");
    private final JTextField muField = new JTextField("0");
    private final JTextField sigmaField = new JTextField("1");
    private final JTextField xField = new JTextField("1,96");
    private final JTextField aField = new JTextField("-1");
    private final JTextField bField = new JTextField("1");
    private final JTextField quantilField = new JTextField("0,95");
    private final JLabel binomialErgebnis = new JLabel(" ");
    private final JLabel normalErgebnis = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Werte eingeben und berechnen");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel, binomialErgebnis, normalErgebnis);
    private final List<JButton> buttons = new ArrayList<>();
    private final List<JTextField> fields = List.of(nField, pField, kField, muField, sigmaField,
            xField, aField, bField, quantilField);

    public VerteilungsPanel()
    {
        super(new BorderLayout(0, 12));
        setOpaque(false);

        JPanel abschnitte = new JPanel(new GridLayout(1, 2, 14, 0));
        abschnitte.setOpaque(false);
        abschnitte.add(abschnitt("Binomialverteilung B(n; p)", "Binomial berechnen", this::berechneBinomial,
                binomialErgebnis, "n", nField, "p", pField, "k", kField));
        abschnitte.add(abschnitt("Normalverteilung N(μ; σ)", "Normal berechnen", this::berechneNormal,
                normalErgebnis, "μ", muField, "σ", sigmaField, "x für P(X ≤ x)", xField,
                "a für P(a ≤ X ≤ b)", aField, "b", bField, "Quantil zu p", quantilField));

        add(abschnitte, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public void applyTheme(AppTheme theme)
    {
        statusAnzeige.setTheme(theme);
        for (JTextField field : fields)
        {
            field.setFont(AppFonts.normal(14));
            ModernButtonStyler.styleInput(field, theme);
            field.setCaretColor(theme.displayForeground());
        }

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }
    }

    /** @param labelsUndFelder abwechselnd Beschriftung (String) und Eingabefeld */
    private JPanel abschnitt(String titel, String buttonText, Runnable aktion, JLabel ergebnisLabel, Object... labelsUndFelder)
    {
        JLabel titelLabel = new JLabel(titel);
        titelLabel.setFont(AppFonts.fett(20));

        JPanel formular = new JPanel(new GridLayout(0, 2, 8, 8));
        formular.setOpaque(false);
        for (int i = 0; i < labelsUndFelder.length; i += 2)
        {
            JTextField field = (JTextField) labelsUndFelder[i + 1];
            field.addActionListener(e -> fuehreAus(aktion));
            formular.add(new JLabel((String) labelsUndFelder[i]));
            formular.add(field);
        }

        JButton button = new JButton(buttonText);
        button.setFocusable(false);
        button.addActionListener(e -> fuehreAus(aktion));
        buttons.add(button);
        formular.add(new JLabel());
        formular.add(button);

        ergebnisLabel.setFont(AppFonts.festeBreite(15));
        ergebnisLabel.setVerticalAlignment(JLabel.TOP);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(titelLabel, BorderLayout.NORTH);
        panel.add(formular, BorderLayout.CENTER);
        panel.add(ergebnisLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void berechneBinomial()
    {
        int n = ganzeZahl(nField, "n");
        double p = ZahlenEingabe.lese(pField.getText());
        int k = ganzeZahl(kField, "k");

        binomialErgebnis.setText(html(
                "P(X = " + k + ") = " + zahl(Verteilungen.binomialGenau(n, p, k)),
                "P(X ≤ " + k + ") = " + zahl(Verteilungen.binomialHoechstens(n, p, k)),
                "P(X ≥ " + k + ") = " + zahl(Verteilungen.binomialMindestens(n, p, k)),
                "E(X) = " + zahl(Verteilungen.binomialErwartungswert(n, p)),
                "σ = " + zahl(Verteilungen.binomialStandardabweichung(n, p))));
        statusAnzeige.zeigeErfolg("Binomialverteilung berechnet");
    }

    private void berechneNormal()
    {
        double mu = ZahlenEingabe.lese(muField.getText());
        double sigma = ZahlenEingabe.lese(sigmaField.getText());
        List<String> zeilen = new ArrayList<>();

        if (!xField.getText().isBlank())
        {
            double x = ZahlenEingabe.lese(xField.getText());
            zeilen.add("P(X ≤ " + zahl(x) + ") = " + zahl(Verteilungen.normalVerteilung(mu, sigma, x)));
        }
        if (!aField.getText().isBlank() || !bField.getText().isBlank())
        {
            double a = ZahlenEingabe.lese(aField.getText());
            double b = ZahlenEingabe.lese(bField.getText());
            zeilen.add("P(" + zahl(a) + " ≤ X ≤ " + zahl(b) + ") = " + zahl(Verteilungen.normalIntervall(mu, sigma, a, b)));
        }
        if (!quantilField.getText().isBlank())
        {
            double wahrscheinlichkeit = ZahlenEingabe.lese(quantilField.getText());
            zeilen.add("P(X ≤ x) = " + zahl(wahrscheinlichkeit) + " für x = "
                    + zahl(Verteilungen.normalQuantil(mu, sigma, wahrscheinlichkeit)));
        }
        if (zeilen.isEmpty())
        {
            throw new IllegalArgumentException("Bitte x, a und b oder p für das Quantil eingeben.");
        }

        normalErgebnis.setText(html(zeilen.toArray(String[]::new)));
        statusAnzeige.zeigeErfolg("Normalverteilung berechnet");
    }

    private int ganzeZahl(JTextField field, String name)
    {
        double wert = ZahlenEingabe.lese(field.getText());
        if (wert != Math.rint(wert) || Math.abs(wert) > Integer.MAX_VALUE)
        {
            throw new IllegalArgumentException(name + " muss eine ganze Zahl sein.");
        }
        return (int) wert;
    }

    private String zahl(double wert)
    {
        return ZahlenAnzeige.formatiere(wert);
    }

    private String html(String... zeilen)
    {
        return "<html>" + String.join("<br>", zeilen) + "</html>";
    }

    private void fuehreAus(Runnable aktion)
    {
        try
        {
            aktion.run();
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage(), "Ungültige Eingabe");
        }
    }
}
