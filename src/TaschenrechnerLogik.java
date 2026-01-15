import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TaschenrechnerLogik
{

    private WinkelModus winkelModus = WinkelModus.DEG;
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
        if (ausdruck.isEmpty())
        {
            ausdruck.append("-");
            return ausdruck.toString();
        }

        int start = startLetzteZahl();

        if (start == ausdruck.length())
            return ausdruck.toString();

        if (start > 0 && ausdruck.charAt(start - 1) == '-')
        {
            ausdruck.deleteCharAt(start - 1);
        } else
        {
            ausdruck.insert(start, '-');
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
            case "×":
                ausdruck.append("*");
                break;
            case "÷":
                ausdruck.append("/");
                break;
            default:
                ausdruck.append(op);
        }

        return ausdruck.toString();
    }


    public String potenz()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        ausdruck.append("^");
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
        } catch (Exception e)
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

    public String fakultaet()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert < 0 || wert != Math.floor(wert))
            return fehler();

        long n = (long) wert;
        long result = 1;

        for (long i = 2; i <= n; i++)
            result *= i;

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(result);

        return String.valueOf(result);
    }

    public String zehnHoch()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        wert = Math.pow(10, wert);

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }


    public String ln()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert <= 0)
            return fehler();

        wert = Math.log(wert);

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }

    public String log()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert <= 0)
            return fehler();

        wert = Math.log10(wert);

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }

    public String sin()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        wert = Math.sin(toRadians(wert));

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }

    public String cos()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        wert = Math.cos(toRadians(wert));

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }

    public String tan()
    {
        if (ausdruck.isEmpty() || endetMitOperator())
            return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        double rad = toRadians(wert);

        if (Math.abs(Math.cos(rad)) < 1e-10)
            return fehler();

        wert = Math.tan(rad);

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(formatDouble(wert).replace(".", ""));

        return formatDouble(wert);
    }

    public void toggleWinkelModus()
    {
        winkelModus = (winkelModus == WinkelModus.DEG)
                ? WinkelModus.RAD
                : WinkelModus.DEG;
    }

    public WinkelModus getWinkelModus()
    {
        return winkelModus;
    }

    public enum WinkelModus
    {
        DEG,
        RAD
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

    private double toRadians(double wert)
    {
        return winkelModus == WinkelModus.DEG
                ? Math.toRadians(wert)
                : wert;
    }

    private String fehler()
    {
        ausdruck.setLength(0);
        gleichGedrueckt = true;
        return "Fehler";
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
}
