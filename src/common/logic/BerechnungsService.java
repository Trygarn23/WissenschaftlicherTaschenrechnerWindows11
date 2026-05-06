package common.logic;

import common.formatting.ZahlenFormatter;
import common.parser.AusdruckParser;
import common.parser.AusdruckParserException;
import common.parser.ParserFehler;
import common.state.RechnerZustand;

public class BerechnungsService
{
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
        return berechneDetailliert().getAnzeigeText();
    }

    public BerechnungsErgebnis berechneDetailliert()
    {
        try
        {
            String original = ausdruck.toString();
            double ergebnis = AusdruckParser.auswerten(original, zustand.getLetzteAntwort(), zustand.getWinkelModus());

            if (!Double.isFinite(ergebnis)) return fehler(BerechnungsFehler.UNGUELTIGES_ERGEBNIS);

            zustand.setLetzteAntwort(ergebnis);

            String formatiertergebnis = zahlenFormatierer.formatiereZahl(ergebnis);

            ausdruck.setLength(0);
            ausdruck.append(zahlenFormatierer.interneDarstellung(ergebnis));

            zustand.setGleichGedrueckt(true);

            verlauf.setLength(0);
            verlauf.append(original).append(" = ").append(formatiertergebnis);

            return BerechnungsErgebnis.erfolg(formatiertergebnis, verlauf.toString());
        }
        catch (AusdruckParserException e)
        {
            return fehler(mappeParserFehler(e.getFehler()));
        }
        catch (Exception e)
        {
            return fehler(BerechnungsFehler.SYNTAX);
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

    private BerechnungsErgebnis fehler(BerechnungsFehler fehler)
    {
        ausdruck.setLength(0);
        verlauf.setLength(0);
        zustand.setGleichGedrueckt(true);
        return BerechnungsErgebnis.fehler(fehler);
    }

    private BerechnungsFehler mappeParserFehler(ParserFehler fehler)
    {
        return switch (fehler)
        {
            case SYNTAX -> BerechnungsFehler.SYNTAX;
            case DIVISION_DURCH_NULL -> BerechnungsFehler.DIVISION_DURCH_NULL;
            case UNGUELTIGER_FUNKTIONSBEREICH -> BerechnungsFehler.UNGUELTIGER_FUNKTIONSBEREICH;
            case UNBEKANNTE_FUNKTION -> BerechnungsFehler.UNBEKANNTE_FUNKTION;
            case KLAMMERN_UNAUSGEGLICHEN -> BerechnungsFehler.KLAMMERN_UNAUSGEGLICHEN;
            case UNGUELTIGES_ERGEBNIS -> BerechnungsFehler.UNGUELTIGES_ERGEBNIS;
        };
    }
}
