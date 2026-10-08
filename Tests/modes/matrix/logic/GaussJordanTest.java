package modes.matrix.logic;

import modes.matrix.model.InverseErgebnis;
import modes.matrix.model.LgsLoesung;
import modes.matrix.model.Matrix;
import modes.matrix.model.RechenSchritt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GaussJordanTest
{
    private final GaussJordan gaussJordan = new GaussJordan();

    @Test
    void invertiere_ShouldReturnInverse_WhenMatrixIsRegular()
    {
        // Arrange
        Matrix matrix = new Matrix(new double[][]{{4, 7}, {2, 6}});

        // Act
        Matrix inverse = gaussJordan.invertiere(matrix).inverse();

        // Assert
        assertMatrixNahe(new double[][]{{0.6, -0.7}, {-0.2, 0.4}}, inverse);
    }

    @Test
    void invertiere_ShouldGiveIdentity_WhenMultipliedWithOriginal()
    {
        // Arrange
        Matrix matrix = new Matrix(new double[][]{{0, 2, 1, 3}, {1, 1, 0, 2}, {4, 0, 1, 1}, {2, 3, 5, 0}});

        // Act
        Matrix inverse = gaussJordan.invertiere(matrix).inverse();

        // Assert
        double[][] einheit = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        assertMatrixNahe(einheit, matrix.multipliziere(inverse));
        assertMatrixNahe(einheit, inverse.multipliziere(matrix));
    }

    @Test
    void invertiere_ShouldThrowGermanMessage_WhenMatrixIsSingular()
    {
        // Arrange
        Matrix singulaer = new Matrix(new double[][]{{1, 2, 3}, {2, 4, 6}, {1, 0, 1}});

        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> gaussJordan.invertiere(singulaer));

        // Assert
        assertEquals("Die Matrix ist singulär und hat keine Inverse.", e.getMessage());
    }

    @Test
    void invertiere_ShouldRejectNonSquareMatrix()
    {
        // Arrange
        Matrix rechteckig = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> gaussJordan.invertiere(rechteckig));
    }

    @Test
    void invertiere_ShouldRecordSwapNormalizeAndEliminateSteps()
    {
        // Arrange: Pivot 0 oben erzwingt einen Zeilentausch
        Matrix matrix = new Matrix(new double[][]{{0, 1}, {2, 4}});

        // Act
        InverseErgebnis ergebnis = gaussJordan.invertiere(matrix);
        List<String> beschreibungen = ergebnis.schritte().stream().map(RechenSchritt::beschreibung).toList();

        // Assert
        assertEquals(List.of("Z1 ↔ Z2", "Z1 ← Z1 : 2", "Z1 ← Z1 − 2·Z2"), beschreibungen);
        assertMatrixNahe(new double[][]{{1, 2, 0, 0.5}, {0, 1, 1, 0}}, ergebnis.schritte().get(1).zwischenstand());
    }

    @Test
    void invertiere_ShouldUsePartialPivoting_WhenLargerPivotIsBelow()
    {
        // Arrange
        Matrix matrix = new Matrix(new double[][]{{1, 2}, {3, 4}});

        // Act
        RechenSchritt ersterSchritt = gaussJordan.invertiere(matrix).schritte().get(0);

        // Assert
        assertEquals("Z1 ↔ Z2", ersterSchritt.beschreibung());
    }

    @Test
    void zeilenSubtraktion_ShouldUsePlus_WhenFactorIsNegative()
    {
        // Arrange
        Matrix matrix = new Matrix(new double[][]{{2, 1}, {-1, 3}});

        // Act
        List<String> beschreibungen = gaussJordan.invertiere(matrix).schritte().stream().map(RechenSchritt::beschreibung).toList();

        // Assert
        assertTrue(beschreibungen.contains("Z2 ← Z2 + Z1"), beschreibungen.toString());
    }

    @Test
    void loese_ShouldReturnUniqueSolution_WhenRankIsFull()
    {
        // Arrange: x + y + z = 6, 2y + 5z = −4, 2x + 5y − z = 27
        Matrix a = new Matrix(new double[][]{{1, 1, 1}, {0, 2, 5}, {2, 5, -1}});
        Matrix b = new Matrix(new double[][]{{6}, {-4}, {27}});

        // Act
        LgsLoesung loesung = gaussJordan.loese(a, b);

        // Assert
        assertEquals(LgsLoesung.Art.EINDEUTIG, loesung.art());
        assertMatrixNahe(new double[][]{{5}, {3}, {-2}}, loesung.loesung());
        assertEquals("Das Gleichungssystem hat genau eine Lösung.", loesung.meldung());
    }

    @Test
    void loese_ShouldReportNoSolution_WhenRanksDiffer()
    {
        // Arrange
        Matrix a = new Matrix(new double[][]{{1, 2}, {2, 4}});
        Matrix b = new Matrix(new double[][]{{3}, {7}});

        // Act
        LgsLoesung loesung = gaussJordan.loese(a, b);

        // Assert
        assertEquals(LgsLoesung.Art.KEINE, loesung.art());
        assertNull(loesung.loesung());
        assertEquals(1, loesung.rangA());
        assertEquals(2, loesung.rangErweitert());
        assertEquals("Das Gleichungssystem hat keine Lösung (Rang A = 1 < Rang (A|b) = 2).", loesung.meldung());
    }

    @Test
    void loese_ShouldReportInfinitelyManySolutions_WhenRankIsBelowUnknowns()
    {
        // Arrange
        Matrix a = new Matrix(new double[][]{{1, 2}, {2, 4}});
        Matrix b = new Matrix(new double[][]{{3}, {6}});

        // Act
        LgsLoesung loesung = gaussJordan.loese(a, b);

        // Assert
        assertEquals(LgsLoesung.Art.UNENDLICH_VIELE, loesung.art());
        assertEquals(1, loesung.rangErweitert());
        assertMatrixNahe(new double[][]{{1, 2, 3}, {0, 0, 0}}, loesung.stufenform());
        assertTrue(loesung.meldung().contains("unendlich viele"));
    }

    @Test
    void loese_ShouldHandleRectangularSystems()
    {
        // Arrange: 3 Gleichungen, 2 Unbekannte, widerspruchsfrei
        Matrix a = new Matrix(new double[][]{{1, 0}, {0, 1}, {1, 1}});
        Matrix b = new Matrix(new double[][]{{2}, {3}, {5}});

        // Act
        LgsLoesung loesung = gaussJordan.loese(a, b);

        // Assert
        assertEquals(LgsLoesung.Art.EINDEUTIG, loesung.art());
        assertMatrixNahe(new double[][]{{2}, {3}}, loesung.loesung());
    }

    @Test
    void loese_ShouldRejectWrongVectorSize()
    {
        // Arrange
        Matrix a = new Matrix(new double[][]{{1, 0}, {0, 1}});
        Matrix b = new Matrix(new double[][]{{1}, {2}, {3}});

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> gaussJordan.loese(a, b));
    }

    @Test
    void serviceLoese_ShouldUseLastColumnOfB()
    {
        // Arrange
        Matrix a = new Matrix(new double[][]{{2, 0}, {0, 4}});
        Matrix b = new Matrix(new double[][]{{9, 4}, {9, 8}});

        // Act
        LgsLoesung loesung = new MatrixRechnerService().loese(a, b);

        // Assert
        assertMatrixNahe(new double[][]{{2}, {2}}, loesung.loesung());
    }

    private static void assertMatrixNahe(double[][] erwartet, Matrix ist)
    {
        assertEquals(erwartet.length, ist.getZeilen(), "Zeilen");
        assertEquals(erwartet[0].length, ist.getSpalten(), "Spalten");
        for (int z = 0; z < erwartet.length; z++)
        {
            for (int s = 0; s < erwartet[z].length; s++)
            {
                assertEquals(erwartet[z][s], ist.get(z, s), 1e-9, "Position " + (z + 1) + "," + (s + 1));
            }
        }
    }
}
