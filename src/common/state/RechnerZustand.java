package common.state;

public class RechnerZustand
{
    private WinkelModus winkelModus = WinkelModus.DEG;
    private boolean gleichGedrueckt = false;
    private final StringBuilder verlauf = new StringBuilder();
    private final StringBuilder ausdruck = new StringBuilder();
    private double letzteAntwort = 0.0;

    public WinkelModus getWinkelModus()
    {
        return winkelModus;
    }

    public void setWinkelModus(WinkelModus winkelModus)
    {
        this.winkelModus = winkelModus;
    }

    public void winkelModusUmschalten()
    {
        winkelModus = (winkelModus == WinkelModus.DEG) ? WinkelModus.RAD : WinkelModus.DEG;
    }

    public boolean isGleichGedrueckt()
    {
        return gleichGedrueckt;
    }

    public void setGleichGedrueckt(boolean gleichGedrueckt)
    {
        this.gleichGedrueckt = gleichGedrueckt;
    }

    public StringBuilder getVerlauf()
    {
        return verlauf;
    }

    public StringBuilder getAusdruck()
    {
        return ausdruck;
    }

    public double getLetzteAntwort()
    {
        return letzteAntwort;
    }

    public void setLetzteAntwort(double letzteAntwort)
    {
        this.letzteAntwort = letzteAntwort;
    }

    public String getAusdruckText()
    {
        return ausdruck.toString();
    }

    public void setAusdruckText(String text)
    {
        ausdruck.setLength(0);
        ausdruck.append(text);
    }

    public void clearAusdruck()
    {
        ausdruck.setLength(0);
    }

    public String getVerlaufText()
    {
        return verlauf.toString();
    }

    public void setVerlaufText(String text)
    {
        verlauf.setLength(0);
        verlauf.append(text);
    }

    public void clearVerlauf()
    {
        verlauf.setLength(0);
    }
}
