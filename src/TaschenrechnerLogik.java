import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TaschenrechnerLogik
{
    private double aktuellerWert = 0;
    private String aktuellerOperator = "";

    private double zweiterWert;
    private String letzterOperator = "";
    private double letzerOperand = 0;

    private boolean neueEingabe = true;
    private boolean gleichGedrueckt = false;

    private StringBuilder eingabe = new StringBuilder();
    private StringBuilder verlauf = new StringBuilder();
    private double verlaufErsterWert;

    public String eingabeZahl(String ziffer)
    {
        if (gleichGedrueckt)
        {
            verlauf.setLength(0);
            gleichGedrueckt = false;
        }
        if (neueEingabe)
        {
            eingabe.setLength(0);
            neueEingabe = false;
        }
        eingabe.append(ziffer);
        return formatEingabeLive(eingabe.toString());
    }

    public String eingabeKomma()
    {
        if (gleichGedrueckt)
        {
            verlauf.setLength(0);
            gleichGedrueckt = false;

        }
        if (neueEingabe)
        {
            eingabe.setLength(0);
            eingabe.append("0");
            neueEingabe = false;
        }

        if (!eingabe.toString().contains(","))
        {
            eingabe.append(",");
        }

        return formatEingabeLive(eingabe.toString());
    }

    public String wechselVorzeichen()
    {
        if (eingabe.isEmpty())
        {
            eingabe.append("-");
            neueEingabe = false;
            return "-0";
        }

        if (eingabe.charAt(0) == '-')
        {
            eingabe.deleteCharAt(0);
        } else
        {
            eingabe.insert(0, '-');
        }
        return formatEingabeLive(eingabe.toString());
    }


    public String loeschen()
    {
        if (!eingabe.isEmpty())
        {
            eingabe.deleteCharAt(eingabe.length() - 1);
        }

        if (eingabe.isEmpty())
        {
            neueEingabe = true;
            return "0";
        }

        return formatEingabeLive(eingabe.toString());
    }

    public String ce()
    {
        if (gleichGedrueckt)
        {
            allesLoeschen();
        } else
        {
            verlauf.setLength(0);
            neueEingabe = true;
        }
        return "0";
    }

    public String allesLoeschen()
    {
        eingabe.setLength(0);
        aktuellerOperator = "";
        letzterOperator = "";
        letzerOperand = 0;
        aktuellerWert = 0;
        neueEingabe = true;
        verlauf.setLength(0);
        return "0";
    }

    public String operatorSetzen(String op)
    {
        if (!aktuellerOperator.isEmpty() && !neueEingabe)
        {
            berechne();
        } else
        {
            aktuellerWert = parseEingabe();
        }
        aktuellerOperator = op;
        neueEingabe = true;

        verlaufErsterWert = aktuellerWert;

        verlauf.setLength(0);
        verlauf.append(formatDouble(verlaufErsterWert)).append(" ").append(op);

        return formatDouble(aktuellerWert);
    }

    public String berechne()
    {
        if (aktuellerOperator.isEmpty())
        {
            gleichGedrueckt = true;
            neueEingabe = true;
            return formatDouble(!eingabe.isEmpty() ? parseEingabe() : aktuellerWert);
        }

        if (!neueEingabe)
        {
            zweiterWert = parseEingabe();
            letzerOperand = zweiterWert;
            letzterOperator = "";
        } else
        {
            zweiterWert = letzerOperand;
            aktuellerOperator = letzterOperator;
        }

        switch (aktuellerOperator)
        {
            case "+":
                aktuellerWert += zweiterWert;
                gleichGedrueckt = true;
                break;
            case "-":
                aktuellerWert -= zweiterWert;
                gleichGedrueckt = true;
                break;
            case "*":
                aktuellerWert *= zweiterWert;
                gleichGedrueckt = true;
                break;
            case "/":
                if (zweiterWert == 0)
                {
                    allesLoeschen();
                    return fehler();
                }
                aktuellerWert /= zweiterWert;
                gleichGedrueckt = true;
                break;
            default:
                throw new IllegalArgumentException("Unbekannter Operator: " + aktuellerOperator);
        }

        verlauf.setLength(0);
        verlauf.append(formatDouble(verlaufErsterWert)).append(" ").append(aktuellerOperator).append(" ").append(formatDouble(zweiterWert)).append(" ").append("=");

        neueEingabe = true;

        eingabe.setLength(0);
        eingabe.append(formatDouble(aktuellerWert).replace(".", "").replace(',', ','));

        return formatDouble(aktuellerWert);
    }

    public String prozent()
    {
        zweiterWert = parseEingabe();

        if (!aktuellerOperator.isEmpty())
        {
            zweiterWert = aktuellerWert * zweiterWert / 100;
        } else
        {
            zweiterWert = zweiterWert / 100;
        }

        eingabe.setLength(0);
        formatter(zweiterWert);
        neueEingabe = false;
        return formatEingabeLive(eingabe.toString());
    }


    public String quadriere()
    {
        zweiterWert = parseEingabe();
        zweiterWert *= zweiterWert;
        eingabe.setLength(0);
        formatter(zweiterWert);
        neueEingabe = false;
        return formatDouble(zweiterWert);
    }

    public String wurzel()
    {
        zweiterWert = parseEingabe();
        zweiterWert = Math.sqrt(zweiterWert);
        eingabe.setLength(0);
        formatter(zweiterWert);
        neueEingabe = false;
        return formatDouble(zweiterWert);
    }

    public String reziprok()
    {
        zweiterWert = parseEingabe();
        if (zweiterWert == 0)
        {
            return fehler();
        }

        zweiterWert = 1 / zweiterWert;
        eingabe.setLength(0);
        formatter(zweiterWert);
        neueEingabe = false;
        return formatDouble(zweiterWert);
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
        String text = eingabe.toString().replace(',', '.');
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
        eingabe.append(formatDouble(wert).replace(".", "").replace(',', ','));
    }
}
