package modes.matrix.model;

import java.util.Arrays;

public final class Matrix
{
    private static final double RANG_TOLERANZ = 1e-10;

    private final int zeilen;
    private final int spalten;
    private final double[][] werte;

    public Matrix(double[][] werte)
    {
        if (werte == null || werte.length == 0 || werte[0] == null || werte[0].length == 0)
        {
            throw new IllegalArgumentException("Matrix braucht mindestens eine Zeile und eine Spalte.");
        }

        this.zeilen = werte.length;
        this.spalten = werte[0].length;
        this.werte = new double[zeilen][spalten];

        for (int z = 0; z < zeilen; z++)
        {
            if (werte[z] == null || werte[z].length != spalten)
            {
                throw new IllegalArgumentException("Alle Matrixzeilen müssen gleich lang sein.");
            }

            System.arraycopy(werte[z], 0, this.werte[z], 0, spalten);
        }
    }

    public static Matrix nullMatrix(int zeilen, int spalten)
    {
        pruefeDimension(zeilen, spalten);
        return new Matrix(new double[zeilen][spalten]);
    }

    public int getZeilen()
    {
        return zeilen;
    }

    public int getSpalten()
    {
        return spalten;
    }

    public double get(int zeile, int spalte)
    {
        if (zeile < 0 || zeile >= zeilen || spalte < 0 || spalte >= spalten)
        {
            throw new IndexOutOfBoundsException("Matrixposition liegt außerhalb der Dimension.");
        }
        return werte[zeile][spalte];
    }

    public double[][] toArray()
    {
        double[][] copy = new double[zeilen][spalten];
        for (int z = 0; z < zeilen; z++)
        {
            System.arraycopy(werte[z], 0, copy[z], 0, spalten);
        }
        return copy;
    }

    public Matrix addiere(Matrix andere)
    {
        pruefeGleicheDimension(andere, "Addition");
        return kombiniere(andere, 1.0);
    }

    public Matrix subtrahiere(Matrix andere)
    {
        pruefeGleicheDimension(andere, "Subtraktion");
        return kombiniere(andere, -1.0);
    }

