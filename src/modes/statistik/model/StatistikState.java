package modes.statistik.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatistikState
{
    private final List<StatistikDatenpunkt> datenpunkte = new ArrayList<>();
    private boolean sortiert;
    private int klassenAnzahl = 0;

    public List<StatistikDatenpunkt> getDatenpunkte()
    {
        return List.copyOf(datenpunkte);
    }

    public void setDatenpunkte(List<StatistikDatenpunkt> neueDatenpunkte)
    {
        datenpunkte.clear();
        if (neueDatenpunkte != null)
        {
            datenpunkte.addAll(neueDatenpunkte);
        }
        sortiereWennNoetig();
    }

    public boolean isSortiert()
    {
        return sortiert;
    }

    public void setSortiert(boolean sortiert)
    {
        this.sortiert = sortiert;
        sortiereWennNoetig();
    }

    public int getKlassenAnzahl()
    {
        return klassenAnzahl;
    }

    public void setKlassenAnzahl(int klassenAnzahl)
    {
        this.klassenAnzahl = Math.max(0, klassenAnzahl);
    }

    private void sortiereWennNoetig()
    {
        if (sortiert)
        {
            datenpunkte.sort(Comparator.comparingDouble(StatistikDatenpunkt::y));
        }
    }
}
