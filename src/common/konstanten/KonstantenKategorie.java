package common.konstanten;

public enum KonstantenKategorie
{
    MATHEMATIK("Mathematik"),
    PHYSIK("Physik"),
    CHEMIE("Chemie"),
    INFORMATIK("Informatik"),
    EIGENE("Eigene");

    private final String label;

    KonstantenKategorie(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
