package common.formatting;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ZahlenFormatter
{
    private static final double SCIENTIFIC_UPPER_LIMIT = 1_000_000_000_000.0;
    private static final double SCIENTIFIC_LOWER_LIMIT = 0.000000001;
    private static final int MIN_NACHKOMMASTELLEN = 2;
    private static final int MAX_NACHKOMMASTELLEN = 15;

    private int nachkommastellen = 11;
    private ZahlenFormatModus formatModus = ZahlenFormatModus.AUTO;

    public String formatiereZahl(double zahl)
    {
        if (zahl == 0.0)
        {
            return "0";
        }

        double absolut = Math.abs(zahl);
        if (formatModus == ZahlenFormatModus.WISSENSCHAFTLICH
                || (formatModus == ZahlenFormatModus.AUTO && (absolut >= SCIENTIFIC_UPPER_LIMIT || absolut < SCIENTIFIC_LOWER_LIMIT)))
        {
            return formatiereWissenschaftlich(zahl);
        }

        DecimalFormatSymbols symbole = new DecimalFormatSymbols(Locale.GERMANY);
        symbole.setDecimalSeparator(',');
        symbole.setGroupingSeparator('.');

        DecimalFormat format = new DecimalFormat("#,###." + "#".repeat(nachkommastellen), symbole);
        String text = format.format(zahl);

        if (text.contains(",") && text.endsWith(",0"))
        {
            text = text.substring(0, text.indexOf(","));
        }

        return text;
    }

    public String formatiereLiveAnzeige(String ausdruck)
    {
        if (ausdruck == null || ausdruck.isEmpty()) return "0";

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < ausdruck.length(); i++)
        {
            char zeichen = ausdruck.charAt(i);

            if (Character.isDigit(zeichen) || istDezimaltrennerVorZahl(ausdruck, i))
            {
                int start = i;
                i = endeZahlenLiteral(ausdruck, i);
                String zahl = ausdruck.substring(start, i + 1);
                result.append(formatiereLiveZahl(zahl));
            }
            else
            {
                result.append(zeichen);
            }
        }

        return result.toString();
    }

    public String formatiereLiveZahl(String zahl)
    {
        if (zahl == null || zahl.isEmpty()) return "";

        if (enthaeltExponent(zahl))
        {
            return zahl.replace('.', ',');
        }

        boolean negativ = zahl.startsWith("-");
        String roh = negativ ? zahl.substring(1) : zahl;

        String ganzzahlTeil = roh;
        String dezimalTeil = "";

        if (roh.contains(","))
        {
            String[] teile = roh.split(",", 2);
            ganzzahlTeil = teile[0];
            dezimalTeil = "," + teile[1];
        }

        ganzzahlTeil = ganzzahlTeil.replace(".", "");
        ganzzahlTeil = ganzzahlTeil.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ".");

        return (negativ ? "-" : "") + ganzzahlTeil + dezimalTeil;
    }

    public String interneDarstellung(double wert)
    {
        String text = BigDecimal.valueOf(wert).stripTrailingZeros().toPlainString();
        return text.replace('.', ',');
    }

    public void setNachkommastellen(int nachkommastellen)
    {
        this.nachkommastellen = Math.max(MIN_NACHKOMMASTELLEN, Math.min(MAX_NACHKOMMASTELLEN, nachkommastellen));
    }

    public int getNachkommastellen()
    {
        return nachkommastellen;
    }

    public void setFormatModus(ZahlenFormatModus formatModus)
    {
        this.formatModus = formatModus == null ? ZahlenFormatModus.AUTO : formatModus;
    }

    public ZahlenFormatModus getFormatModus()
    {
        return formatModus;
    }

    private String formatiereWissenschaftlich(double zahl)
    {
        DecimalFormatSymbols symbole = new DecimalFormatSymbols(Locale.GERMANY);
        symbole.setDecimalSeparator(',');

        DecimalFormat format = new DecimalFormat("0." + "#".repeat(nachkommastellen) + "E0", symbole);
        return format.format(zahl).replace('E', 'e');
    }

    private boolean istDezimaltrennerVorZahl(String text, int index)
    {
        char zeichen = text.charAt(index);
        return (zeichen == ',' || zeichen == '.')
                && index + 1 < text.length()
                && Character.isDigit(text.charAt(index + 1));
    }

    private int endeZahlenLiteral(String text, int start)
    {
        int i = start;
        boolean exponentGesehen = false;

        while (i + 1 < text.length())
        {
            char naechstes = text.charAt(i + 1);

            if (Character.isDigit(naechstes) || naechstes == ',' || naechstes == '.')
            {
                i++;
                continue;
            }

            if ((naechstes == 'e' || naechstes == 'E') && !exponentGesehen)
            {
                int exponentStart = i + 2;
                if (exponentStart < text.length() && (text.charAt(exponentStart) == '+' || text.charAt(exponentStart) == '-'))
                {
                    exponentStart++;
                }

                if (exponentStart < text.length() && Character.isDigit(text.charAt(exponentStart)))
                {
                    exponentGesehen = true;
                    i++;
                    continue;
                }
            }

            if ((text.charAt(i) == 'e' || text.charAt(i) == 'E') && (naechstes == '+' || naechstes == '-'))
            {
                i++;
                continue;
            }

            break;
        }

        return i;
    }

    private boolean enthaeltExponent(String zahl)
    {
        return zahl.indexOf('e') >= 0 || zahl.indexOf('E') >= 0;
    }
}
