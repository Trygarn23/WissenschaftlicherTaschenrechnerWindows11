package common.logic;

public class BerechnungsErgebnis
{
    private final boolean erfolgreich;
    private final String anzeigeText;
    private final String verlaufText;
    private final BerechnungsFehler fehler;
    private final String fehlerAusdruck;
    private final int fehlerPosition;

    private BerechnungsErgebnis(boolean erfolgreich, String anzeigeText, String verlaufText, BerechnungsFehler fehler,
                                String fehlerAusdruck, int fehlerPosition)
    {
        this.erfolgreich = erfolgreich;
        this.anzeigeText = anzeigeText;
        this.verlaufText = verlaufText;
        this.fehler = fehler;
        this.fehlerAusdruck = fehlerAusdruck;
        this.fehlerPosition = fehlerPosition;
    }

    public static BerechnungsErgebnis erfolg(String anzeigeText, String verlaufText)
    {
        return new BerechnungsErgebnis(true, anzeigeText, verlaufText, BerechnungsFehler.KEINER, "", -1);
    }

    public static BerechnungsErgebnis fehler(BerechnungsFehler fehler)
    {
        return fehler(fehler, "", -1);
    }

    /** @param position 0-basierte Fehlerstelle im internen Ausdruck oder -1, wenn unbekannt */
    public static BerechnungsErgebnis fehler(BerechnungsFehler fehler, String ausdruck, int position)
    {
        return new BerechnungsErgebnis(false, "Fehler", "", fehler, ausdruck, position);
    }

    public boolean isErfolgreich()
    {
        return erfolgreich;
    }

    public String getAnzeigeText()
    {
        return anzeigeText;
    }

    public String getVerlaufText()
    {
        return verlaufText;
    }

    public BerechnungsFehler getFehler()
    {
        return fehler;
    }

    public String getFehlerMeldung()
    {
        return fehler.getMeldung();
    }

    /** Der Ausdruck, an dem die Berechnung gescheitert ist (leer, wenn die Stelle unbekannt ist). */
    public String getFehlerAusdruck()
    {
        return fehlerAusdruck;
    }

    public int getFehlerPosition()
    {
        return fehlerPosition;
    }

    public boolean hatFehlerPosition()
    {
        return fehlerPosition >= 0 && !fehlerAusdruck.isEmpty();
    }
}
