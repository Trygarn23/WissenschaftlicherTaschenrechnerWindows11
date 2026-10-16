package modes.wissenschaftlich.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

public class VariablenSpeicherTest
{
    @TempDir
    Path ordner;

    @Test
    void speichere_ShouldPersistValuesAcrossInstances()
    {
        // Arrange
        Path datei = ordner.resolve("unterordner/variablen.properties");
        VariablenSpeicher speicher = new VariablenSpeicher(datei);

        // Act
        speicher.speichere("A", 1.0 / 3);
        speicher.speichere("F", -42.5);
        VariablenSpeicher geladen = new VariablenSpeicher(datei);

        // Assert
        assertEquals(OptionalDouble.of(1.0 / 3), geladen.wert("A"));
        assertEquals(OptionalDouble.of(-42.5), geladen.wert("F"));
        assertTrue(geladen.wert("B").isEmpty());
    }

    @Test
    void speichere_ShouldRejectUnknownNameOrInvalidValue()
    {
        // Arrange
        VariablenSpeicher speicher = new VariablenSpeicher(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> speicher.speichere("G", 1));
        assertThrows(IllegalArgumentException.class, () -> speicher.speichere("A", Double.NaN));
        assertTrue(speicher.wert("A").isEmpty());
    }

    @Test
    void konstruktor_ShouldIgnoreBrokenValues_WhenFileIsCorrupt() throws IOException
    {
        // Arrange
        Path datei = ordner.resolve("variablen.properties");
        Files.writeString(datei, "A=kaputt\nB=2.5\n");

        // Act
        VariablenSpeicher speicher = new VariablenSpeicher(datei);

        // Assert
        assertTrue(speicher.wert("A").isEmpty());
        assertEquals(OptionalDouble.of(2.5), speicher.wert("B"));
    }
}
