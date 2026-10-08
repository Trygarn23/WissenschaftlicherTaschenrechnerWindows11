package modes.matrix.logic;

import common.formatting.ZahlenAnzeige;
import modes.matrix.model.InverseErgebnis;
import modes.matrix.model.LgsLoesung;
import modes.matrix.model.Matrix;
import modes.matrix.model.RechenSchritt;

import java.util.ArrayList;
import java.util.List;

/** Gauß-Jordan mit Teilpivotisierung; jeder Zeilenschritt wird protokolliert. */
public class GaussJordan
{
    private static final double TOLERANZ = 1e-10;

    public InverseErgebnis invertiere(Matrix matrix)
    {
        int n = matrix.getZeilen();
        if (n != matrix.getSpalten())
        {
            throw new IllegalArgumentException("Die Inverse gibt es nur für quadratische Matrizen.");
        }

        double[][] erweitert = new double[n][2 * n];
        for (int z = 0; z < n; z++)
        {
            for (int s = 0; s < n; s++)
            {
                erweitert[z][s] = matrix.get(z, s);
            }
            erweitert[z][n + z] = 1.0;
        }

        List<RechenSchritt> schritte = new ArrayList<>();
        if (reduziere(erweitert, n, schritte) < n)
        {
            throw new IllegalArgumentException("Die Matrix ist singulär und hat keine Inverse.");
        }

        double[][] inverse = new double[n][n];
        for (int z = 0; z < n; z++)
        {
            System.arraycopy(erweitert[z], n, inverse[z], 0, n);
        }
        return new InverseErgebnis(new Matrix(inverse), schritte);
    }

    public LgsLoesung loese(Matrix a, Matrix b)
    {
        if (b.getSpalten() != 1)
        {
            throw new IllegalArgumentException("b muss ein Spaltenvektor sein.");
        }
        if (b.getZeilen() != a.getZeilen())
        {
            throw new IllegalArgumentException("b braucht so viele Zeilen wie A (" + a.getZeilen() + ").");
        }

        int zeilen = a.getZeilen();
        int unbekannte = a.getSpalten();
        double[][] erweitert = new double[zeilen][unbekannte + 1];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < unbekannte; s++)
            {
                erweitert[z][s] = a.get(z, s);
            }
            erweitert[z][unbekannte] = b.get(z, 0);
        }

        List<RechenSchritt> schritte = new ArrayList<>();
        int rangA = reduziere(erweitert, unbekannte, schritte);

        // Unterhalb der Pivotzeilen ist A schon null; steht rechts noch etwas, heißt die Zeile 0 = c ≠ 0.
        int rangErweitert = rangA;
        for (int z = rangA; z < zeilen; z++)
        {
            if (Math.abs(erweitert[z][unbekannte]) > TOLERANZ)
            {
                rangErweitert = rangA + 1;
                break;
            }
        }

        Matrix stufenform = new Matrix(erweitert);
        if (rangErweitert > rangA)
        {
            return new LgsLoesung(LgsLoesung.Art.KEINE, null, stufenform, rangA, rangErweitert, unbekannte, schritte);
        }
        if (rangA < unbekannte)
        {
            return new LgsLoesung(LgsLoesung.Art.UNENDLICH_VIELE, null, stufenform, rangA, rangErweitert, unbekannte, schritte);
        }

        double[][] x = new double[unbekannte][1];
        for (int i = 0; i < unbekannte; i++)
        {
            x[i][0] = erweitert[i][unbekannte];
        }
        return new LgsLoesung(LgsLoesung.Art.EINDEUTIG, new Matrix(x), stufenform, rangA, rangErweitert, unbekannte, schritte);
    }

    /** Bringt m in reduzierte Stufenform; Pivots nur in den ersten pivotSpalten Spalten. Liefert den Rang dieses Teils. */
    private int reduziere(double[][] m, int pivotSpalten, List<RechenSchritt> schritte)
    {
        int zeile = 0;
        for (int spalte = 0; spalte < pivotSpalten && zeile < m.length; spalte++)
        {
            int beste = zeile;
            for (int z = zeile + 1; z < m.length; z++)
            {
                if (Math.abs(m[z][spalte]) > Math.abs(m[beste][spalte]))
                {
                    beste = z;
                }
            }
            if (Math.abs(m[beste][spalte]) <= TOLERANZ)
            {
                continue;
            }

            if (beste != zeile)
            {
                double[] tmp = m[zeile];
                m[zeile] = m[beste];
                m[beste] = tmp;
                protokolliere(schritte, "Z" + (zeile + 1) + " ↔ Z" + (beste + 1), m);
            }

            double pivot = m[zeile][spalte];
            if (pivot != 1.0)
            {
                for (int s = 0; s < m[zeile].length; s++)
                {
                    m[zeile][s] /= pivot;
                }
                protokolliere(schritte, "Z" + (zeile + 1) + " ← Z" + (zeile + 1) + " : " + klammere(pivot), m);
            }

            for (int z = 0; z < m.length; z++)
            {
                double faktor = m[z][spalte];
                if (z == zeile || Math.abs(faktor) <= TOLERANZ)
                {
                    continue;
                }
                for (int s = 0; s < m[z].length; s++)
                {
                    m[z][s] -= faktor * m[zeile][s];
                }
                protokolliere(schritte, zeilenSubtraktion(z, faktor, zeile), m);
            }
            zeile++;
        }
        return zeile;
    }

    private void protokolliere(List<RechenSchritt> schritte, String beschreibung, double[][] m)
    {
        // Rundungsreste wie 1e-17 auf 0 setzen, sonst sehen Zwischenmatrizen und Rangprüfung schief aus.
        for (double[] zeile : m)
        {
            for (int s = 0; s < zeile.length; s++)
            {
                if (Math.abs(zeile[s]) <= TOLERANZ)
                {
                    zeile[s] = 0.0;
                }
            }
        }
        schritte.add(new RechenSchritt(beschreibung, new Matrix(m)));
    }

    private String zeilenSubtraktion(int ziel, double faktor, int quelle)
    {
        String zeichen = faktor > 0 ? " − " : " + ";
        double betrag = Math.abs(faktor);
        String vielfaches = Math.abs(betrag - 1.0) <= TOLERANZ ? "" : ZahlenAnzeige.formatiere(betrag) + "·";
        return "Z" + (ziel + 1) + " ← Z" + (ziel + 1) + zeichen + vielfaches + "Z" + (quelle + 1);
    }

    private String klammere(double wert)
    {
        String text = ZahlenAnzeige.formatiere(wert);
        return wert < 0 ? "(" + text + ")" : text;
    }
}
