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
}
