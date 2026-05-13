package modes.statistik.model;

import java.util.List;

public final class StatistikErgebnis
{
    private final List<StatistikDatenpunkt> datenpunkte;
    private final List<Double> modalwerte;
    private final List<StatistikKlasse> histogramm;
    private final int anzahl;
    private final double summe;
    private final double minimum;
    private final double maximum;
    private final double mittelwert;
    private final double median;
    private final double q1;
    private final double q3;
    private final double varianzPopulation;
    private final double varianzStichprobe;
    private final double standardabweichungPopulation;
    private final double standardabweichungStichprobe;
    private final StatistikRegression lineareRegression;
    private final StatistikRegression quadratischeRegression;

    public StatistikErgebnis(
            List<StatistikDatenpunkt> datenpunkte,
            List<Double> modalwerte,
            List<StatistikKlasse> histogramm,
            int anzahl,
            double summe,
            double minimum,
            double maximum,
            double mittelwert,
            double median,
            double q1,
            double q3,
            double varianzPopulation,
            double varianzStichprobe,
            double standardabweichungPopulation,
            double standardabweichungStichprobe,
            StatistikRegression lineareRegression,
            StatistikRegression quadratischeRegression)
    {
        this.datenpunkte = List.copyOf(datenpunkte);
        this.modalwerte = List.copyOf(modalwerte);
        this.histogramm = List.copyOf(histogramm);
        this.anzahl = anzahl;
        this.summe = summe;
        this.minimum = minimum;
        this.maximum = maximum;
        this.mittelwert = mittelwert;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.varianzPopulation = varianzPopulation;
        this.varianzStichprobe = varianzStichprobe;
        this.standardabweichungPopulation = standardabweichungPopulation;
        this.standardabweichungStichprobe = standardabweichungStichprobe;
        this.lineareRegression = lineareRegression;
        this.quadratischeRegression = quadratischeRegression;
    }

    public List<StatistikDatenpunkt> getDatenpunkte()
    {
        return datenpunkte;
    }

    public List<Double> getModalwerte()
    {
        return modalwerte;
    }

    public List<StatistikKlasse> getHistogramm()
    {
        return histogramm;
    }

    public int getAnzahl()
    {
        return anzahl;
    }

    public double getSumme()
    {
        return summe;
    }

    public double getMinimum()
    {
        return minimum;
    }

    public double getMaximum()
    {
        return maximum;
    }

    public double getMittelwert()
    {
        return mittelwert;
    }

    public double getMedian()
    {
        return median;
    }

    public double getQ1()
    {
        return q1;
    }

    public double getQ3()
    {
        return q3;
    }

    public double getSpannweite()
    {
        return maximum - minimum;
    }

    public double getInterquartilsabstand()
    {
        return q3 - q1;
    }

    public double getVarianzPopulation()
    {
        return varianzPopulation;
    }

    public double getVarianzStichprobe()
    {
        return varianzStichprobe;
    }

    public double getStandardabweichungPopulation()
    {
        return standardabweichungPopulation;
    }

    public double getStandardabweichungStichprobe()
    {
        return standardabweichungStichprobe;
    }

    public StatistikRegression getLineareRegression()
    {
        return lineareRegression;
    }

    public StatistikRegression getQuadratischeRegression()
    {
        return quadratischeRegression;
    }
}
