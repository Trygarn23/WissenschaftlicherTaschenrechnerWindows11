package common.logic;

import common.formatting.ZahlenFormatter;
import common.state.RechnerZustand;

/**
 * Bearbeitet den Ausdruck im Display. Einfache Eingaben (Ziffern, Operatoren, Klammern, Löschen,
 * Rückgängig) stehen hier; Cursor, Term-Einrahmung und Zahl-Umrechnungen liegen in eigenen Helfern.
 */
public class AusdruckEditor
{
    private final RechnerZustand zustand;
    private final ZahlenFormatter zahlenFormatierer;
    private final AusdruckCursor cursor;
    private final AusdruckTermRahmen termRahmen;
    private final LetzteZahlBearbeitung letzteZahl;

    /** Ausdruck vor dem letzten C/CE, für einmal Rückgängig. */
    private String vorDemLoeschen;

    public AusdruckEditor(RechnerZustand zustand, ZahlenFormatter zahlenFormatierer)
    {
        this.zustand = zustand;
        this.zahlenFormatierer = zahlenFormatierer;
        this.cursor = new AusdruckCursor(zustand);
        this.termRahmen = new AusdruckTermRahmen(zustand);
        this.letzteZahl = new LetzteZahlBearbeitung(zustand, zahlenFormatierer);
    }

    public String eingabeZahl(String ziffer)
    {
        resetNachGleichWennNoetig(zustand);
        zustand.appendAusdruck(ziffer);
        return zustand.getAusdruckText();
    }

    public String eingabeKomma()
    {
        resetNachGleichWennNoetig(zustand);

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

        if (zustand.isAusdruckLeer() || endetMitOperatorOderKlammerAuf() || letztesZeichen(zustand) == '(')
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
        resetNachGleichWennNoetig(zustand);
        malVorNeuemWert();
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

        merkeFuerRueckgaengig();
        zustand.clearAusdruck();
        return "0";
    }

    public String allesLoeschen()
    {
        merkeFuerRueckgaengig();
        zustand.clearVerlauf();
        zustand.clearAusdruck();
        zustand.setGleichGedrueckt(false);
        return "0";
    }

    /**
     * Holt den zuletzt mit C/CE gelöschten Ausdruck zurück. Geht nur, solange das Display noch leer ist –
     * sobald wieder etwas getippt wurde, würde Rückgängig sonst die neue Eingabe überschreiben.
     */
    public String rueckgaengig()
    {
        if (kannRueckgaengig())
        {
            zustand.setAusdruckText(vorDemLoeschen);
            zustand.setGleichGedrueckt(false);
            vorDemLoeschen = null;
        }
        return zustand.isAusdruckLeer() ? "0" : zustand.getAusdruckText();
    }

    public boolean kannRueckgaengig()
    {
        return vorDemLoeschen != null && zustand.isAusdruckLeer();
    }

    /** Ein leeres Display wird nicht gemerkt, sonst würde zweimal C den eigentlichen Ausdruck wegwerfen. */
    private void merkeFuerRueckgaengig()
    {
        if (!zustand.isAusdruckLeer())
        {
            vorDemLoeschen = zustand.getAusdruckText();
        }
    }

    public int offeneKlammern()
    {
        return Math.max(0, AusdruckTermFinder.zaehleOffeneKlammern(zustand.getAusdruckText()));
    }

    public int getCursor()
    {
        return cursor.getCursor();
    }

    public void setzeCursor(int index)
    {
        cursor.setzeCursor(index);
    }

    public void bewegeCursor(int schritte)
    {
        cursor.bewegeCursor(schritte);
    }

    /** Führt eine Eingabe an der Cursorstelle aus, siehe {@link AusdruckCursor#amCursor}. */
    public void amCursor(Runnable aktion)
    {
        cursor.amCursor(aktion);
    }

    public void loescheRechts()
    {
        cursor.loescheRechts();
    }

    /** Fügt z. B. eine Konstante an der Cursorstelle ein; steht davor schon eine Zahl, kommt ein Malzeichen dazwischen. */
    public void fuegeEin(String text)
    {
        // Steht rechts vom Cursor schon ein Wert, braucht es auch dahinter ein ×, sonst verschmelzen die Ziffern.
        boolean malDanach = !zustand.isCursorAmEnde() && !zustand.isGleichGedrueckt()
                && zustand.getCursor() < zustand.getAusdruckLaenge()
                && istWertAnfang(zustand.getAusdruckZeichen(zustand.getCursor()));
        amCursor(() -> {
            String ausdruck = zustand.getAusdruckText();
            boolean malNoetig = !ausdruck.isEmpty() && (Character.isLetterOrDigit(ausdruck.charAt(ausdruck.length() - 1))
                    || ausdruck.endsWith(")") || ausdruck.endsWith("π"));
            zustand.appendAusdruck((malNoetig ? "×" : "") + text + (malDanach ? "×" : ""));
            zustand.setGleichGedrueckt(false);
        });
    }

