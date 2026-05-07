package modes.komplex.model;

public class KomplexState
{
    private KomplexeZahl ersteZahl = new KomplexeZahl(0, 0);
    private KomplexeZahl zweiteZahl = new KomplexeZahl(0, 0);
    private KomplexeZahl ergebnis = new KomplexeZahl(0, 0);
    private KomplexDarstellung darstellung = KomplexDarstellung.KARTESISCH;
    private String status = "Bereit";

    public KomplexeZahl getErsteZahl()
    {
        return ersteZahl;
    }

    public void setErsteZahl(KomplexeZahl ersteZahl)
    {
        this.ersteZahl = ersteZahl;
    }

    public KomplexeZahl getZweiteZahl()
    {
        return zweiteZahl;
    }

    public void setZweiteZahl(KomplexeZahl zweiteZahl)
    {
        this.zweiteZahl = zweiteZahl;
    }

    public KomplexeZahl getErgebnis()
    {
        return ergebnis;
    }

    public void setErgebnis(KomplexeZahl ergebnis)
    {
        this.ergebnis = ergebnis;
    }

    public KomplexDarstellung getDarstellung()
    {
        return darstellung;
    }

    public void setDarstellung(KomplexDarstellung darstellung)
    {
        this.darstellung = darstellung;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
