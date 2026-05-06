package common.logic;

import common.formatting.ZahlenFormatter;
import common.state.RechnerZustand;

import java.math.BigInteger;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;

public class AusdruckEditor
{
    private static final String FEHLER_TEXT = "Fehler";

    private final RechnerZustand zustand;
    private final ZahlenFormatter zahlenFormatierer;
    private final StringBuilder ausdruck;
    private final StringBuilder verlauf;

    public AusdruckEditor(RechnerZustand zustand, ZahlenFormatter zahlenFormatierer)
    {
        this.zustand = zustand;
        this.zahlenFormatierer = zahlenFormatierer;
        this.ausdruck = zustand.getAusdruck();
        this.verlauf = zustand.getVerlauf();
    }

    public String eingabeZahl(String ziffer)
    {
        resetNachGleichWennNoetig();
        ausdruck.append(ziffer);
        return ausdruck.toString();
    }

    public String eingabeKomma()
    {
        resetNachGleichWennNoetig();

        int start = startLetzteZahl();
        if (ausdruck.length() > 0 && start < ausdruck.length() && ausdruck.substring(start).contains(","))
        {
            return ausdruck.toString();
        }

        if (ausdruck.isEmpty() || endetMitOperatorOderKlammerAuf())
        {
            ausdruck.append("0");
        }

        ausdruck.append(",");
        return ausdruck.toString();
    }

    public String wechselVorzeichen()
    {
        if (zustand.isGleichGedrueckt())
        {
            zustand.setGleichGedrueckt(false);
        }

        if (ausdruck.isEmpty() || endetMitOperatorOderKlammerAuf() || letztesZeichen() == '(')
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
        resetNachGleichWennNoetig();

        if (ausdruck.length() > 0)
        {
            char letztesZeichen = letztesZeichen();
            if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
            {
                ausdruck.append("*");
            }
        }

        ausdruck.append("(");
        return ausdruck.toString();
    }

    public String klammerZu()
    {
        if (!kannKlammerSchliessen())
        {
            return ausdruck.isEmpty() ? "0" : ausdruck.toString();
        }

        ausdruck.append(")");
        return ausdruck.toString();
    }

    public String loeschen()
    {
        if (ausdruck.length() > 0)
        {
            ausdruck.deleteCharAt(ausdruck.length() - 1);
        }
        return ausdruck.isEmpty() ? "0" : ausdruck.toString();
    }

    public String ce()
    {
        if (zustand.isGleichGedrueckt())
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
        zustand.setGleichGedrueckt(false);
        return "0";
    }

    public String operatorSetzen(String operator)
    {
        operator = normalisiereOperator(operator);

        if (zustand.isGleichGedrueckt())
        {
            zustand.setGleichGedrueckt(false);
        }

        if ("-".equals(operator))
        {
            if (ausdruck.isEmpty())
            {
                ausdruck.append("-");
                return ausdruck.toString();
            }

            char letztesZeichen = letztesZeichen();
            if (istOperatorZeichen(letztesZeichen) || letztesZeichen == '(')
            {
                ausdruck.append("-");
                return ausdruck.toString();
            }
        }

        if (ausdruck.isEmpty()) return ausdruck.toString();
        if (endetMitOperatorOderKlammerAuf()) return ausdruck.toString();

        ausdruck.append(operator);
        return ausdruck.toString();
    }

    public String potenz()
    {
        if (ausdruck.isEmpty() || endetMitOperatorOderKlammerAuf()) return ausdruck.toString();

        zustand.setGleichGedrueckt(false);
        ausdruck.append("^");
        return ausdruck.toString();
    }

    public String prozent()
    {
        return wendeAufLetzteZahlAn(x -> x / 100.0, x -> true);
    }

    public String quadriere()
    {
        return letztenTermEinrahmen("(", ")^2");
    }

    public String wurzel()
    {
        return funktionEinfuegenOderUmklammern("sqrt");
    }

    public String reziprok()
    {
        return letztenTermEinrahmen("1/(", ")");
    }

    public String ans()
    {
        return konstanteEinsetzen(zustand.getLetzteAntwort());
    }

    public void setzeAusdruckAusVerlaufErgebnis(String verlaufErgebnis)
    {
        if (verlaufErgebnis == null) return;

        String text = verlaufErgebnis.trim();
        text = text.replace(" ", "");
        text = text.replace(".", "");
        text = text.replace('−', '-').replace('–', '-').replace('—', '-');

        ausdruck.setLength(0);
        ausdruck.append(text);
        zustand.setGleichGedrueckt(false);
    }