    private static boolean istWertAnfang(char zeichen)
    {
        return Character.isLetterOrDigit(zeichen) || zeichen == '(' || zeichen == ',' || zeichen == 'π';
    }

    public String operatorSetzen(String operator)
    {
        operator = AusdruckNormalisierung.operator(operator);

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

            char letztesZeichen = letztesZeichen(zustand);
            if (AusdruckTermFinder.istOperatorZeichen(letztesZeichen) || letztesZeichen == '(' || letztesZeichen == ';')
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
        return letzteZahl.prozent();
    }

    public String fakultaet()
    {
        return letzteZahl.fakultaet();
    }

    public String quadriere()
    {
        return termRahmen.letztenTermEinrahmen("(", ")^2");
    }

    public String wurzel()
    {
        return funktionEinfuegenOderUmklammern("sqrt");
    }

    public String reziprok()
    {
        return termRahmen.letztenTermEinrahmen("1/(", ")");
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
        resetNachGleichWennNoetig(zustand);
        malVorNeuemWert();
        zustand.appendAusdruck(zahlenFormatierer.interneDarstellung(wert));
        return zustand.getAusdruckText();
    }

    public String funktionEinfuegenOderUmklammern(String funktionsName)
    {
        return termRahmen.funktionEinfuegenOderUmklammern(funktionsName);
    }

    public String funktionOhneArgumenteEinfuegen(String funktionsName)
    {
        resetNachGleichWennNoetig(zustand);
        malVorNeuemWert();
        zustand.appendAusdruck(funktionsName);
        zustand.appendAusdruck("()");
        zustand.setGleichGedrueckt(false);
        return zustand.getAusdruckText();
    }

    public String praefixOperatorEinfuegenOderUmklammern(String praefix)
    {
        return termRahmen.praefixOperatorEinfuegenOderUmklammern(praefix);
    }

    /** Für Funktionen mit zwei Argumenten wie nCr(n;k), siehe {@link AusdruckTermRahmen#zweiArgumentFunktion}. */
    public String zweiArgumentFunktion(String funktionsName)
    {
        return termRahmen.zweiArgumentFunktion(funktionsName);
    }

    public boolean endetMitOperatorOderKlammerAuf()
    {
        return endetMitOperatorOderKlammerAuf(zustand);
    }

    /** Trennt Funktionsargumente; nur innerhalb einer offenen Klammer und nach einem Wert sinnvoll. */
    public String trennzeichen()
    {
        if (zustand.isGleichGedrueckt() || endetMitOperatorOderKlammerAuf() || offeneKlammern() == 0)
        {
            return zustand.getAusdruckText();
        }
        zustand.appendAusdruck(";");
        return zustand.getAusdruckText();
    }

    /** Direkt nach einem Wert (z. B. „2“ vor „(“ oder „π“) wird ein implizites Mal eingefügt. */
    private void malVorNeuemWert()
    {
        if (zustand.isAusdruckLeer()) return;

        char letztesZeichen = letztesZeichen(zustand);
        if (Character.isDigit(letztesZeichen) || letztesZeichen == ')' || letztesZeichen == ',')
        {
            zustand.appendAusdruck("*");
        }
    }

    private boolean kannKlammerSchliessen()
    {
        if (zustand.isAusdruckLeer()) return false;
        if (AusdruckTermFinder.zaehleOffeneKlammern(zustand.getAusdruckText()) <= 0) return false;

        char zeichen = letztesZeichen(zustand);
        return !AusdruckTermFinder.istOperatorZeichen(zeichen) && zeichen != '(';
    }

    private int startLetzteZahl()
    {
        return AusdruckTermFinder.startLetzteZahl(zustand.getAusdruckText());
    }

    // Gemeinsame Prüfungen, auch für die Helfer AusdruckTermRahmen und LetzteZahlBearbeitung.

    static boolean endetMitOperatorOderKlammerAuf(RechnerZustand zustand)
    {
        if (zustand.isAusdruckLeer()) return true;

        char zeichen = letztesZeichen(zustand);
        return AusdruckTermFinder.istOperatorZeichen(zeichen) || zeichen == '(' || zeichen == ';';
    }

    static char letztesZeichen(RechnerZustand zustand)
    {
        return zustand.getAusdruckZeichen(zustand.getAusdruckLaenge() - 1);
    }

    /** Nach „=“ beginnt eine neue Eingabe mit leerem Ausdruck. */
    static void resetNachGleichWennNoetig(RechnerZustand zustand)
    {
        if (!zustand.isGleichGedrueckt()) return;

        zustand.clearAusdruck();
        zustand.setGleichGedrueckt(false);
    }
}
