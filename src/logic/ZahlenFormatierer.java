package logic;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ZahlenFormatierer
{
    public String formatiereZahl(double zahl)
    {
        DecimalFormatSymbols symbole = new DecimalFormatSymbols(Locale.GERMANY);
        symbole.setDecimalSeparator(',');
        symbole.setGroupingSeparator('.');

        DecimalFormat format = new DecimalFormat("#,###.###########", symbole);
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

        if (!ausdruck.matches("-?[0-9.,]+"))
        {
            return ausdruck;
        }

        boolean negativ = ausdruck.startsWith("-");
        String roh = negativ ? ausdruck.substring(1) : ausdruck;

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
}