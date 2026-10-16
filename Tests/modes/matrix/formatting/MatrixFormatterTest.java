package modes.matrix.formatting;

import modes.matrix.formatting.MatrixFormatter;
import modes.matrix.logic.MatrixRechnerService;
import modes.matrix.model.Matrix;
import modes.matrix.model.RechenSchritt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatrixFormatterTest
{
    @Test
    void formatiere_ShouldRenderCompactMatrix()
    {
        MatrixFormatter formatter = new MatrixFormatter();
        Matrix matrix = new Matrix(new double[][]{{1, 2.5}, {0, -3}});

        assertEquals("[ 1  2,5 ]" + System.lineSeparator() + "[ 0  -3 ]", formatter.formatiere(matrix));
    }

    @Test
    void formatiereLgs_ShouldShowSolutionOrEchelonForm()
    {
        MatrixFormatter formatter = new MatrixFormatter();
        MatrixRechnerService service = new MatrixRechnerService();

        String eindeutig = formatter.formatiereLgs(service.loese(new Matrix(new double[][]{{2, 0}, {0, 4}}), new Matrix(new double[][]{{2}, {8}})));
        String keine = formatter.formatiereLgs(service.loese(new Matrix(new double[][]{{1, 1}, {1, 1}}), new Matrix(new double[][]{{1}, {2}})));

        assertTrue(eindeutig.startsWith("x1 = 1"), eindeutig);
        assertTrue(keine.startsWith("Stufenform (A|b):"), keine);
    }

    @Test
    void mitSchritten_ShouldAppendStepsOnlyWhenWantedAndPresent()
    {
        MatrixFormatter formatter = new MatrixFormatter();
        List<RechenSchritt> schritte = List.of(new RechenSchritt("Z1 tauschen", new Matrix(new double[][]{{1}})));

        assertEquals("E", formatter.mitSchritten("E", schritte, false));
        assertEquals("E", formatter.mitSchritten("E", List.of(), true));
        assertTrue(formatter.mitSchritten("E", schritte, true).contains("1. Z1 tauschen"));
    }
}
