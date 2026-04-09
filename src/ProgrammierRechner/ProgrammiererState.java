package ProgrammierRechner;

public class ProgrammiererState
{
    private long wert = 0;
    private Basis basis = Basis.DEC;
    private Wortbreite wortbreite = Wortbreite.QWORD;
    private boolean unsigned = false;

    public long getWert()
    {
        return wert;
    }

    public void setWert(long wert)
    {
        this.wert = wert;
    }

    public Basis getBasis()
    {
        return basis;
    }

    public void setBasis(Basis basis)
    {
        this.basis = basis;
    }

    public Wortbreite getWortbreite()
    {
        return wortbreite;
    }

    public void setWortbreite(Wortbreite wortbreite)
    {
        this.wortbreite = wortbreite;
    }

    public boolean isUnsigned()
    {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned)
    {
        this.unsigned = unsigned;
    }
}

