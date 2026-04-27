package common.state;

public class SpeicherState
{
    private double wert = 0.0;

    public double getWert()
    {
        return wert;
    }

    public void setWert(double wert)
    {
        this.wert = wert;
    }

    public void loeschen()
    {
        wert = 0.0;
    }

    public void addiere(double betrag)
    {
        wert += betrag;
    }

    public void subtrahiere(double betrag)
    {
        wert -= betrag;
    }
}