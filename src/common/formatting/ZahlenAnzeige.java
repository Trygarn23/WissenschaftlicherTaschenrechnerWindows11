package common.formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Einheitliche Zahlendarstellung für die Modi (Komplex, Matrix, Statistik, Einheiten):
 * deutsches Komma, Tausenderpunkte, bis zu 10 Nachkommastellen.
 */
public final class ZahlenAnzeige
{
    private static final double NULL_TOLERANZ = 1e-10;

    private ZahlenAnzeige()
    {
    }

    public static String formatiere(double wert)
    {
        if (!Double.isFinite(wert))
        {
            return "nicht definiert";
        }
        if (Math.abs(wert) < NULL_TOLERANZ)
        {
            return "0";
        }
        // DecimalFormat ist nicht threadsicher, deshalb pro Aufruf neu.
        return new DecimalFormat("#,##0.##########", DecimalFormatSymbols.getInstance(Locale.GERMANY)).format(wert);
    }
}
