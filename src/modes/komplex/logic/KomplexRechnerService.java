package modes.komplex.logic;

import modes.komplex.model.KomplexeZahl;

public class KomplexRechnerService
{
    public KomplexeZahl addiere(KomplexeZahl a, KomplexeZahl b)
    {
        return a.addiere(b);
    }

    public KomplexeZahl subtrahiere(KomplexeZahl a, KomplexeZahl b)
    {
        return a.subtrahiere(b);
    }

    public KomplexeZahl multipliziere(KomplexeZahl a, KomplexeZahl b)
    {
        return a.multipliziere(b);
    }

    public KomplexeZahl dividiere(KomplexeZahl a, KomplexeZahl b)
    {
        return a.dividiere(b);
    }

    public KomplexeZahl konjugiert(KomplexeZahl zahl)
    {
        return zahl.konjugiert();
    }
}
