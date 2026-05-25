import modes.matrix.logic.MatrixRechnerService;
import modes.matrix.model.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixRechnerServiceTest
{
    @Test
    void matrixRechnerService_ShouldExposeTransposeTraceAndRank()
    {
        MatrixRechnerService service = new MatrixRechnerService();
        Matrix matrix = new Matrix(new double[][]{{1, 2}, {3, 4}});

        assertEquals(new Matrix(new double[][]{{1, 3}, {2, 4}}), service.transponiere(matrix));
        assertEquals(5, service.spur(matrix), 1e-10);
        assertEquals(2, service.rang(matrix));
    }
}
