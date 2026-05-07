package modes.komplex.model;

public enum KomplexDarstellung
{
    KARTESISCH("Kartesisch"),
    POLAR_RAD("Polar RAD"),
    POLAR_DEG("Polar DEG");

    private final String label;

    KomplexDarstellung(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
