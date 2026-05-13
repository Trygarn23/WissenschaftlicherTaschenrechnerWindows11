import modes.matrix.model.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest
{
    @Test
    void matrix_ShouldDefensivelyCopyValues_WhenCreatedAndExposed()
    {
        double[][] values = {{1, 2}, {3, 4}};
        Matrix matrix = new Matrix(values);

        values[0][0] = 99;
        double[][] copy = matrix.toArray();
        copy[0][1] = 99;

        assertEquals(1, matrix.get(0, 0));
        assertEquals(2, matrix.get(0, 1));
    }

    @Test
    void matrix_ShouldRejectInvalidDimensions()
    {
        assertThrows(IllegalArgumentException.class, () -> new Matrix(new double[][]{}));
        assertThrows(IllegalArgumentException.class, () -> new Matrix(new double[][]{{1}, {1, 2}}));
    }

    @Test
    void addiereUndSubtrahiere_ShouldCalculateElementWise_WhenDimensionsMatch()
    {
        Matrix a = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix b = new Matrix(new double[][]{{5, 6}, {7, 8}});

        assertEquals(new Matrix(new double[][]{{6, 8}, {10, 12}}), a.addiere(b));
        assertEquals(new Matrix(new double[][]{{-4, -4}, {-4, -4}}), a.subtrahiere(b));
    }

    @Test
    void matrixOperations_ShouldRejectDimensionMismatch()
    {
        Matrix a = new Matrix(new double[][]{{1, 2}});
        Matrix b = new Matrix(new double[][]{{1, 2}, {3, 4}});

        assertThrows(IllegalArgumentException.class, () -> a.addiere(b));
        assertThrows(IllegalArgumentException.class, () -> a.subtrahiere(b));
        assertThrows(IllegalArgumentException.class, () -> a.multipliziere(a));
    }

    @Test
    void skalarMultiplizieren_ShouldMultiplyEveryCell()
    {
        Matrix matrix = new Matrix(new double[][]{{1, -2}, {3, 0.5}});

        assertEquals(new Matrix(new double[][]{{2, -4}, {6, 1}}), matrix.skalarMultiplizieren(2));
    }

    @Test
    void multipliziere_ShouldUseMatrixMultiplication_WhenDimensionsMatch()
    {
        Matrix a = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
        Matrix b = new Matrix(new double[][]{{7, 8}, {9, 10}, {11, 12}});

        assertEquals(new Matrix(new double[][]{{58, 64}, {139, 154}}), a.multipliziere(b));
    }

    @Test
    void determinante_ShouldCalculateTwoByTwoAndThreeByThree()
    {
        Matrix twoByTwo = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix threeByThree = new Matrix(new double[][]{{6, 1, 1}, {4, -2, 5}, {2, 8, 7}});

        assertEquals(-2, twoByTwo.determinante(), 1e-10);
        assertEquals(-306, threeByThree.determinante(), 1e-10);
    }
}
