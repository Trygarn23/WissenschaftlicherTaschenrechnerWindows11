package modes.matrix.logic;

import modes.matrix.model.Matrix;

public class MatrixRechnerService
{
    public Matrix addiere(Matrix a, Matrix b)
    {
        return a.addiere(b);
    }

    public Matrix subtrahiere(Matrix a, Matrix b)
    {
        return a.subtrahiere(b);
    }

    public Matrix skalarMultiplizieren(Matrix matrix, double skalar)
    {
        return matrix.skalarMultiplizieren(skalar);
    }

    public Matrix multipliziere(Matrix a, Matrix b)
    {
        return a.multipliziere(b);
    }

    public double determinante(Matrix matrix)
    {
        return matrix.determinante();
    }

    public Matrix transponiere(Matrix matrix)
    {
        return matrix.transponiere();
    }

    public double spur(Matrix matrix)
    {
        return matrix.spur();
    }

    public int rang(Matrix matrix)
    {
        return matrix.rang();
    }
}
