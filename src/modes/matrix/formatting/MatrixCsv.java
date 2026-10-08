package modes.matrix.formatting;

import common.formatting.ZahlenEingabe;
import modes.matrix.model.Matrix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/** Matrix ↔ CSV-Text: Semikolon als Trenner, Dezimalkomma (wie deutsches Excel). */
public final class MatrixCsv
{
    private MatrixCsv()
    {
    }

    public static String schreibe(Matrix matrix)
    {
        StringBuilder builder = new StringBuilder();
        for (int z = 0; z < matrix.getZeilen(); z++)
        {
            StringJoiner zeile = new StringJoiner(";");
            for (int s = 0; s < matrix.getSpalten(); s++)
            {
                // Volle Genauigkeit statt Anzeigeformat, damit Export → Import nichts verliert.
                zeile.add(BigDecimal.valueOf(matrix.get(z, s)).stripTrailingZeros().toPlainString().replace('.', ','));
            }
            builder.append(zeile).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public static Matrix lese(String text)
    {
        String inhalt = text == null ? "" : text.replace("﻿", "");
        List<double[]> zeilen = new ArrayList<>();
        String[] rohZeilen = inhalt.split("\\R");
        for (int i = 0; i < rohZeilen.length; i++)
        {
            if (rohZeilen[i].isBlank())
            {
                continue;
            }

            String[] zellen = rohZeilen[i].split(";", -1);
            if (!zeilen.isEmpty() && zellen.length != zeilen.get(0).length)
            {
                throw new IllegalArgumentException("Zeile " + (i + 1) + " hat " + zellen.length
                        + " Werte, erwartet sind " + zeilen.get(0).length + ".");
            }

            double[] werte = new double[zellen.length];
            for (int s = 0; s < zellen.length; s++)
            {
                try
                {
                    werte[s] = ZahlenEingabe.lese(zellen[s]);
                }
                catch (IllegalArgumentException e)
                {
                    throw new IllegalArgumentException("Zeile " + (i + 1) + ", Spalte " + (s + 1) + ": " + e.getMessage(), e);
                }
            }
            zeilen.add(werte);
        }

        if (zeilen.isEmpty())
        {
            throw new IllegalArgumentException("Die CSV-Datei enthält keine Werte.");
        }
        return new Matrix(zeilen.toArray(new double[0][]));
    }
}