    public void setzeAusdruckAusZwischenablage(String text)
    {
        if (text == null) return;

        String normalisiert = text.trim()
                .replaceAll("\\s+", "")
                .replace('×', '*')
                .replace('÷', '/')
                .replace('−', '-')
                .replace('–', '-')
                .replace('—', '-');

        if (normalisiert.isBlank()) return;

        ausdruck.setLength(0);
        ausdruck.append(normalisiert);
        verlauf.setLength(0);
        zustand.setGleichGedrueckt(false);
    }

    public String konstanteEinsetzen(double wert)
    {
        resetNachGleichWennNoetig();

        if (ausdruck.length() > 0)
        {
            char letztesZeichen = letztesZeichen();
            if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
            {
                ausdruck.append("*");
            }
        }

        ausdruck.append(interneDarstellung(wert));
        return ausdruck.toString();
    }

    public String funktionEinfuegenOderUmklammern(String funktionsName)
    {
        resetNachGleichWennNoetig();

        if (ausdruck.isEmpty() || endetMitOperatorOderKlammerAuf())
        {
            ausdruck.append(funktionsName).append("(");
            zustand.setGleichGedrueckt(false);
            return ausdruck.toString();
        }

        int start = startLetzterTerm();

        if (start < 0 || start >= ausdruck.length())
        {
            ausdruck.append(funktionsName).append("(");
            zustand.setGleichGedrueckt(false);
            return ausdruck.toString();
        }

        String term = ausdruck.substring(start);
        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(funktionsName).append("(").append(term).append(")");

        zustand.setGleichGedrueckt(false);
        return ausdruck.toString();
    }

    public String funktionOhneArgumenteEinfuegen(String funktionsName)
    {
        resetNachGleichWennNoetig();

        if (ausdruck.length() > 0)
        {
            char letztesZeichen = letztesZeichen();
            if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
            {
                ausdruck.append("*");
            }
        }

        ausdruck.append(funktionsName).append("()");
        zustand.setGleichGedrueckt(false);
        return ausdruck.toString();
    }

    public String praefixOperatorEinfuegenOderUmklammern(String praefix)
    {
        resetNachGleichWennNoetig();

        if (ausdruck.isEmpty() || endetMitOperatorOderKlammerAuf())
        {
            ausdruck.append(praefix);
            zustand.setGleichGedrueckt(false);
            return ausdruck.toString();
        }

        int start = startLetzterTerm();
        if (start < 0 || start >= ausdruck.length())
        {
            ausdruck.append(praefix);
            zustand.setGleichGedrueckt(false);
            return ausdruck.toString();
        }

        String term = ausdruck.substring(start);
        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(praefix).append(term).append(")");

        zustand.setGleichGedrueckt(false);
        return ausdruck.toString();
    }

    public String fakultaet()
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();

        int start = startLetzteZahl();
        double wert = letzteZahlAlsDouble();

        if (wert < 0 || wert != Math.floor(wert)) return fehler();

        int n = (int) wert;
        if (n > 5000) return fehler();

