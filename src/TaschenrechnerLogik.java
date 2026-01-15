import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TaschenrechnerLogik
{


    private boolean gleichGedrueckt = false;

    private final StringBuilder verlauf = new StringBuilder();
    private final StringBuilder ausdruck = new StringBuilder();


    public String eingabeZahl(String ziffer)
    {
        if (gleichGedrueckt)
        {
            ausdruck.setLength(0);
            gleichGedrueckt = false;
        }

        ausdruck.append(ziffer);
        return ausdruck.toString();
    }


    public String eingabeKomma()
    {
        if (gleichGedrueckt)
        {
            ausdruck.setLength(0);
            gleichGedrueckt = false;
        }

        if (ausdruck.isEmpty() || endetMitOperator())
        {
            ausdruck.append("0");
        }

        ausdruck.append(",");
        return ausdruck.toString();
    }


    public String wechselVorzeichen()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
        {
            ausdruck.append("-");
        }
        return ausdruck.toString();
    }


    public String klammerAuf()
    {
        if (gleichGedrueckt)
        {
            ausdruck.setLength(0);
            gleichGedrueckt = false;
        }

        ausdruck.append("(");
        return ausdruck.toString();
    }

    public String klammerZu()
    {
        ausdruck.append(")");
        return ausdruck.toString();
    }


    public String loeschen()
    {
        if (!ausdruck.isEmpty())
        {
            ausdruck.deleteCharAt(ausdruck.length() - 1);
        }

        if (ausdruck.isEmpty())
            return "0";

        return ausdruck.toString();
    }


    public String ce()
    {
        if (gleichGedrueckt)
        {
            allesLoeschen();
        } else
        {
            verlauf.setLength(0);
        }
        return "0";
    }

    public String allesLoeschen()
    {
        verlauf.setLength(0);
        return "0";
    }

    public String operatorSetzen(String op)
    {
        if (gleichGedrueckt)
            gleichGedrueckt = false;

        switch (op)
        {
            case "×": ausdruck.append("*"); break;
            case "÷": ausdruck.append("/"); break;
            default:  ausdruck.append(op);
        }

        return ausdruck.toString();
    }


    public String berechne()
    {
        try
        {
            double result = TaschenrechnerParser.auswerten(ausdruck.toString());
            ausdruck.setLength(0);
            ausdruck.append(formatDouble(result).replace(".", ""));
            gleichGedrueckt = true;
            return formatDouble(result);
        }
        catch (Exception e)
        {
            ausdruck.setLength(0);
            return "Fehler";
        }
    }


    public String prozent()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble() / 100.0;

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }


    public String quadriere()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();
        wert = wert * wert;

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }


    public String wurzel()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert < 0)
            return fehler();

        wert = Math.sqrt(wert);

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }


    public String reziprok()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert == 0)
            return fehler();

        wert = 1.0 / wert;

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }


    public String getVerlauf()
    {
        return verlauf.toString();
    }

    public String formatDouble(double d)
    {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.GERMANY);
        sym.setDecimalSeparator(',');
        sym.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,###.###########", sym);
        String num = df.format(d);

        if (num.contains(",") && num.endsWith(",0"))
        {
            num = num.substring(0, num.indexOf(","));
        }

        return num;
    }

    public double parseEingabe()
    {
        String text = ausdruck.toString().replace(',', '.');
        try
        {
            return Double.parseDouble(text);
        } catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private String formatEingabeLive(String raw)
    {
        if (raw.isEmpty() || raw.contains("-"))
        {
            return raw;
        }

        boolean negativ = raw.startsWith("-");
        if (negativ)
        {
            raw = raw.substring(1);
        }

        String ganz = raw;
        String dezimal = "";

        if (raw.contains(","))
        {
            String[] parts = raw.split(",", 2);
            ganz = parts[0];
            dezimal = "," + parts[1];
        }

        ganz = ganz.replaceAll("\\.", "");
        ganz = ganz.replaceAll("(\\d)(?=(\\{3})+§)", "$1.");

        return (negativ ? "-" : "") + ganz + dezimal;
    }

    private String fehler()
    {
        return "Hier ist ein Fehler!";
    }

    public void formatter(double wert)
    {
        ausdruck.append(formatDouble(wert).replace(".", ""));
    }

    private boolean endetMitOperator()
    {
        if (ausdruck.isEmpty()) return true;
        char c = ausdruck.charAt(ausdruck.length() - 1);
        return "+-*/(".indexOf(c) >= 0;
    }

    private int startLetzteZahl()
    {
        int i = ausdruck.length() - 1;
        while (i >= 0 && (Character.isDigit(ausdruck.charAt(i)) || ausdruck.charAt(i) == ',' || ausdruck.charAt(i) == '.'))
        {
            i--;
        }
        return i + 1;
    }

    private double letzteZahlAlsDouble()
    {
        int start = startLetzteZahl();
        String zahl = ausdruck.substring(start).replace(',', '.');
        return Double.parseDouble(zahl);
    }


}
