package modes.komplex.formatting;

import common.formatting.ZahlenAnzeige;
import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexeZahl;

public class KomplexFormatter
{
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
        return ZahlenAnzeige.formatiere(wert);
    }
}
