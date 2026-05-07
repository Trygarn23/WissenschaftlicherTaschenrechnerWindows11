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

    public KomplexeZahl ausPolarDeg(double betrag, double phaseDeg)
    {
        return KomplexeZahl.ausPolar(betrag, Math.toRadians(phaseDeg));
    }

    public KomplexeZahl ausPolarRad(double betrag, double phaseRad)
    {
        return KomplexeZahl.ausPolar(betrag, phaseRad);
    }
}
