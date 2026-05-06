package common.logic;

public class BerechnungsErgebnis
{
    private final boolean erfolgreich;
    private final String anzeigeText;
    private final String verlaufText;
    private final BerechnungsFehler fehler;

    private BerechnungsErgebnis(boolean erfolgreich, String anzeigeText, String verlaufText, BerechnungsFehler fehler)
    {
        this.erfolgreich = erfolgreich;
        this.anzeigeText = anzeigeText;
        this.verlaufText = verlaufText;
        this.fehler = fehler;
    }

    public static BerechnungsErgebnis erfolg(String anzeigeText, String verlaufText)
    {
        return new BerechnungsErgebnis(true, anzeigeText, verlaufText, BerechnungsFehler.KEINER);
    }

    public static BerechnungsErgebnis fehler(BerechnungsFehler fehler)
    {
        return new BerechnungsErgebnis(false, "Fehler", "", fehler);
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
}
