package modes.statistik.ui;

import modes.statistik.formatting.StatistikFormatter;
import modes.statistik.model.StatistikErgebnis;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.stream.Stream;

/** Textausgabe der Kennzahlen plus Statuszeile. */
public class StatistikErgebnisPanel extends JPanel
{
    private static final String BEREIT = "Statistikmodus bereit";

    private final StatistikFormatter formatter = new StatistikFormatter();
    private final JTextArea resultArea = new JTextArea("Bereit");
    private final JLabel statusLabel = new JLabel(BEREIT);
    private final StatusAnzeige statusAnzeige;

    /** @param zusaetzlichHervorheben Komponenten, die bei Erfolg/Fehler mit aufleuchten (z. B. das Diagramm). */
    public StatistikErgebnisPanel(JComponent... zusaetzlichHervorheben)
    {
        super(new BorderLayout(0, 12));
        setOpaque(false);

        JComponent[] hervorheben = Stream.concat(Stream.of(resultArea), Arrays.stream(zusaetzlichHervorheben))
                .toArray(JComponent[]::new);
        statusAnzeige = new StatusAnzeige(statusLabel, hervorheben);

        resultArea.setEditable(false);
        resultArea.setFont(AppFonts.festeBreite(14));

        add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public void applyTheme(AppTheme theme)
    {
        resultArea.setBackground(theme.displayBackground());
        resultArea.setForeground(theme.displayForeground());
        resultArea.setCaretColor(theme.displayForeground());
        statusAnzeige.setTheme(theme);
    }

    public void zeigeErgebnis(StatistikErgebnis ergebnis, String status)
    {
        resultArea.setText(formatter.formatiereErgebnis(ergebnis));
        statusAnzeige.zeigeErfolg(status + " | n = " + ergebnis.getAnzahl());
    }

    public void zeigeStatus(String status)
    {
        statusAnzeige.zeigeErfolg(status);
    }

    public void zeigeFehler(String meldung)
    {
        resultArea.setText("Fehler");
        statusAnzeige.zeigeFehler(meldung, "Ungültige Statistikdaten");
    }

    public void leeren()
    {
        resultArea.setText("Bereit");
        statusAnzeige.zeigeErfolg(BEREIT);
    }
}
