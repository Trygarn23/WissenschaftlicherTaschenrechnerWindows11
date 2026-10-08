package modes.matrix.formatting;

import modes.matrix.model.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixCsvTest
{
    @Test
    void schreibe_ShouldUseSemicolonAndDecimalComma()
    {
        // Arrange
        Matrix matrix = new Matrix(new double[][]{{1, 2.5}, {-0.125, 1234567}});

        // Act
        String csv = MatrixCsv.schreibe(matrix);

        // Assert
        String nl = System.lineSeparator();
        assertEquals("1;2,5" + nl + "-0,125;1234567" + nl, csv);
    }

    @Test
    void schreibeUndLese_ShouldRoundTripWithoutLoss()
    {
        // Arrange
        Matrix matrix = new Matrix(new double[][]{{1.0 / 3, -2}, {0, 1e-12}, {7.75, 100}});

        // Act
        Matrix gelesen = MatrixCsv.lese(MatrixCsv.schreibe(matrix));

        // Assert
        assertEquals(matrix, gelesen);
    }

    @Test
    void lese_ShouldIgnoreBlankLinesBomAndSpaces()
    {
        // Arrange
        String csv = "﻿\n 1 ; 2,5 \r\n\r\n3;4\n\n";

        // Act
        Matrix matrix = MatrixCsv.lese(csv);

        // Assert
        assertEquals(new Matrix(new double[][]{{1, 2.5}, {3, 4}}), matrix);
    }

    @Test
    void lese_ShouldRejectRowsOfDifferentLength()
    {
        // Arrange
        String csv = "1;2;3\n4;5\n";

        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MatrixCsv.lese(csv));

        // Assert
        assertEquals("Zeile 2 hat 2 Werte, erwartet sind 3.", e.getMessage());
    }

    @Test
    void lese_ShouldNameCell_WhenValueIsInvalid()
    {
        // Arrange
        String csv = "1;2\n3;abc\n";

        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MatrixCsv.lese(csv));

        // Assert
        assertTrue(e.getMessage().startsWith("Zeile 2, Spalte 2:"), e.getMessage());
    }

    @Test
    void lese_ShouldRejectEmptyText()
    {
        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MatrixCsv.lese("\n  \n"));

        // Assert
        assertEquals("Die CSV-Datei enthält keine Werte.", e.getMessage());
    }
}
