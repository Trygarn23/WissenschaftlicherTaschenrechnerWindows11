import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TaschenrechnerLogik
{
    private WinkelModus winkelModus = WinkelModus.DEG;
    private boolean gleichGedrueckt = false;

    private final StringBuilder verlauf = new StringBuilder();
    private final StringBuilder ausdruck = new StringBuilder();

    public enum WinkelModus
    {DEG, RAD}

    private double memory = 0.0;
    private double ans = 0.0;

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

        int start = startLetzteZahl();
        if (!ausdruck.isEmpty() && start < ausdruck.length() && ausdruck.substring(start).contains(","))
        {
            return ausdruck.toString();
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
        if (gleichGedrueckt)
        {
            gleichGedrueckt = false;
        }

        // Wenn gerade keine Zahl aktiv ist (z.B. nach Operator), dann starte negative Zahl
        if (ausdruck.isEmpty() || endetMitOperator() || ausdruck.charAt(ausdruck.length() - 1) == '(')
        {
            ausdruck.append("-");
            return ausdruck.toString();
        }

        int start = startLetzteZahl();
        if (start >= ausdruck.length())
        {
            ausdruck.append("-");
            return ausdruck.toString();
        }

        // Wenn Zahl bereits mit unary '-' startet, entfernen, sonst einfügen
        if (ausdruck.charAt(start) == '-')
        {
            ausdruck.deleteCharAt(start);
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

        // implizite Multiplikation: 2(3) => 2*(3)
        if (!ausdruck.isEmpty())
        {
            char last = ausdruck.charAt(ausdruck.length() - 1);
            if (Character.isDigit(last) || last == ')' || last == ',')
            {
                ausdruck.append("*");
            }
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
        return ausdruck.isEmpty() ? "0" : ausdruck.toString();
    }

    public String ce()
    {
        if (gleichGedrueckt)
        {
            return allesLoeschen();
        }
        ausdruck.setLength(0);
        return "0";
    }

    public String allesLoeschen()
    {
        verlauf.setLength(0);
        ausdruck.setLength(0);
        gleichGedrueckt = false;
        return "0";
    }

    public String operatorSetzen(String op)
    {
        // UI-Symbole normalisieren
        if ("×".equals(op)) op = "*";
        if ("÷".equals(op)) op = "/";

        if (gleichGedrueckt)
        {
            gleichGedrueckt = false;
        }

        // '-' kann auch unary sein (z.B. 5*-3 oder 5--3)
        if ("-".equals(op))
        {
            if (ausdruck.isEmpty())
            {
                ausdruck.append('-');
                return ausdruck.toString();
            }
            char last = ausdruck.charAt(ausdruck.length() - 1);
            if (isOperatorChar(last) || last == '(')
            {
                ausdruck.append('-');
                return ausdruck.toString();
            }
        }

        if (ausdruck.isEmpty()) return ausdruck.toString();
        if (endetMitOperator()) return ausdruck.toString();

        ausdruck.append(op);
        return ausdruck.toString();
    }

    public String potenz()
    {
        if (ausdruck.isEmpty() || endetMitOperator()) return ausdruck.toString();
        gleichGedrueckt = false;
        ausdruck.append("^");
        return ausdruck.toString();
    }

    public String berechne()
    {
        try
        {
            String original = ausdruck.toString();
            double result = TaschenrechnerParser.auswerten(
                    original,
                    ans,
                    winkelModus == WinkelModus.DEG
                            ? TaschenrechnerParser.WinkelModus.DEG
                            : TaschenrechnerParser.WinkelModus.RAD
            );


            if (!Double.isFinite(result)) return fehler();

            ans = result;

            ausdruck.setLength(0);
            ausdruck.append(toInternal(result));

            gleichGedrueckt = true;

            verlauf.setLength(0);
            verlauf.append(original).append(" = ").append(formatDouble(result));

            return formatDouble(result);
        } catch (Exception e)
        {
            return fehler();
        }
    }

    public String prozent()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble() / 100.0;
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String quadriere()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();
        wert = wert * wert;
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String wurzel()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();
        if (wert < 0) return fehler();
        wert = Math.sqrt(wert);
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String reziprok()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();
        if (wert == 0) return fehler();
        wert = 1.0 / wert;
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String fakultaet()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert < 0 || wert != Math.floor(wert)) return fehler();

        long n = (long) wert;
        long result = 1;
        for (long i = 2; i <= n; i++) result *= i;

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(result);
        return String.valueOf(result);
    }

    public String zehnHoch()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = Math.pow(10, letzteZahlAlsDouble());
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String ln()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double v = letzteZahlAlsDouble();
        if (v <= 0) return fehler();
        double wert = Math.log(v);
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String log()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double v = letzteZahlAlsDouble();
        if (v <= 0) return fehler();
        double wert = Math.log10(v);
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String sin()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = Math.sin(toRadians(letzteZahlAlsDouble()));
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String cos()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = Math.cos(toRadians(letzteZahlAlsDouble()));
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    public String tan()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();

        double input = letzteZahlAlsDouble();
        double rad = toRadians(input);

        if (Math.abs(Math.cos(rad)) < 1e-12) return fehler();

        double wert = Math.tan(rad);
        if (!Double.isFinite(wert)) return fehler();

        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    // exp(x) = e^x
    public String exp()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = Math.exp(letzteZahlAlsDouble());
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    // |x|
    public String betrag()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();
        double wert = Math.abs(letzteZahlAlsDouble());
        if (!Double.isFinite(wert)) return fehler();
        ersetzeLetzteZahl(start, wert);
        return formatDouble(wert);
    }

    // ========= KONSTANTEN =========

    private String konstanteEinsetzen(double wert)
    {
        if (gleichGedrueckt)
        {
            ausdruck.setLength(0);
            gleichGedrueckt = false;
        }

        // implizite Multiplikation
        if (!ausdruck.isEmpty())
        {
            char last = ausdruck.charAt(ausdruck.length() - 1);
            if (Character.isDigit(last) || last == ')' || last == ',')
            {
                ausdruck.append('*');
            }
        }

        ausdruck.append(toInternal(wert));
        return ausdruck.toString();
    }

    public String pi()
    {
        return konstanteEinsetzen(Math.PI);
    }

    public String e()
    {
        return konstanteEinsetzen(Math.E);
    }

    // ========= WINKELMODUS =========

    public void toggleWinkelModus()
    {
        winkelModus = (winkelModus == WinkelModus.DEG) ? WinkelModus.RAD : WinkelModus.DEG;
    }

    public WinkelModus getWinkelModus()
    {
        return winkelModus;
    }

    // ========= ANZEIGE / FORMAT =========

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

    public String formatLiveAnzeige()
    {
        if (ausdruck.isEmpty()) return "0";

        String raw = ausdruck.toString();

        // Nur formatieren, wenn es wirklich nur eine Zahl ist (evtl. mit Vorzeichen)
        if (!raw.matches("-?[0-9.,]+"))
        {
            return raw;
        }

        boolean negativ = raw.startsWith("-");
        if (negativ) raw = raw.substring(1);

        String ganz = raw;
        String dezimal = "";

        if (raw.contains(","))
        {
            String[] parts = raw.split(",", 2);
            ganz = parts[0];
            dezimal = "," + parts[1];
        }

        ganz = ganz.replace(".", "");
        ganz = ganz.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ".");

        return (negativ ? "-" : "") + ganz + dezimal;
    }

    // Speicherung und so

    public String ans()
    {
        return konstanteEinsetzen(ans);
    }

    public String memoryClear()
    {
        memory = 0.0;
        return "0";
    }

    public String memoryRecall()
    {
        return konstanteEinsetzen(memory);
    }

    public String memoryAdd()
    {
        memory += aktuellerWertOder0();
        return formatDouble(memory);
    }

    public String memorySub()
    {
        memory -= aktuellerWertOder0();
        return formatDouble(memory);
    }

    private double aktuellerWertOder0()
    {
        if (ausdruck.isEmpty()) return 0.0;

        try
        {
            if (endetMitOperator()) return 0.0;
            double v = TaschenrechnerParser.auswerten(
                    ausdruck.toString(),
                    ans,
                    winkelModus == WinkelModus.DEG
                            ? TaschenrechnerParser.WinkelModus.DEG
                            : TaschenrechnerParser.WinkelModus.RAD
            );

            return Double.isFinite(v) ? v : 0.0;
        }
        catch (Exception e)
        {
            return 0.0;
        }
    }



    // ========= INTERN =========

    private boolean kannLetzteZahlBearbeiten()
    {
        if (ausdruck.isEmpty()) return false;
        if (endetMitOperator()) return false;
        // Wenn Ausdruck mit ')' endet, wissen wir nicht sauber, wo die letzte Zahl ist
        if (ausdruck.charAt(ausdruck.length() - 1) == ')') return false;
        return true;
    }

    private boolean endetMitOperator()
    {
        if (ausdruck.isEmpty()) return true;
        char c = ausdruck.charAt(ausdruck.length() - 1);
        return isOperatorChar(c) || c == '(';
    }

    private boolean isOperatorChar(char c)
    {
        return "+-*/^%".indexOf(c) >= 0;
    }

    private int startLetzteZahl()
    {
        int i = ausdruck.length() - 1;

        while (i >= 0)
        {
            char ch = ausdruck.charAt(i);
            if (Character.isDigit(ch) || ch == ',' || ch == '.') i--;
            else break;
        }

        // unary '-' gehört zur Zahl?
        if (i >= 0 && ausdruck.charAt(i) == '-')
        {
            if (i == 0) return 0;

            char before = ausdruck.charAt(i - 1);
            if (isOperatorChar(before) || before == '(')
            {
                return i;
            }
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
        return (winkelModus == WinkelModus.DEG) ? Math.toRadians(wert) : wert;
    }

    private String fehler()
    {
        ausdruck.setLength(0);
        gleichGedrueckt = true;
        return "Fehler";
    }

    private String toInternal(double wert)
    {
        // verhindert wissenschaftliche Notation (E)
        String s = BigDecimal.valueOf(wert).stripTrailingZeros().toPlainString();
        return s.replace('.', ',');
    }

    private void ersetzeLetzteZahl(int start, double wert)
    {
        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(toInternal(wert));
        gleichGedrueckt = false;
    }

    public void setAusdruckVonHistoryResult(String resultDisplay)
    {
        if (resultDisplay == null) return;

        String s = resultDisplay.trim();
        s = s.replace(" ", "");
        s = s.replace(".", "");
        s = s.replace('−', '-').replace('–', '-').replace('—', '-');

        ausdruck.setLength(0);
        ausdruck.append(s);
        gleichGedrueckt = false;
    }

}
