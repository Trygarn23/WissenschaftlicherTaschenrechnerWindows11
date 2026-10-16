package common.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DateiPersistenzTest
{
    @TempDir
    Path tempDir;

    @Test
    void ladeProperties_ShouldReturnEmpty_WhenFileIsMissingOrBroken() throws Exception
    {
        // Arrange
        Path kaputt = tempDir.resolve("kaputt.properties");
        Files.write(kaputt, new byte[]{(byte) 0xC3, 0x28});
        Path escape = tempDir.resolve("escape.properties");
        Files.writeString(escape, "a=\\u12");

        // Act & Assert
        assertTrue(DateiPersistenz.ladeProperties(tempDir.resolve("fehlt.properties")).isEmpty());
        assertTrue(DateiPersistenz.ladeProperties(kaputt).isEmpty());
        assertTrue(DateiPersistenz.ladeProperties(escape).isEmpty());
    }

    @Test
    void speichereUndLadeProperties_ShouldRoundTripAndCreateParentFolders()
    {
        // Arrange
        Path datei = tempDir.resolve("a").resolve("b").resolve("x.properties");
        Properties properties = new Properties();
        properties.setProperty("schluessel", "Wert mit Ümlaut = und Gleichheitszeichen");

        // Act
        DateiPersistenz.speichereProperties(datei, properties, "Test");
        Properties geladen = DateiPersistenz.ladeProperties(datei).orElseThrow();

        // Assert
        assertEquals("Wert mit Ümlaut = und Gleichheitszeichen", geladen.getProperty("schluessel"));
    }

    @Test
    void speichereUndLadeZeilen_ShouldRoundTrip_AndReturnEmptyListWhenMissing()
    {
        // Arrange
        Path datei = tempDir.resolve("zeilen").resolve("z.txt");

        // Act
        DateiPersistenz.speichereZeilen(datei, List.of("eins", "zwei ü"));

        // Assert
        assertEquals(List.of("eins", "zwei ü"), DateiPersistenz.ladeZeilen(datei));
        assertEquals(List.of(), DateiPersistenz.ladeZeilen(tempDir.resolve("fehlt.txt")));
    }

    @Test
    void leseWerte_ShouldFallBack_WhenValuesAreMissingOrInvalid()
    {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty("zahl", "abc");
        properties.setProperty("kommazahl", "Infinity");
        properties.setProperty("enum", "NIX");

        // Act & Assert
        assertEquals(7, DateiPersistenz.leseInt(properties, "zahl", 7));
        assertEquals(7, DateiPersistenz.leseInt(properties, "fehlt", 7));
        assertEquals(1.5, DateiPersistenz.leseDouble(properties, "kommazahl", 1.5), 0.0);
        assertEquals(Thread.State.NEW, DateiPersistenz.leseEnum(properties, "enum", Thread.State.class, Thread.State.NEW));
    }

    @Test
    void leseWerte_ShouldParseValidValues()
    {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty("zahl", "42");
        properties.setProperty("kommazahl", "2.5");
        properties.setProperty("enum", "RUNNABLE");

        // Act & Assert
        assertEquals(42, DateiPersistenz.leseInt(properties, "zahl", 0));
        assertEquals(2.5, DateiPersistenz.leseDouble(properties, "kommazahl", 0), 0.0);
        assertEquals(Thread.State.RUNNABLE, DateiPersistenz.leseEnum(properties, "enum", Thread.State.class, Thread.State.NEW));
    }
}
