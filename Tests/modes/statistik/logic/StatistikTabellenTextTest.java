package modes.statistik.logic;

import modes.statistik.model.StatistikDatenpunkt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatistikTabellenTextTest
{
    private final StatistikTabellenText tabellenText = new StatistikTabellenText();

    @Test
    void alsTabellenText_ShouldUseTabsDecimalCommaAndHeader()
    {
        // Arrange
        List<StatistikDatenpunkt> daten = List.of(
                new StatistikDatenpunkt(1, 2.5, 1),
                new StatistikDatenpunkt(2, -3000, 0.5));

        // Act
        String text = tabellenText.alsTabellenText(daten);

        // Assert
        assertEquals("x\ty\tGewicht\n1\t2,5\t1\n2\t-3000\t0,5", text);
    }

    @Test
    void liesCsv_ShouldReadSemicolonsWithHeaderAndDecimalComma()
    {
        // Act
        List<StatistikDatenpunkt> daten = tabellenText.liesCsv("x;y\n1;2,5\n2;3,5\n");

        // Assert
        assertEquals(List.of(new StatistikDatenpunkt(1, 2.5, 1), new StatistikDatenpunkt(2, 3.5, 1)), daten);
    }

    @Test
    void liesCsv_ShouldReadTabsWithDecimalPointAndWeight()
    {
        // Act
        List<StatistikDatenpunkt> daten = tabellenText.liesCsv("1\t2.5\t0.5\r\n2\t3\t2");

        // Assert
        assertEquals(List.of(new StatistikDatenpunkt(1, 2.5, 0.5), new StatistikDatenpunkt(2, 3, 2)), daten);
    }

    @Test
    void liesCsv_ShouldNumberSingleColumnAndSkipBomAndEmptyLines()
    {
        // Act
        List<StatistikDatenpunkt> daten = tabellenText.liesCsv("﻿Messwert;\n\n4,5;\n7;\n;;\n");

        // Assert
        assertEquals(List.of(new StatistikDatenpunkt(1, 4.5, 1), new StatistikDatenpunkt(2, 7, 1)), daten);
    }

    @Test
    void liesCsv_ShouldReadCopiedTableTextBack()
    {
        // Arrange
        List<StatistikDatenpunkt> original = List.of(
                new StatistikDatenpunkt(1, 0.125, 1),
                new StatistikDatenpunkt(-2, 1e-7, 3));

        // Act
        List<StatistikDatenpunkt> gelesen = tabellenText.liesCsv(tabellenText.alsTabellenText(original));

        // Assert
        assertEquals(original, gelesen);
    }

    @Test
    void liesCsv_ShouldNameLineOfInvalidValue()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> tabellenText.liesCsv("1;2\n1;abc"));

        // Assert
        assertEquals("Zeile 2: „abc“ ist keine gültige Zahl.", fehler.getMessage());
    }

    @Test
    void liesCsv_ShouldRejectEmptyFileAndTooManyColumns()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class,
                () -> tabellenText.liesCsv("x;y\n"));
        IllegalArgumentException spalten = assertThrows(IllegalArgumentException.class,
                () -> tabellenText.liesCsv("1;2;3;4"));

        // Assert
        assertEquals("Die Datei enthält keine Daten.", leer.getMessage());
        assertTrue(spalten.getMessage().startsWith("Zeile 1: Höchstens 3 Spalten"));
    }
}
