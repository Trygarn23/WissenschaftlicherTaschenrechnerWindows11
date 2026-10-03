package ui.shell;

import ui.animation.AnimationSupport;
import ui.theme.AppTheme;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Color;

/**
 * Statuszeile der Modi: Erfolg in normaler Farbe, Fehler in Warnfarbe.
 * Optional werden Ergebnisbereiche kurz eingefärbt, damit man die Rückmeldung auch sieht.
 */
public final class StatusAnzeige
{
    private final JLabel label;
    private final JComponent[] hervorheben;
    private AppTheme theme;

    public StatusAnzeige(JLabel label, JComponent... hervorheben)
    {
        this.label = label;
        this.hervorheben = hervorheben;
    }

    public void setTheme(AppTheme theme)
    {
        this.theme = theme;
        label.setForeground(theme.secondaryDisplayForeground());
    }

    public void zeigeErfolg(String text)
    {
        label.setText(text);
        if (theme != null)
        {
            label.setForeground(theme.secondaryDisplayForeground());
            pulsiere(theme.successPulseColor());
        }
    }

    public void zeigeFehler(String meldung)
    {
        zeigeFehler(meldung, "Ungültige Eingabe");
    }

    /** Zeigt die Fehlermeldung oder, wenn keine da ist, einen allgemeinen Text. */
    public void zeigeFehler(String meldung, String ersatzText)
    {
        label.setText(meldung == null || meldung.isBlank() ? ersatzText : meldung);
        label.setForeground(theme == null ? Color.RED : theme.dangerBackground());
        if (theme != null)
        {
            pulsiere(theme.errorPulseColor());
        }
    }

    private void pulsiere(Color farbe)
    {
        for (JComponent komponente : hervorheben)
        {
            AnimationSupport.pulseBackground(komponente, farbe, 200);
        }
    }
}
