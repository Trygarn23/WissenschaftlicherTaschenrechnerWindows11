package modes.statistik.model;

public record StatistikKlasse(double von, double bis, int anzahl)
{
    public StatistikKlasse
    {
        if (!Double.isFinite(von) || !Double.isFinite(bis) || bis < von)
        {
            throw new IllegalArgumentException("Ungültige Klassengrenzen.");
        }

        if (anzahl < 0)
        {
            throw new IllegalArgumentException("Anzahl darf nicht negativ sein.");
        }
    }
}