        BigInteger ergebnis = BigInteger.ONE;
        for (int i = 2; i <= n; i++)
        {
            ergebnis = ergebnis.multiply(BigInteger.valueOf(i));
        }

        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(ergebnis);
        zustand.setGleichGedrueckt(false);
        return ergebnis.toString();
    }

    public boolean endetMitOperatorOderKlammerAuf()
    {
        if (ausdruck.isEmpty()) return true;

        char zeichen = letztesZeichen();
        return istOperatorZeichen(zeichen) || zeichen == '(';
    }

    private String letztenTermEinrahmen(String praefix, String suffix)
    {
        resetNachGleichWennNoetig();

        if (ausdruck.isEmpty() || endetMitOperatorOderKlammerAuf())
        {
            return ausdruck.toString();
        }

        int start = startLetzterTerm();
        if (start < 0 || start >= ausdruck.length())
        {
            return ausdruck.toString();
        }

        String term = ausdruck.substring(start);
        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(praefix).append(term).append(suffix);

        zustand.setGleichGedrueckt(false);
        return ausdruck.toString();
    }

    private int startLetzterTerm()
    {
        int i = ausdruck.length() - 1;
        if (i < 0) return 0;

        char letztesZeichen = ausdruck.charAt(i);

        if (letztesZeichen == ')')
        {
            int klammerStand = 1;
            i--;

            while (i >= 0 && klammerStand > 0)
            {
                char zeichen = ausdruck.charAt(i);

                if (zeichen == ')') klammerStand++;
                else if (zeichen == '(') klammerStand--;

                i--;
            }

            if (klammerStand != 0) return 0;

            while (i >= 0 && Character.isLetter(ausdruck.charAt(i)))
            {
                i--;
            }

            return bezieheUnaeresMinusEin(i + 1);
        }

        if (Character.isDigit(letztesZeichen) || letztesZeichen == ',' || letztesZeichen == '.')
        {
            while (i >= 0)
            {
                char zeichen = ausdruck.charAt(i);

                if (Character.isDigit(zeichen) || zeichen == ',' || zeichen == '.')
                {
                    i--;
                } else
                {
                    break;
                }
            }

            return bezieheUnaeresMinusEin(i + 1);
        }

        if (Character.isLetter(letztesZeichen))
        {
            while (i >= 0 && Character.isLetter(ausdruck.charAt(i)))
            {
                i--;
            }

            return bezieheUnaeresMinusEin(i + 1);
        }

        return ausdruck.length();
    }

    private int bezieheUnaeresMinusEin(int start)
    {
        if (start > 0 && ausdruck.charAt(start - 1) == '-')
        {
            if (start - 1 == 0) return start - 1;

            char davor = ausdruck.charAt(start - 2);
            if (istOperatorZeichen(davor) || davor == '(')
            {
                return start - 1;
            }
        }

        return start;
    }

    private void resetNachGleichWennNoetig()
    {
        if (!zustand.isGleichGedrueckt()) return;

        ausdruck.setLength(0);
        zustand.setGleichGedrueckt(false);
    }

    private String wendeAufLetzteZahlAn(DoubleUnaryOperator operation, DoublePredicate eingabeGueltig)
    {
        if (!kannLetzteZahlBearbeiten()) return ausdruck.toString();

        int start = startLetzteZahl();
        double eingabe = letzteZahlAlsDouble();

        if (!eingabeGueltig.test(eingabe)) return fehler();

        double ergebnis = operation.applyAsDouble(eingabe);
        if (!Double.isFinite(ergebnis)) return fehler();

        ersetzeLetzteZahl(start, ergebnis);
        return formatiereZahl(ergebnis);
    }

    private boolean kannLetzteZahlBearbeiten()
    {
        if (ausdruck.isEmpty()) return false;
        if (endetMitOperatorOderKlammerAuf()) return false;
        return letztesZeichen() != ')';
    }

    private boolean kannKlammerSchliessen()
    {
        if (ausdruck.isEmpty()) return false;
        if (zaehleOffeneKlammern() <= 0) return false;

        char zeichen = letztesZeichen();
        return !istOperatorZeichen(zeichen) && zeichen != '(';
    }

    private int zaehleOffeneKlammern()
    {
        int stand = 0;

        for (int i = 0; i < ausdruck.length(); i++)
        {
            char zeichen = ausdruck.charAt(i);
            if (zeichen == '(') stand++;
            else if (zeichen == ')') stand--;
        }

        return stand;
    }

    private char letztesZeichen()
    {
        return ausdruck.charAt(ausdruck.length() - 1);
    }

    private boolean istOperatorZeichen(char zeichen)
    {
        return "+-*/^%".indexOf(zeichen) >= 0;
    }

    private int startLetzteZahl()
    {
        int i = ausdruck.length() - 1;

        while (i >= 0)
        {
            char zeichen = ausdruck.charAt(i);
            if (Character.isDigit(zeichen) || zeichen == ',' || zeichen == '.') i--;
            else break;
        }

        if (i >= 0 && ausdruck.charAt(i) == '-')
        {
            if (i == 0) return 0;

            char davor = ausdruck.charAt(i - 1);
            if (istOperatorZeichen(davor) || davor == '(')
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

    private String interneDarstellung(double wert)
    {
        return zahlenFormatierer.interneDarstellung(wert);
    }

    private String formatiereZahl(double wert)
    {
        return zahlenFormatierer.formatiereZahl(wert);
    }

    private void ersetzeLetzteZahl(int start, double wert)
    {
        ausdruck.delete(start, ausdruck.length());
        ausdruck.append(interneDarstellung(wert));
        zustand.setGleichGedrueckt(false);
    }

    private String normalisiereOperator(String operator)
    {
        if ("×".equals(operator)) return "*";
        if ("÷".equals(operator)) return "/";
        return operator;
    }

    private String fehler()
    {
        ausdruck.setLength(0);
        verlauf.setLength(0);
        zustand.setGleichGedrueckt(true);
        return FEHLER_TEXT;
    }
}