    public Matrix skalarMultiplizieren(double skalar)
    {
        double[][] result = new double[zeilen][spalten];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < spalten; s++)
            {
                result[z][s] = werte[z][s] * skalar;
            }
        }
        return new Matrix(result);
    }

    public Matrix multipliziere(Matrix andere)
    {
        if (andere == null)
        {
            throw new IllegalArgumentException("Zweite Matrix fehlt.");
        }

        if (spalten != andere.zeilen)
        {
            throw new IllegalArgumentException("Matrixmultiplikation braucht: Spalten von A = Zeilen von B.");
        }

        double[][] result = new double[zeilen][andere.spalten];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < andere.spalten; s++)
            {
                double summe = 0.0;
                for (int i = 0; i < spalten; i++)
                {
                    summe += werte[z][i] * andere.werte[i][s];
                }
                result[z][s] = summe;
            }
        }
        return new Matrix(result);
    }

    public double determinante()
    {
        if (zeilen != spalten)
        {
            throw new IllegalArgumentException("Determinante ist nur für quadratische Matrizen definiert.");
        }

        if (zeilen == 2)
        {
            return werte[0][0] * werte[1][1] - werte[0][1] * werte[1][0];
        }

        if (zeilen == 3)
        {
            return werte[0][0] * (werte[1][1] * werte[2][2] - werte[1][2] * werte[2][1])
                    - werte[0][1] * (werte[1][0] * werte[2][2] - werte[1][2] * werte[2][0])
                    + werte[0][2] * (werte[1][0] * werte[2][1] - werte[1][1] * werte[2][0]);
        }

        throw new IllegalArgumentException("Determinante ist aktuell nur für 2x2 und 3x3 umgesetzt.");
    }

    public Matrix transponiere()
    {
        double[][] result = new double[spalten][zeilen];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < spalten; s++)
            {
                result[s][z] = werte[z][s];
            }
        }
        return new Matrix(result);
    }

    public double spur()
    {
        if (zeilen != spalten)
        {
            throw new IllegalArgumentException("Spur ist nur fÃ¼r quadratische Matrizen definiert.");
        }

        double summe = 0.0;
        for (int i = 0; i < zeilen; i++)
        {
            summe += werte[i][i];
        }
        return summe;
    }

    public int rang()
    {
        double[][] arbeitskopie = toArray();
        int rang = 0;
        int pivotZeile = 0;

        for (int spalte = 0; spalte < spalten && pivotZeile < zeilen; spalte++)
        {
            int besteZeile = findePivotZeile(arbeitskopie, pivotZeile, spalte);
            if (Math.abs(arbeitskopie[besteZeile][spalte]) <= RANG_TOLERANZ)
            {
                continue;
            }

            tauscheZeilen(arbeitskopie, pivotZeile, besteZeile);
            eliminiereSpalte(arbeitskopie, pivotZeile, spalte);
            pivotZeile++;
            rang++;
        }

        return rang;
    }

    private Matrix kombiniere(Matrix andere, double faktor)
    {
        double[][] result = new double[zeilen][spalten];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < spalten; s++)
            {
                result[z][s] = werte[z][s] + faktor * andere.werte[z][s];
            }
        }
        return new Matrix(result);
    }

    private void pruefeGleicheDimension(Matrix andere, String operation)
    {
        if (andere == null)
        {
            throw new IllegalArgumentException("Zweite Matrix fehlt.");
        }

        if (zeilen != andere.zeilen || spalten != andere.spalten)
        {
            throw new IllegalArgumentException(operation + " braucht gleich große Matrizen.");
        }
    }

    private static void pruefeDimension(int zeilen, int spalten)
    {
        if (zeilen <= 0 || spalten <= 0)
        {
            throw new IllegalArgumentException("Matrixdimensionen müssen positiv sein.");
        }
    }

    private int findePivotZeile(double[][] matrix, int startZeile, int spalte)
    {
        int besteZeile = startZeile;
        double besterWert = Math.abs(matrix[startZeile][spalte]);
        for (int z = startZeile + 1; z < zeilen; z++)
        {
            double wert = Math.abs(matrix[z][spalte]);
            if (wert > besterWert)
            {
                besterWert = wert;
                besteZeile = z;
            }
        }
        return besteZeile;
    }

    private void tauscheZeilen(double[][] matrix, int ersteZeile, int zweiteZeile)
    {
        if (ersteZeile == zweiteZeile)
        {
            return;
        }

        double[] tmp = matrix[ersteZeile];
        matrix[ersteZeile] = matrix[zweiteZeile];
        matrix[zweiteZeile] = tmp;
    }

    private void eliminiereSpalte(double[][] matrix, int pivotZeile, int pivotSpalte)
    {
        double pivot = matrix[pivotZeile][pivotSpalte];
        for (int z = pivotZeile + 1; z < zeilen; z++)
        {
            double faktor = matrix[z][pivotSpalte] / pivot;
            if (Math.abs(faktor) <= RANG_TOLERANZ)
            {
                continue;
            }

            for (int s = pivotSpalte; s < spalten; s++)
            {
                matrix[z][s] -= faktor * matrix[pivotZeile][s];
                if (Math.abs(matrix[z][s]) <= RANG_TOLERANZ)
                {
                    matrix[z][s] = 0.0;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Matrix matrix)) return false;
        return zeilen == matrix.zeilen && spalten == matrix.spalten && Arrays.deepEquals(werte, matrix.werte);
    }

    @Override
    public int hashCode()
    {
        int result = 31 * zeilen + spalten;
        result = 31 * result + Arrays.deepHashCode(werte);
        return result;
    }
}
