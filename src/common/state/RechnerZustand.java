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

    @Deprecated
    public StringBuilder getVerlauf()
    {
        return verlauf;
    }

    @Deprecated
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
        if (text != null)
        {
            ausdruck.append(text);
        }
    }

    public void appendAusdruck(String text)
    {
        if (text != null)
        {
            ausdruck.append(text);
        }
    }

    public int getAusdruckLaenge()
    {
        return ausdruck.length();
    }

    public boolean isAusdruckLeer()
    {
        return ausdruck.isEmpty();
    }

    public char getAusdruckZeichen(int index)
    {
        return ausdruck.charAt(index);
    }

    public String getAusdruckTeilText(int start)
    {
        return ausdruck.substring(start);
    }

    public String getAusdruckTeilText(int start, int end)
    {
        return ausdruck.substring(start, end);
    }

    public void insertAusdruck(int index, String text)
    {
        if (text != null)
        {
            ausdruck.insert(index, text);
        }
    }

    public void deleteAusdruck(int start, int end)
    {
        ausdruck.delete(start, end);
    }

    public void deleteAusdruckZeichen(int index)
    {
        ausdruck.deleteCharAt(index);
    }

    public void setAusdruckLaenge(int laenge)
    {
        ausdruck.setLength(laenge);
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
        if (text != null)
        {
            verlauf.append(text);
        }
    }

    public void appendVerlauf(String text)
    {
        if (text != null)
        {
            verlauf.append(text);
        }
    }

    public void clearVerlauf()
    {
        verlauf.setLength(0);
    }
}
