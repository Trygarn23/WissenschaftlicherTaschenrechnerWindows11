package modes.statistik.formatting;

import common.formatting.ZahlenAnzeige;
import modes.statistik.model.StatistikErgebnis;
import modes.statistik.model.StatistikRegression;

import java.util.stream.Collectors;

public class StatistikFormatter
{
    public String formatiereZahl(double wert)
    {
        return ZahlenAnzeige.formatiere(wert);
    }

    public String formatiereErgebnis(StatistikErgebnis ergebnis)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Kennzahlen").append(System.lineSeparator());
        builder.append("n: ").append(ergebnis.getAnzahl()).append(System.lineSeparator());
        builder.append("Summe: ").append(formatiereZahl(ergebnis.getSumme())).append(System.lineSeparator());
        builder.append("Mittelwert: ").append(formatiereZahl(ergebnis.getMittelwert())).append(System.lineSeparator());
        builder.append("Median: ").append(formatiereZahl(ergebnis.getMedian())).append(System.lineSeparator());
        builder.append("Modus: ").append(formatiereModalwerte(ergebnis)).append(System.lineSeparator());
        builder.append("Minimum: ").append(formatiereZahl(ergebnis.getMinimum())).append(System.lineSeparator());
        builder.append("Maximum: ").append(formatiereZahl(ergebnis.getMaximum())).append(System.lineSeparator());
        builder.append("Spannweite: ").append(formatiereZahl(ergebnis.getSpannweite())).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        builder.append("Streuung").append(System.lineSeparator());
        builder.append("Q1: ").append(formatiereZahl(ergebnis.getQ1())).append(System.lineSeparator());
        builder.append("Q3: ").append(formatiereZahl(ergebnis.getQ3())).append(System.lineSeparator());
        builder.append("IQR: ").append(formatiereZahl(ergebnis.getInterquartilsabstand())).append(System.lineSeparator());
        builder.append("Varianz (Population): ").append(formatiereZahl(ergebnis.getVarianzPopulation())).append(System.lineSeparator());
        builder.append("StdAbw (Population): ").append(formatiereZahl(ergebnis.getStandardabweichungPopulation())).append(System.lineSeparator());
        builder.append("Varianz (Stichprobe): ").append(formatiereZahl(ergebnis.getVarianzStichprobe())).append(System.lineSeparator());
        builder.append("StdAbw (Stichprobe): ").append(formatiereZahl(ergebnis.getStandardabweichungStichprobe())).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        builder.append("Regression").append(System.lineSeparator());
        builder.append(formatiereRegression(ergebnis.getLineareRegression())).append(System.lineSeparator());
        builder.append(formatiereRegression(ergebnis.getQuadratischeRegression()));
        return builder.toString();
    }

    private String formatiereModalwerte(StatistikErgebnis ergebnis)
    {
        if (ergebnis.getModalwerte().isEmpty())
        {
            return "kein eindeutiger Modus";
        }

        return ergebnis.getModalwerte().stream()
                .map(this::formatiereZahl)
                .collect(Collectors.joining(", "));
    }

    private String formatiereRegression(StatistikRegression regression)
    {
        if (regression == null)
        {
            return "nicht berechenbar";
        }

        if ("Linear".equals(regression.getTyp()))
        {
            return "Linear: y = " + formatiereZahl(regression.getB())
                    + "x + " + formatiereZahl(regression.getC())
                    + " | R2 = " + formatiereZahl(regression.getBestimmtheitsmass());
        }

        return "Quadratisch: y = " + formatiereZahl(regression.getA())
                + "x^2 + " + formatiereZahl(regression.getB())
                + "x + " + formatiereZahl(regression.getC())
                + " | R2 = " + formatiereZahl(regression.getBestimmtheitsmass());
    }
}
