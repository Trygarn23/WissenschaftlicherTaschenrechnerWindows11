package modes.komplex.model;

public final class KomplexeZahl
{
    private final double real;
    private final double imaginaer;

    public KomplexeZahl(double real, double imaginaer)
    {
        this.real = real;
        this.imaginaer = imaginaer;
    }

    public double getReal()
    {
        return real;
    }

    public double getImaginaer()
    {
        return imaginaer;
    }

    public KomplexeZahl addiere(KomplexeZahl andere)
    {
        return new KomplexeZahl(real + andere.real, imaginaer + andere.imaginaer);
    }

    public KomplexeZahl subtrahiere(KomplexeZahl andere)
    {
        return new KomplexeZahl(real - andere.real, imaginaer - andere.imaginaer);
    }

    public KomplexeZahl multipliziere(KomplexeZahl andere)
    {
        return new KomplexeZahl(
                real * andere.real - imaginaer * andere.imaginaer,
                real * andere.imaginaer + imaginaer * andere.real
        );
    }

    public KomplexeZahl dividiere(KomplexeZahl andere)
    {
        double nenner = andere.real * andere.real + andere.imaginaer * andere.imaginaer;
        if (nenner == 0.0)
        {
            throw new ArithmeticException("Division durch 0 + 0i");
        }

        return new KomplexeZahl(
                (real * andere.real + imaginaer * andere.imaginaer) / nenner,
                (imaginaer * andere.real - real * andere.imaginaer) / nenner
        );
    }

    public KomplexeZahl konjugiert()
    {
        return new KomplexeZahl(real, -imaginaer);
    }

    public double betrag()
    {
        return Math.hypot(real, imaginaer);
    }

    public double phaseRad()
    {
        return Math.atan2(imaginaer, real);
    }

    public double phaseDeg()
    {
        return Math.toDegrees(phaseRad());
    }

    public static KomplexeZahl ausPolar(double betrag, double phaseRad)
    {
        return new KomplexeZahl(betrag * Math.cos(phaseRad), betrag * Math.sin(phaseRad));
    }
}
