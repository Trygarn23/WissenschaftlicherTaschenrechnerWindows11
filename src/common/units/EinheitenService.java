package common.units;

import java.util.List;

public class EinheitenService
{
    private final EinheitenKatalog katalog;

    public EinheitenService()
    {
        this(new EinheitenKatalog());
    }

    public EinheitenService(EinheitenKatalog katalog)
    {
        this.katalog = katalog;
    }

    public List<EinheitKategorie> kategorien()
    {
        return katalog.kategorien();
    }

    public List<Einheit> einheitenFuer(EinheitKategorie kategorie)
    {
        return katalog.einheitenFuer(kategorie);
    }

    public double rechneUm(double wert, Einheit von, Einheit nach)
    {
        if (von == null || nach == null)
        {
            throw new IllegalArgumentException("Start- und Zieleinheit muessen gesetzt sein.");
        }

        if (von.kategorie() != nach.kategorie())
        {
            throw new IllegalArgumentException("Einheiten muessen zur gleichen Kategorie gehoeren.");
        }

        if (!Double.isFinite(wert))
        {
            throw new IllegalArgumentException("Wert muss endlich sein.");
        }

        return nach.ausBasis(von.nachBasis(wert));
    }
}
