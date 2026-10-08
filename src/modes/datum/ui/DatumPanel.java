package modes.datum.ui;

import common.state.RechnerModus;
import modes.datum.logic.DatumsRechner;
import modes.datum.logic.DatumsRechner.Einheit;
import modes.datum.logic.DatumsRechner.Verschiebung;
import modes.datum.logic.DatumsRechner.Zeitraum;
import modes.datum.logic.ZeitSummierer;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class DatumPanel extends JPanel implements ModePanel
{
    private final JTextField vonField = new JTextField();
    private final JTextField bisField = new JTextField();
    private final JCheckBox feiertageBox = new JCheckBox("Bundesweite Feiertage abziehen");
    private final JTextField datumField = new JTextField();
    private final JTextField anzahlField = new JTextField("1");
    private final JComboBox<Einheit> einheitBox = new JComboBox<>(Einheit.values());
    private final JTextArea zeitenArea = new JTextArea("7:30\n-0:30", 8, 20);
    private final JTabbedPane tabs = new JTabbedPane();

    private final JLabel resultLabel = new JLabel("–");
    private final JLabel detailLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final List<JButton> buttons = new ArrayList<>();

    private AppTheme theme;

    public DatumPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        String heute = DatumsRechner.formatiere(LocalDate.now());
        vonField.setText(heute);
        bisField.setText(heute);
        datumField.setText(heute);

        tabs.setFocusable(false);
        tabs.addTab("Zeitraum", buildZeitraumTab());
        tabs.addTab("Rechnen", buildRechnenTab());
        tabs.addTab("Stunden", buildStundenTab());
        tabs.setPreferredSize(new Dimension(360, 0));

        add(tabs, BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.DATUM;
    }

    @Override
    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        tabs.setBackground(theme.panelBackground());
        tabs.setForeground(theme.displayForeground());

        for (JComponent eingabe : List.of(vonField, bisField, datumField, anzahlField, zeitenArea))
        {
            eingabe.setFont(AppFonts.normal(15));
            ModernButtonStyler.styleInput(eingabe, theme);
        }
        for (JTextField field : List.of(vonField, bisField, datumField, anzahlField))
        {
            field.setCaretColor(theme.displayForeground());
        }
        zeitenArea.setCaretColor(theme.displayForeground());

        einheitBox.setFont(AppFonts.normal(14));
        einheitBox.setBackground(theme.inputBackground());
        einheitBox.setForeground(theme.displayForeground());

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }

        applyThemeToChildren(this);
        resultLabel.setForeground(theme.displayForeground());
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
    }

    private JPanel buildZeitraumTab()
    {
        JPanel panel = tabPanel();
        panel.add(datumZeile("Von", vonField));
        panel.add(datumZeile("Bis", bisField));
        panel.add(feiertageBox);
        feiertageBox.setOpaque(false);
        feiertageBox.setFocusable(false);

        JPanel aktionen = new JPanel(new GridLayout(1, 2, 8, 0));
        aktionen.setOpaque(false);
        aktionen.add(createButton("Tage zwischen", this::berechneZeitraum));
        aktionen.add(createButton("Arbeitstage", this::berechneArbeitstage));
        panel.add(aktionen);

        vonField.addActionListener(e -> berechneZeitraum());
        bisField.addActionListener(e -> berechneZeitraum());
        return wrapNorth(panel);
    }

    private JPanel buildRechnenTab()
    {
        JPanel panel = tabPanel();
        panel.add(datumZeile("Datum", datumField));

        JPanel anzahl = new JPanel(new GridLayout(1, 2, 8, 0));
        anzahl.setOpaque(false);
        anzahl.add(anzahlField);
        anzahl.add(einheitBox);
        panel.add(beschriftet("Anzahl", anzahl));

        JPanel aktionen = new JPanel(new GridLayout(1, 3, 8, 0));
        aktionen.setOpaque(false);
        aktionen.add(createButton("+ addieren", () -> berechneVerschiebung(1)));
        aktionen.add(createButton("− abziehen", () -> berechneVerschiebung(-1)));
        aktionen.add(createButton("Wochentag", this::zeigeWochentag));
        panel.add(aktionen);

        datumField.addActionListener(e -> zeigeWochentag());
        anzahlField.addActionListener(e -> berechneVerschiebung(1));
        return wrapNorth(panel);
    }

    private JPanel buildStundenTab()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Zeiten (je Zeile oder mit Komma getrennt)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(zeitenArea), BorderLayout.CENTER);
        panel.add(createButton("Summieren", this::summiereStunden), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JLabel title = new JLabel("Datum/Zeit");
        title.setFont(AppFonts.fett(28));

        JPanel resultBox = new JPanel(new GridLayout(0, 1, 0, 10));
        resultBox.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(40));
        detailLabel.setFont(AppFonts.normal(18));
        statusLabel.setFont(AppFonts.normal(14));
        resultBox.add(resultLabel);
        resultBox.add(detailLabel);
        resultBox.add(statusLabel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(resultBox, BorderLayout.CENTER);
        return panel;
    }

    private void berechneZeitraum()
    {
        ausfuehren(() ->
        {
            Zeitraum zeitraum = DatumsRechner.zeitraum(DatumsRechner.lese(vonField.getText()), DatumsRechner.lese(bisField.getText()));
            Period p = zeitraum.periode();
            zeigeErgebnis(zeitraum.tage() + " Tage",
                    zeitraum.wochen() + " Wochen " + zeitraum.restTage() + " Tage | "
                            + p.getYears() + " J. " + p.getMonths() + " M. " + p.getDays() + " T.",
                    "Zeitraum berechnet");
        });
    }

    private void berechneArbeitstage()
    {
        ausfuehren(() ->
        {
            boolean feiertage = feiertageBox.isSelected();
            long tage = DatumsRechner.arbeitstage(DatumsRechner.lese(vonField.getText()), DatumsRechner.lese(bisField.getText()), feiertage);
            zeigeErgebnis(tage + " Arbeitstage",
                    "Mo–Fr inkl. Start und Ende" + (feiertage ? ", ohne bundesweite Feiertage" : ""),
                    "Arbeitstage gezählt");
        });
    }

    private void berechneVerschiebung(int vorzeichen)
    {
        ausfuehren(() ->
        {
            LocalDate start = DatumsRechner.lese(datumField.getText());
            long anzahl = leseAnzahl() * vorzeichen;
            Verschiebung ergebnis = DatumsRechner.verschiebe(start, anzahl, (Einheit) einheitBox.getSelectedItem());
            zeigeErgebnis(DatumsRechner.formatiere(ergebnis.datum()),
                    DatumsRechner.wochentag(ergebnis.datum())
                            + (ergebnis.gekappt() ? " | auf Monatsende gekürzt" : ""),
                    "Datum verschoben");
        });
    }

    private void zeigeWochentag()
    {
        ausfuehren(() ->
        {
            LocalDate datum = DatumsRechner.lese(datumField.getText());
            zeigeErgebnis(DatumsRechner.wochentag(datum),
                    DatumsRechner.formatiere(datum) + " | " + DatumsRechner.kalenderwoche(datum),
                    "Wochentag ermittelt");
        });
    }

    private void summiereStunden()
    {
        ausfuehren(() ->
        {
            int minuten = ZeitSummierer.summiereMinuten(zeitenArea.getText());
            zeigeErgebnis(ZeitSummierer.formatiereStunden(minuten), ZeitSummierer.formatiereDezimal(minuten), "Zeiten summiert");
        });
    }

    private long leseAnzahl()
    {
        String text = anzahlField.getText().strip();
        try
        {
            return Long.parseLong(text);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("„" + text + "“ ist keine gültige ganze Zahl.");
        }
    }

    private void ausfuehren(Runnable aktion)
    {
        try
        {
            aktion.run();
        }
        catch (IllegalArgumentException | DateTimeException | ArithmeticException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void zeigeErgebnis(String ergebnis, String detail, String status)
    {
        resultLabel.setText(ergebnis);
        detailLabel.setText(detail);
        statusAnzeige.zeigeErfolg(status);
    }

    private JPanel datumZeile(String titel, JTextField field)
    {
        JPanel zeile = new JPanel(new BorderLayout(8, 0));
        zeile.setOpaque(false);
        zeile.add(field, BorderLayout.CENTER);
        zeile.add(createButton("Heute", () -> field.setText(DatumsRechner.formatiere(LocalDate.now()))), BorderLayout.EAST);
        return beschriftet(titel + " (TT.MM.JJJJ)", zeile);
    }

    private JPanel beschriftet(String titel, JComponent inhalt)
    {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(new JLabel(titel), BorderLayout.NORTH);
        panel.add(inhalt, BorderLayout.CENTER);
        return panel;
    }

    private JPanel tabPanel()
    {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel wrapNorth(JPanel inhalt)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(inhalt, BorderLayout.NORTH);
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

    private void applyThemeToChildren(Component component)
    {
        if (component instanceof JLabel label && label != resultLabel && label != detailLabel && label != statusLabel)
        {
            label.setForeground(theme.displayForeground());
        }
        else if (component instanceof JCheckBox box)
        {
            box.setForeground(theme.displayForeground());
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
