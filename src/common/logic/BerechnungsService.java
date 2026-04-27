package common.logic;

import common.formatting.ZahlenFormatter;
import common.parser.AusdruckParser;
import common.state.RechnerZustand;

public class BerechnungsService
{
    private static final String FEHLER_TEXT = "Fehler";

    private final RechnerZustand zustand;
    private final ZahlenFormatter zahlenFormatierer;
    private final StringBuilder ausdruck;
    private final StringBuilder verlauf;

    public BerechnungsService(RechnerZustand zustand, ZahlenFormatter zahlenFormatierer)
    {
        this.zustand = zustand;
        this.zahlenFormatierer = zahlenFormatierer;
        this.ausdruck = zustand.getAusdruck();
        this.verlauf = zustand.getVerlauf();
    }

    public String berechne()
    {
        try
        {
            String original = ausdruck.toString();
            double ergebnis = AusdruckParser.auswerten(original, zustand.getLetzteAntwort(), zustand.getWinkelModus());

            if (!Double.isFinite(ergebnis)) return fehler();

            zustand.setLetzteAntwort(ergebnis);

            ausdruck.setLength(0);
            ausdruck.append(zahlenFormatierer.interneDarstellung(ergebnis));

            zustand.setGleichGedrueckt(true);

            verlauf.setLength(0);
            verlauf.append(original).append(" = ").append(zahlenFormatierer.formatiereZahl(ergebnis));

            return zahlenFormatierer.formatiereZahl(ergebnis);
        }
        catch (Exception e)
        {
            return fehler();
        }
    }

    public double aktuellerWertOder0()
    {
        if (ausdruck.isEmpty()) return 0.0;

        try
        {
            if (endetMitOperatorOderKlammerAuf()) return 0.0;

            double wert = AusdruckParser.auswerten(ausdruck.toString(), zustand.getLetzteAntwort(), zustand.getWinkelModus());
            return Double.isFinite(wert) ? wert : 0.0;
        }
        catch (Exception e)
        {
            return 0.0;
        }
    }

    private boolean endetMitOperatorOderKlammerAuf()
    {
        if (ausdruck.isEmpty()) return true;
        char zeichen = ausdruck.charAt(ausdruck.length() - 1);
        return "+-*/^%".indexOf(zeichen) >= 0 || zeichen == '(';
    }

    private String fehler()
    {
        ausdruck.setLength(0);
        verlauf.setLength(0);
        zustand.setGleichGedrueckt(true);
        return FEHLER_TEXT;
    }
}
