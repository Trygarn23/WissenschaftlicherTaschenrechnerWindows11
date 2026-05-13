import modes.matrix.formatting.MatrixFormatter;
import modes.matrix.model.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixFormatterTest
{
    @Test
    void formatiere_ShouldRenderCompactMatrix()
    {
        MatrixFormatter formatter = new MatrixFormatter();
        Matrix matrix = new Matrix(new double[][]{{1, 2.5}, {0, -3}});

        assertEquals("[ 1  2,5 ]" + System.lineSeparator() + "[ 0  -3 ]", formatter.formatiere(matrix));
    }
}
