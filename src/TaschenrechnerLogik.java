import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;

public class TaschenrechnerLogik
{
    public enum WinkelModus
    {DEG, RAD}

    private static final String ERROR_TEXT = "Fehler";

    private WinkelModus winkelModus = WinkelModus.DEG;
    private boolean gleichGedrueckt = false;

    private final StringBuilder verlauf = new StringBuilder();
    private final StringBuilder ausdruck = new StringBuilder();

    private double memory = 0.0;
    private double ans = 0.0;

    public String eingabeZahl(String ziffer)
    {
        resetAfterEqualsIfNeeded();
        ausdruck.append(ziffer);
        return ausdruck.toString();
    }

    public String eingabeKomma()
    {
        resetAfterEqualsIfNeeded();

        int start = startLetzteZahl();
        if (ausdruck.length() > 0 && start < ausdruck.length() && ausdruck.substring(start).contains(","))
        {
            return ausdruck.toString();
        }

        if (ausdruck.length() == 0 || endetMitOperatorOderKlammerAuf())
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

        if (ausdruck.length() == 0 || endetMitOperatorOderKlammerAuf() || lastChar() == '(')
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
        resetAfterEqualsIfNeeded();

        if (ausdruck.length() > 0)
        {
            char last = lastChar();
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
        if (ausdruck.length() > 0)
        {
            ausdruck.deleteCharAt(ausdruck.length() - 1);
        }
        return ausdruck.length() == 0 ? "0" : ausdruck.toString();
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
        op = normalizeOperator(op);

        if (gleichGedrueckt)
        {
            gleichGedrueckt = false;
        }

        if ("-".equals(op))
        {
            if (ausdruck.length() == 0)
            {
                ausdruck.append('-');
                return ausdruck.toString();
            }
            char last = lastChar();
            if (isOperatorChar(last) || last == '(')
            {
                ausdruck.append('-');
                return ausdruck.toString();
            }
        }

        if (ausdruck.length() == 0) return ausdruck.toString();
        if (endetMitOperatorOderKlammerAuf()) return ausdruck.toString();

        ausdruck.append(op);
        return ausdruck.toString();
    }

    public String potenz()
    {
        if (ausdruck.length() == 0 || endetMitOperatorOderKlammerAuf()) return ausdruck.toString();
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
        return applyToLastNumber(x -> x / 100.0, x -> true);
    }

    public String quadriere()
    {
        return applyToLastNumber(x -> x * x, x -> true);
    }

    public String wurzel()
    {
        return applyToLastNumber(Math::sqrt, x -> x >= 0);
    }

    public String reziprok()
    {
        return applyToLastNumber(x -> 1.0 / x, x -> x != 0.0);
    }

    public String zehnHoch()
    {
        return applyToLastNumber(x -> Math.pow(10, x), x -> true);
    }

    public String ln()
    {
        return applyToLastNumber(Math::log, x -> x > 0);
    }

    public String log()
    {
        return applyToLastNumber(Math::log10, x -> x > 0);
    }

    public String sin()
    {
        return applyToLastNumber(x -> Math.sin(toRadians(x)), x -> true);
    }

    public String cos()
    {
        return applyToLastNumber(x -> Math.cos(toRadians(x)), x -> true);
    }

    public String exp()
    {
        return applyToLastNumber(Math::exp, x -> true);
    }

    public String betrag()
    {
        return applyToLastNumber(Math::abs, x -> true);
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

    private String konstanteEinsetzen(double wert)
    {
        resetAfterEqualsIfNeeded();

        if (ausdruck.length() > 0)
        {
            char last = lastChar();
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

    public void toggleWinkelModus()
    {
        winkelModus = (winkelModus == WinkelModus.DEG) ? WinkelModus.RAD : WinkelModus.DEG;
    }

    public WinkelModus getWinkelModus()
    {
        return winkelModus;
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

    public String formatLiveAnzeige()
    {
        if (ausdruck.isEmpty()) return "0";

        String raw = ausdruck.toString();

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
            if (endetMitOperatorOderKlammerAuf()) return 0.0;

            double v = TaschenrechnerParser.auswerten(
                    ausdruck.toString(),
                    ans,
                    winkelModus == WinkelModus.DEG
                            ? TaschenrechnerParser.WinkelModus.DEG
                            : TaschenrechnerParser.WinkelModus.RAD
            );

            return Double.isFinite(v) ? v : 0.0;
        } catch (Exception e)
        {
            return 0.0;
        }
    }

    private void resetAfterEqualsIfNeeded()
    {
        if (!gleichGedrueckt) return;
        ausdruck.setLength(0);
        gleichGedrueckt = false;
    }

    private String applyToLastNumber(DoubleUnaryOperator operation, DoublePredicate isValidInput)
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();
        int start = startLetzteZahl();

        double input = letzteZahlAlsDouble();
        if (!isValidInput.test(input)) return fehler();

        double value = operation.applyAsDouble(input);
        if (!Double.isFinite(value)) return fehler();

        ersetzeLetzteZahl(start, value);
        return formatDouble(value);
    }

    private boolean kannLetzteZahlBearbeiten()
    {
        if (ausdruck.isEmpty()) return false;
        if (endetMitOperatorOderKlammerAuf()) return false;
        if (lastChar() == ')') return false;
        return true;
    }

    private boolean endetMitOperatorOderKlammerAuf()
    {
        if (ausdruck.isEmpty()) return true;
        char c = lastChar();
        return isOperatorChar(c) || c == '(';
    }

    private char lastChar()
    {
        return ausdruck.charAt(ausdruck.length() - 1);
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
        verlauf.setLength(0);
        gleichGedrueckt = true;
        return ERROR_TEXT;
    }

    private String toInternal(double wert)
    {
        String s = BigDecimal.valueOf(wert).stripTrailingZeros().toPlainString();
        return s.replace('.', ',');
    }

    private void ersetzeLetzteZahl(int start, double wert)
    {
        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(toInternal(wert));
        gleichGedrueckt = false;
    }

    private String normalizeOperator(String op)
    {
        if ("×".equals(op)) return "*";
        if ("÷".equals(op)) return "/";
        return op;
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
