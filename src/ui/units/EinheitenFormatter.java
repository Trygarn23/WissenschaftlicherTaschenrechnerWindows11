package ui.units;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

final class EinheitenFormatter
{
    private final DecimalFormat format;

    EinheitenFormatter()
    {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
        format = new DecimalFormat("#,##0.##########", symbols);
    }

    String formatiere(double wert)
    {
        if (!Double.isFinite(wert))
        {
            return "nicht definiert";
        }

        return format.format(wert);
    }
}
