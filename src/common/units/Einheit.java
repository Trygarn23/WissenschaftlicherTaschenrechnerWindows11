package common.units;

public record Einheit(EinheitKategorie kategorie, String name, String symbol, double faktorZurBasiseinheit, double offsetZurBasiseinheit)
{
    public Einheit
    {
        if (kategorie == null)
        {
            throw new IllegalArgumentException("Kategorie fehlt.");
        }

        if (name == null || name.isBlank() || symbol == null || symbol.isBlank())
        {
            throw new IllegalArgumentException("Einheit braucht Name und Symbol.");
        }

        if (!Double.isFinite(faktorZurBasiseinheit) || Math.abs(faktorZurBasiseinheit) < 1e-15)
        {
            throw new IllegalArgumentException("Faktor muss endlich und ungleich 0 sein.");
        }

        if (!Double.isFinite(offsetZurBasiseinheit))
        {
            throw new IllegalArgumentException("Offset muss endlich sein.");
        }
    }

    public double nachBasis(double wert)
    {
        return wert * faktorZurBasiseinheit + offsetZurBasiseinheit;
    }

    public double ausBasis(double basiswert)
    {
        return (basiswert - offsetZurBasiseinheit) / faktorZurBasiseinheit;
    }

    @Override
    public String toString()
    {
        return name + " (" + symbol + ")";
    }
}
