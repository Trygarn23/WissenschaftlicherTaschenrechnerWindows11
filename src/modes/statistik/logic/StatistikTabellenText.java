package modes.statistik.logic;

import common.formatting.ZahlenEingabe;
import modes.statistik.model.StatistikDatenpunkt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Wandelt Statistikdaten in Tabellentext (Zwischenablage) und liest CSV-Dateien ein – ohne Swing. */
public class StatistikTabellenText
{
    private static final String KOPFZEILE = "x\ty\tGewicht";

    /** Tab-getrennt mit Dezimalkomma, so wie Excel und LibreOffice es beim Einfügen erwarten. */
    public String alsTabellenText(List<StatistikDatenpunkt> daten)
    {
        StringBuilder text = new StringBuilder(KOPFZEILE);
        for (StatistikDatenpunkt punkt : daten)
        {
            text.append('\n')
                    .append(zahl(punkt.x())).append('\t')
                    .append(zahl(punkt.y())).append('\t')
                    .append(zahl(punkt.gewicht()));
        }
        return text.toString();
    }

    /**
     * Liest Semikolon- oder Tab-getrennte Zeilen mit 1 (y), 2 (x; y) oder 3 Spalten (x; y; Gewicht).
     * Dezimalkomma und -punkt gehen beide; eine Kopfzeile wird erkannt, wenn ihr erstes Feld keine Zahl ist.
     */
    public List<StatistikDatenpunkt> liesCsv(String text)
    {
        String inhalt = text == null ? "" : text.replace("﻿", "");
        String trenner = inhalt.contains(";") ? ";" : "\t";
        String[] zeilen = inhalt.split("\\R");

        List<StatistikDatenpunkt> daten = new ArrayList<>();
        boolean ersteZeile = true;
        for (int i = 0; i < zeilen.length; i++)
        {
            if (zeilen[i].isBlank())
            {
                continue;
            }

            List<String> felder = felder(zeilen[i], trenner);
            if (felder.isEmpty())
            {
                continue;
            }
            if (ersteZeile && !istZahl(felder.getFirst()))
            {
                ersteZeile = false;
                continue;
            }
            ersteZeile = false;

            try
            {
                daten.add(datenpunkt(felder, daten.size() + 1));
            }
            catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException("Zeile " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        if (daten.isEmpty())
        {
            throw new IllegalArgumentException("Die Datei enthält keine Daten.");
        }
        return daten;
    }

    private List<String> felder(String zeile, String trenner)
    {
        List<String> felder = new ArrayList<>(Arrays.stream(zeile.split(trenner, -1)).map(String::trim).toList());
        while (!felder.isEmpty() && felder.getLast().isEmpty())
        {
            felder.removeLast();
        }
        return felder;
    }

    private StatistikDatenpunkt datenpunkt(List<String> felder, int index)
    {
        if (felder.size() > 3)
        {
            throw new IllegalArgumentException("Höchstens 3 Spalten erlaubt (x, y, Gewicht).");
        }

        double[] zahlen = felder.stream().mapToDouble(ZahlenEingabe::lese).toArray();
        return switch (zahlen.length)
        {
            case 1 -> StatistikDatenpunkt.nurY(index, zahlen[0]);
            case 2 -> StatistikDatenpunkt.nurY(zahlen[0], zahlen[1]);
            default -> new StatistikDatenpunkt(zahlen[0], zahlen[1], zahlen[2]);
        };
    }

    private boolean istZahl(String feld)
    {
        try
        {
            ZahlenEingabe.lese(feld);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    private String zahl(double wert)
    {
        // Ohne Tausenderpunkte, damit der Text auch wieder eingelesen werden kann.
        return BigDecimal.valueOf(wert).stripTrailingZeros().toPlainString().replace('.', ',');
    }
}
