package modes.komplex.formatting;

import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexeZahl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class KomplexFormatter
{
    private final DecimalFormat format;

    public KomplexFormatter()
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator(',');
        format = new DecimalFormat("#,##0.##########", symbols);
    }

    public String formatiere(KomplexeZahl zahl, KomplexDarstellung darstellung)
    {
        return switch (darstellung)
        {
            case KARTESISCH -> formatiereKartesisch(zahl);
            case POLAR_RAD -> formatierePolar(zahl.betrag(), zahl.phaseRad(), "rad");
            case POLAR_DEG -> formatierePolar(zahl.betrag(), zahl.phaseDeg(), "°");
        };
    }

    public String formatiereKartesisch(KomplexeZahl zahl)
    {
        double imag = zahl.getImaginaer();
        String sign = imag < 0 ? " - " : " + ";
        return formatiereDouble(zahl.getReal()) + sign + formatiereDouble(Math.abs(imag)) + "i";
    }

    public String formatierePolar(double betrag, double phase, String einheit)
    {
        return formatiereDouble(betrag) + " ∠ " + formatiereDouble(phase) + einheit;
    }

    public String formatiereDouble(double wert)
    {
        if (Math.abs(wert) < 1e-10)
        {
            return "0";
        }
        return format.format(wert);
    }
}
