package modes.finanz.formatting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Deutsche Ausgabe von Geldbeträgen („1.234,56 €“), Prozentsätzen und Faktoren. */
public final class GeldFormat
{
    private GeldFormat()
    {
    }

    public static String euro(BigDecimal betrag)
    {
        return formatiere(betrag, "#,##0.00") + " €";
    }

    public static String prozent(BigDecimal prozent)
    {
        return formatiere(prozent, "#,##0.####") + " %";
    }

    public static String zahl(BigDecimal zahl)
    {
        return formatiere(zahl, "#,##0.######");
    }

    // DecimalFormat ist nicht threadsicher, daher pro Aufruf neu.
    private static String formatiere(BigDecimal wert, String muster)
    {
        DecimalFormat format = new DecimalFormat(muster, DecimalFormatSymbols.getInstance(Locale.GERMANY));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(wert);
    }
}
