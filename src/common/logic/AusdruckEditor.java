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

    public AusdruckEditor(RechnerZustand zustand, ZahlenFormatter zahlenFormatierer)
    {
        this.zustand = zustand;
        this.zahlenFormatierer = zahlenFormatierer;
    }

    public String eingabeZahl(String ziffer)
    {
        resetNachGleichWennNoetig();
        zustand.appendAusdruck(ziffer);
        return zustand.getAusdruckText();
    }

    public String eingabeKomma()
    {
        resetNachGleichWennNoetig();

        int start = startLetzteZahl();
        if (zustand.getAusdruckLaenge() > 0
                && start < zustand.getAusdruckLaenge()
                && zustand.getAusdruckTeilText(start).contains(","))
        {
            return zustand.getAusdruckText();
        }

        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf())
        {
            zustand.appendAusdruck("0");
        }

        zustand.appendAusdruck(",");
        return zustand.getAusdruckText();
    }

    public String wechselVorzeichen()
    {
        if (zustand.isGleichGedrueckt())
        {
            zustand.setGleichGedrueckt(false);
        }

        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf() || letztesZeichen() == '(')
        {
            zustand.appendAusdruck("-");
            return zustand.getAusdruckText();
        }

        int start = startLetzteZahl();
        if (start >= zustand.getAusdruckLaenge())
        {
            zustand.appendAusdruck("-");
            return zustand.getAusdruckText();
        }

        if (zustand.getAusdruckZeichen(start) == '-')
        {
            zustand.deleteAusdruckZeichen(start);
        } else
        {
            zustand.insertAusdruck(start, "-");
        }

        return zustand.getAusdruckText();
    }

    public String klammerAuf()
    {
        resetNachGleichWennNoetig();

        if (zustand.getAusdruckLaenge() > 0)
        {
            char letztesZeichen = letztesZeichen();
            if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
            {
                zustand.appendAusdruck("*");
            }
        }

        zustand.appendAusdruck("(");
        return zustand.getAusdruckText();
    }

    public String klammerZu()
    {
        if (!kannKlammerSchliessen())
        {
            return zustand.isAusdruckLeer() ? "0" : zustand.getAusdruckText();
        }

        zustand.appendAusdruck(")");
        return zustand.getAusdruckText();
    }

    public String loeschen()
    {
        if (zustand.getAusdruckLaenge() > 0)
        {
            zustand.deleteAusdruckZeichen(zustand.getAusdruckLaenge() - 1);
        }
        return zustand.isAusdruckLeer() ? "0" : zustand.getAusdruckText();
    }

    public String ce()
    {
        if (zustand.isGleichGedrueckt())
        {
            return allesLoeschen();
        }

        zustand.clearAusdruck();
        return "0";
    }

    public String allesLoeschen()
    {
        zustand.clearVerlauf();
        zustand.clearAusdruck();
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
            if (zustand.isAusdruckLeer())
            {
                zustand.appendAusdruck("-");
                return zustand.getAusdruckText();
            }

            char letztesZeichen = letztesZeichen();
            if (istOperatorZeichen(letztesZeichen) || letztesZeichen == '(')
            {
                zustand.appendAusdruck("-");
                return zustand.getAusdruckText();
            }
        }

        if (zustand.isAusdruckLeer()) return zustand.getAusdruckText();
        if (endetMitOperatorOderKlammerAuf()) return zustand.getAusdruckText();

        zustand.appendAusdruck(operator);
        return zustand.getAusdruckText();
    }

    public String potenz()
    {
        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf()) return zustand.getAusdruckText();

        zustand.setGleichGedrueckt(false);
        zustand.appendAusdruck("^");
        return zustand.getAusdruckText();
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

        String text = AusdruckNormalisierung.verlaufErgebnis(verlaufErgebnis);
        zustand.setAusdruckText(text);
        zustand.setGleichGedrueckt(false);
    }

    public void setzeAusdruckAusZwischenablage(String text)
    {
        if (text == null) return;

        String normalisiert = AusdruckNormalisierung.zwischenablage(text);
        if (normalisiert.isBlank()) return;

        zustand.setAusdruckText(normalisiert);
        zustand.clearVerlauf();
        zustand.setGleichGedrueckt(false);
    }


    public String konstanteEinsetzen(double wert)
    {
        resetNachGleichWennNoetig();

        if (zustand.getAusdruckLaenge() > 0)
        {
            char letztesZeichen = letztesZeichen();
            if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
            {
                zustand.appendAusdruck("*");
            }
        }

        zustand.appendAusdruck(interneDarstellung(wert));
        return zustand.getAusdruckText();
    }

    public String funktionEinfuegenOderUmklammern(String funktionsName)
    {
        resetNachGleichWennNoetig();

        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf())
        {
            zustand.appendAusdruck(funktionsName);
            zustand.appendAusdruck("(");
            zustand.setGleichGedrueckt(false);
            return zustand.getAusdruckText();
        }

        int start = startLetzterTerm();

        if (start < 0 || start >= zustand.getAusdruckLaenge())
        {
            zustand.appendAusdruck(funktionsName);
            zustand.appendAusdruck("(");
            zustand.setGleichGedrueckt(false);
            return zustand.getAusdruckText();
        }

        String term = zustand.getAusdruckTeilText(start);
        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(funktionsName);
        zustand.appendAusdruck("(");
        zustand.appendAusdruck(term);
        zustand.appendAusdruck(")");

        zustand.setGleichGedrueckt(false);
        return zustand.getAusdruckText();
    }

    public String funktionOhneArgumenteEinfuegen(String funktionsName)
    {
        resetNachGleichWennNoetig();

        if (zustand.getAusdruckLaenge() > 0)
        {
            char letztesZeichen = letztesZeichen();
            if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
            {
                zustand.appendAusdruck("*");
            }
        }

        zustand.appendAusdruck(funktionsName);
        zustand.appendAusdruck("()");
        zustand.setGleichGedrueckt(false);
        return zustand.getAusdruckText();
    }

    public String praefixOperatorEinfuegenOderUmklammern(String praefix)
    {
        resetNachGleichWennNoetig();

        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf())
        {
            zustand.appendAusdruck(praefix);
            zustand.setGleichGedrueckt(false);
            return zustand.getAusdruckText();
        }

        int start = startLetzterTerm();
        if (start < 0 || start >= zustand.getAusdruckLaenge())
        {
            zustand.appendAusdruck(praefix);
            zustand.setGleichGedrueckt(false);
            return zustand.getAusdruckText();
        }

        String term = zustand.getAusdruckTeilText(start);
        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(praefix);
        zustand.appendAusdruck(term);
        zustand.appendAusdruck(")");

        zustand.setGleichGedrueckt(false);
        return zustand.getAusdruckText();
    }

    public String fakultaet()
    {
        if (!kannLetzteZahlBearbeiten()) return zustand.getAusdruckText();

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

        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(ergebnis.toString());
        zustand.setGleichGedrueckt(false);
        return ergebnis.toString();
    }

    public boolean endetMitOperatorOderKlammerAuf()
    {
        if (zustand.isAusdruckLeer()) return true;

        char zeichen = letztesZeichen();
        return istOperatorZeichen(zeichen) || zeichen == '(';
    }

    private String letztenTermEinrahmen(String praefix, String suffix)
    {
        resetNachGleichWennNoetig();

        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf())
        {
            return zustand.getAusdruckText();
        }

        int start = startLetzterTerm();
        if (start < 0 || start >= zustand.getAusdruckLaenge())
        {
            return zustand.getAusdruckText();
        }

        String term = zustand.getAusdruckTeilText(start);
        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(praefix);
        zustand.appendAusdruck(term);
        zustand.appendAusdruck(suffix);

        zustand.setGleichGedrueckt(false);
        return zustand.getAusdruckText();
    }

    private int startLetzterTerm()
    {
        int i = zustand.getAusdruckLaenge() - 1;
        if (i < 0) return 0;

        char letztesZeichen = zustand.getAusdruckZeichen(i);

        if (letztesZeichen == ')')
        {
            int klammerStand = 1;
            i--;

            while (i >= 0 && klammerStand > 0)
            {
                char zeichen = zustand.getAusdruckZeichen(i);

                if (zeichen == ')') klammerStand++;
                else if (zeichen == '(') klammerStand--;

                i--;
            }

            if (klammerStand != 0) return 0;

            while (i >= 0 && Character.isLetter(zustand.getAusdruckZeichen(i)))
            {
                i--;
            }

            return bezieheUnaeresMinusEin(i + 1);
        }

        if (Character.isDigit(letztesZeichen) || letztesZeichen == ',' || letztesZeichen == '.')
        {
            while (i >= 0)
            {
                char zeichen = zustand.getAusdruckZeichen(i);

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
            while (i >= 0 && Character.isLetter(zustand.getAusdruckZeichen(i)))
            {
                i--;
            }

            return bezieheUnaeresMinusEin(i + 1);
        }

        return zustand.getAusdruckLaenge();
    }

    private int bezieheUnaeresMinusEin(int start)
    {
        if (start > 0 && zustand.getAusdruckZeichen(start - 1) == '-')
        {
            if (start - 1 == 0) return start - 1;

            char davor = zustand.getAusdruckZeichen(start - 2);
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

        zustand.clearAusdruck();
        zustand.setGleichGedrueckt(false);
    }

    private String wendeAufLetzteZahlAn(DoubleUnaryOperator operation, DoublePredicate eingabeGueltig)
    {
        if (!kannLetzteZahlBearbeiten()) return zustand.getAusdruckText();

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
        if (zustand.isAusdruckLeer()) return false;
        if (endetMitOperatorOderKlammerAuf()) return false;
        return letztesZeichen() != ')';
    }

    private boolean kannKlammerSchliessen()
    {
        if (zustand.isAusdruckLeer()) return false;
        if (zaehleOffeneKlammern() <= 0) return false;

        char zeichen = letztesZeichen();
        return !istOperatorZeichen(zeichen) && zeichen != '(';
    }

    private int zaehleOffeneKlammern()
    {
        int stand = 0;

        for (int i = 0; i < zustand.getAusdruckLaenge(); i++)
        {
            char zeichen = zustand.getAusdruckZeichen(i);
            if (zeichen == '(') stand++;
            else if (zeichen == ')') stand--;
        }

        return stand;
    }

    private char letztesZeichen()
    {
        return zustand.getAusdruckZeichen(zustand.getAusdruckLaenge() - 1);
    }

    private boolean istOperatorZeichen(char zeichen)
    {
        return "+-*/^%".indexOf(zeichen) >= 0;
    }

    private int startLetzteZahl()
    {
        int i = zustand.getAusdruckLaenge() - 1;

        while (i >= 0)
        {
            char zeichen = zustand.getAusdruckZeichen(i);
            if (Character.isDigit(zeichen) || zeichen == ',' || zeichen == '.') i--;
            else break;
        }

        if (i >= 0 && zustand.getAusdruckZeichen(i) == '-')
        {
            if (i == 0) return 0;

            char davor = zustand.getAusdruckZeichen(i - 1);
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
        String zahl = zustand.getAusdruckTeilText(start).replace(',', '.');
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
        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(interneDarstellung(wert));
        zustand.setGleichGedrueckt(false);
    }

    private String normalisiereOperator(String operator)
    {
        return AusdruckNormalisierung.operator(operator);
    }

    private String fehler()
    {
        zustand.clearAusdruck();
        zustand.clearVerlauf();
        zustand.setGleichGedrueckt(true);
        return FEHLER_TEXT;
    }
}
