package modes.statistik.logic;

import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikErgebnis;
import modes.statistik.model.StatistikKlasse;
import modes.statistik.model.StatistikRegression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatistikRechnerService
{
    private static final Pattern ZAHL_PATTERN =
            Pattern.compile("[-+]?(?:\\d+(?:[,.]\\d+)?|[,.]\\d+)(?:[eE][-+]?\\d+)?");

    public List<StatistikDatenpunkt> parseText(String text)
    {
        if (text == null || text.isBlank())
        {
            return List.of();
        }

        if (enthaeltTabellarischeTrenner(text))
        {
            return parseTabellarischenText(text);
        }

        List<StatistikDatenpunkt> result = new ArrayList<>();
        Matcher matcher = ZAHL_PATTERN.matcher(text);
        int index = 1;
        while (matcher.find())
        {
            result.add(StatistikDatenpunkt.nurY(index++, parseZahl(matcher.group())));
        }
        return result;
    }

    public StatistikErgebnis berechne(List<StatistikDatenpunkt> datenpunkte)
    {
        if (datenpunkte == null || datenpunkte.isEmpty())
        {
            throw new IllegalArgumentException("Mindestens ein Datenwert wird benoetigt.");
        }

        List<StatistikDatenpunkt> daten = List.copyOf(datenpunkte);
        List<Double> werte = daten.stream()
                .map(StatistikDatenpunkt::y)
                .sorted()
                .toList();

        int n = werte.size();
        double summe = werte.stream().mapToDouble(Double::doubleValue).sum();
        double minimum = werte.getFirst();
        double maximum = werte.getLast();
        double gewichtSumme = daten.stream().mapToDouble(StatistikDatenpunkt::gewicht).sum();
        double mittelwert = daten.stream()
                .mapToDouble(punkt -> punkt.y() * punkt.gewicht())
                .sum() / gewichtSumme;

        double median = quantil(werte, 0.5);
        double q1 = quantil(werte, 0.25);
        double q3 = quantil(werte, 0.75);
        double quadratabweichung = gewichteteQuadratabweichung(daten, mittelwert);
        double varianzPopulation = quadratabweichung / gewichtSumme;
        double varianzStichprobe = n > 1 && gewichtSumme > 1.0 ? quadratabweichung / (gewichtSumme - 1.0) : 0.0;

        return new StatistikErgebnis(
                daten,
                modalwerte(werte),
                erstelleHistogramm(werte, 0),
                n,
                summe,
                minimum,
                maximum,
                mittelwert,
                median,
                q1,
                q3,
                varianzPopulation,
                varianzStichprobe,
                Math.sqrt(varianzPopulation),
                Math.sqrt(varianzStichprobe),
                lineareRegression(daten),
                quadratischeRegression(daten)
        );
    }

    public List<StatistikKlasse> histogramm(List<StatistikDatenpunkt> datenpunkte, int klassenAnzahl)
    {
        if (datenpunkte == null || datenpunkte.isEmpty())
        {
            return List.of();
        }

        List<Double> werte = datenpunkte.stream()
                .map(StatistikDatenpunkt::y)
                .sorted()
                .toList();
        return erstelleHistogramm(werte, klassenAnzahl);
    }

    private List<StatistikDatenpunkt> parseTabellarischenText(String text)
    {
        List<StatistikDatenpunkt> result = new ArrayList<>();
        String[] zeilen = text.split("\\R");
        int index = 1;

        for (String zeile : zeilen)
        {
            if (zeile == null || zeile.isBlank())
            {
                continue;
            }

            String[] teile = zeile.trim().split("[;\\t]");
            List<Double> zahlen = new ArrayList<>();
            for (String teil : teile)
            {
                if (!teil.isBlank())
                {
                    zahlen.add(parseZahl(teil.trim()));
                }
            }

            if (zahlen.size() == 1)
            {
                result.add(StatistikDatenpunkt.nurY(index++, zahlen.getFirst()));
            }
            else if (zahlen.size() >= 2)
            {
                double gewicht = zahlen.size() >= 3 ? zahlen.get(2) : 1.0;
                result.add(new StatistikDatenpunkt(zahlen.getFirst(), zahlen.get(1), gewicht));
                index++;
            }
        }

        return result;
    }

    private boolean enthaeltTabellarischeTrenner(String text)
    {
        return text.contains(";") || text.contains("\t");
    }

    private double parseZahl(String text)
    {
        return Double.parseDouble(text.trim().replace(',', '.'));
    }

    private double quantil(List<Double> werte, double p)
    {
        if (werte.size() == 1)
        {
            return werte.getFirst();
        }

        double position = p * (werte.size() - 1);
        int unten = (int) Math.floor(position);
        int oben = (int) Math.ceil(position);

        if (unten == oben)
        {
            return werte.get(unten);
        }

        double anteil = position - unten;
        return werte.get(unten) * (1.0 - anteil) + werte.get(oben) * anteil;
    }

    private double gewichteteQuadratabweichung(List<StatistikDatenpunkt> daten, double mittelwert)
    {
        return daten.stream()
                .mapToDouble(punkt -> punkt.gewicht() * Math.pow(punkt.y() - mittelwert, 2))
                .sum();
    }

    private List<Double> modalwerte(List<Double> werte)
    {
        Map<Double, Integer> haeufigkeiten = new LinkedHashMap<>();
        for (double wert : werte)
        {
            haeufigkeiten.merge(wert, 1, Integer::sum);
        }

        int maximum = haeufigkeiten.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (maximum <= 1)
        {
            return List.of();
        }

        return haeufigkeiten.entrySet().stream()
                .filter(entry -> entry.getValue() == maximum)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<StatistikKlasse> erstelleHistogramm(List<Double> werte, int klassenAnzahl)
    {
        if (werte.isEmpty())
        {
            return List.of();
        }

        double minimum = werte.getFirst();
        double maximum = werte.getLast();
        int klassen = klassenAnzahl > 0 ? klassenAnzahl : Math.max(1, (int) Math.ceil(Math.sqrt(werte.size())));

        if (minimum == maximum)
        {
            return List.of(new StatistikKlasse(minimum, maximum, werte.size()));
        }

        double breite = (maximum - minimum) / klassen;
        int[] counts = new int[klassen];
        for (double wert : werte)
        {
            int index = (int) Math.floor((wert - minimum) / breite);
            if (index >= klassen)
            {
                index = klassen - 1;
            }
            counts[index]++;
        }

        List<StatistikKlasse> result = new ArrayList<>();
        for (int i = 0; i < klassen; i++)
        {
            double von = minimum + i * breite;
            double bis = i == klassen - 1 ? maximum : von + breite;
            result.add(new StatistikKlasse(von, bis, counts[i]));
        }
        return result;
    }

    private StatistikRegression lineareRegression(List<StatistikDatenpunkt> daten)
    {
        if (daten.size() < 2)
        {
            return null;
        }

        double gewichtSumme = daten.stream().mapToDouble(StatistikDatenpunkt::gewicht).sum();
        double mittelX = daten.stream().mapToDouble(p -> p.x() * p.gewicht()).sum() / gewichtSumme;
        double mittelY = daten.stream().mapToDouble(p -> p.y() * p.gewicht()).sum() / gewichtSumme;
        double sxx = daten.stream().mapToDouble(p -> p.gewicht() * Math.pow(p.x() - mittelX, 2)).sum();

        if (Math.abs(sxx) < 1e-12)
        {
            return null;
        }

        double sxy = daten.stream().mapToDouble(p -> p.gewicht() * (p.x() - mittelX) * (p.y() - mittelY)).sum();
        double steigung = sxy / sxx;
        double achsenabschnitt = mittelY - steigung * mittelX;
        return StatistikRegression.linear(steigung, achsenabschnitt, bestimmtheitsmass(daten, x -> steigung * x + achsenabschnitt));
    }

    private StatistikRegression quadratischeRegression(List<StatistikDatenpunkt> daten)
    {
        if (daten.size() < 3)
        {
            return null;
        }

        double[][] matrix = new double[3][4];
        for (StatistikDatenpunkt punkt : daten)
        {
            double w = punkt.gewicht();
            double x = punkt.x();
            double y = punkt.y();
            matrix[0][0] += w * Math.pow(x, 4);
            matrix[0][1] += w * Math.pow(x, 3);
            matrix[0][2] += w * x * x;
            matrix[0][3] += w * x * x * y;
            matrix[1][0] += w * Math.pow(x, 3);
            matrix[1][1] += w * x * x;
            matrix[1][2] += w * x;
            matrix[1][3] += w * x * y;
            matrix[2][0] += w * x * x;
            matrix[2][1] += w * x;
            matrix[2][2] += w;
            matrix[2][3] += w * y;
        }

        double[] coeff = loese3x3(matrix);
        if (coeff == null)
        {
            return null;
        }

        return StatistikRegression.quadratisch(
                coeff[0],
                coeff[1],
                coeff[2],
                bestimmtheitsmass(daten, x -> coeff[0] * x * x + coeff[1] * x + coeff[2])
        );
    }

    private double[] loese3x3(double[][] matrix)
    {
        for (int pivot = 0; pivot < 3; pivot++)
        {
            int besteZeile = pivot;
            for (int zeile = pivot + 1; zeile < 3; zeile++)
            {
                if (Math.abs(matrix[zeile][pivot]) > Math.abs(matrix[besteZeile][pivot]))
                {
                    besteZeile = zeile;
                }
            }

            if (Math.abs(matrix[besteZeile][pivot]) < 1e-12)
            {
                return null;
            }

            double[] temp = matrix[pivot];
            matrix[pivot] = matrix[besteZeile];
            matrix[besteZeile] = temp;

            double divisor = matrix[pivot][pivot];
            for (int spalte = pivot; spalte < 4; spalte++)
            {
                matrix[pivot][spalte] /= divisor;
            }

            for (int zeile = 0; zeile < 3; zeile++)
            {
                if (zeile == pivot) continue;

                double faktor = matrix[zeile][pivot];
                for (int spalte = pivot; spalte < 4; spalte++)
                {
                    matrix[zeile][spalte] -= faktor * matrix[pivot][spalte];
                }
            }
        }

        return new double[]{matrix[0][3], matrix[1][3], matrix[2][3]};
    }

    private double bestimmtheitsmass(List<StatistikDatenpunkt> daten, RegressionFunction function)
    {
        double mittelY = daten.stream()
                .mapToDouble(StatistikDatenpunkt::y)
                .average()
                .orElse(0.0);
        double total = daten.stream()
                .mapToDouble(p -> Math.pow(p.y() - mittelY, 2))
                .sum();

        if (Math.abs(total) < 1e-12)
        {
            return 1.0;
        }

        double residual = daten.stream()
                .mapToDouble(p -> Math.pow(p.y() - function.apply(p.x()), 2))
                .sum();
        return 1.0 - residual / total;
    }

    @FunctionalInterface
    private interface RegressionFunction
    {
        double apply(double x);
    }
}
