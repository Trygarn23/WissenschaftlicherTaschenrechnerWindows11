package common.logic;

import common.formatting.ZahlenFormatter;
import common.parser.AusdruckParser;
import common.parser.AusdruckParserException;
import common.parser.ParserFehler;
import common.state.RechnerZustand;

import java.util.OptionalDouble;

public class BerechnungsService
{
    private final RechnerZustand zustand;
    private final ZahlenFormatter zahlenFormatierer;

    public BerechnungsService(RechnerZustand zustand, ZahlenFormatter zahlenFormatierer)
    {
        this.zustand = zustand;
        this.zahlenFormatierer = zahlenFormatierer;
    }

    public String berechne()
    {
        return berechneDetailliert().getAnzeigeText();
    }

    public BerechnungsErgebnis berechneDetailliert()
    {
        try
        {
            String original = zustand.getAusdruckText();
            double ergebnis = AusdruckParser.auswerten(original, zustand.getLetzteAntwort(), zustand.getWinkelModus());

            if (!Double.isFinite(ergebnis)) return fehler(BerechnungsFehler.UNGUELTIGES_ERGEBNIS);

            zustand.setLetzteAntwort(ergebnis);

            String formatiertergebnis = zahlenFormatierer.formatiereZahl(ergebnis);

            zustand.setAusdruckText(zahlenFormatierer.interneDarstellung(ergebnis));

            zustand.setGleichGedrueckt(true);

            zustand.setVerlaufText(original + " = " + formatiertergebnis);

            return BerechnungsErgebnis.erfolg(formatiertergebnis, zustand.getVerlaufText());
        }
        catch (AusdruckParserException e)
        {
            return fehler(mappeParserFehler(e.getFehler()));
        }
        catch (IllegalArgumentException e)
        {
            // z. B. NumberFormatException bei Zahlen wie "1.2.3"
            return fehler(BerechnungsFehler.SYNTAX);
        }
    }

    public double aktuellerWertOder0()
    {
        return probeAuswertung(zustand.getAusdruckText()).orElse(0.0);
    }

    public OptionalDouble vorschauWert()
    {
        String ausdruck = zustand.getAusdruckText();
        if (endetMitOperatorOderKlammerAuf(ausdruck)) return OptionalDouble.empty();

        int offen = Math.max(0, AusdruckTermFinder.zaehleOffeneKlammern(ausdruck));
        return probeAuswertung(ausdruck + ")".repeat(offen));
    }

    private OptionalDouble probeAuswertung(String ausdruck)
    {
        if (endetMitOperatorOderKlammerAuf(ausdruck)) return OptionalDouble.empty();
        
        try
        {
            double wert = AusdruckParser.auswerten(ausdruck, zustand.getLetzteAntwort(), zustand.getWinkelModus());
            return Double.isFinite(wert) ? OptionalDouble.of(wert) : OptionalDouble.empty();
        }
        catch (IllegalArgumentException e)
        {
            return OptionalDouble.empty();
        }
    }

    private static boolean endetMitOperatorOderKlammerAuf(String ausdruck)
    {
        if (ausdruck.isEmpty()) return true;
        char zeichen = ausdruck.charAt(ausdruck.length() - 1);
        return "+-*/^%".indexOf(zeichen) >= 0 || zeichen == '(';
    }

    private BerechnungsErgebnis fehler(BerechnungsFehler fehler)
    {
        zustand.clearAusdruck();
        zustand.clearVerlauf();
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
