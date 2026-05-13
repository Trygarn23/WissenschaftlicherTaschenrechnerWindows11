package common.units;

public enum EinheitKategorie
{
    LAENGE("Laenge"),
    FLAECHE("Flaeche"),
    VOLUMEN("Volumen"),
    MASSE("Masse"),
    ZEIT("Zeit"),
    GESCHWINDIGKEIT("Geschwindigkeit"),
    TEMPERATUR("Temperatur"),
    DATENMENGE("Datenmenge");

    private final String label;

    EinheitKategorie(String label)
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
