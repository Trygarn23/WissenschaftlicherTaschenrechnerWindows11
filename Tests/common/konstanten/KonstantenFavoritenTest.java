package common.konstanten;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KonstantenFavoritenTest
{
    @TempDir
    Path ordner;

    @Test
    void umschalten_ShouldPersistFavoritesAcrossInstances()
    {
        // Arrange
        Path datei = ordner.resolve("unterordner/favoriten.txt");
        KonstantenFavoriten favoriten = new KonstantenFavoriten(datei);

        // Act
        favoriten.umschalten("Lichtgeschwindigkeit");
        favoriten.umschalten("Planck-Konstante");
        favoriten.umschalten("Lichtgeschwindigkeit");
        KonstantenFavoriten geladen = new KonstantenFavoriten(datei);

        // Assert
        assertEquals(Set.of("Planck-Konstante"), geladen.namen());
        assertTrue(geladen.istFavorit("Planck-Konstante"));
        assertFalse(geladen.istFavorit("Lichtgeschwindigkeit"));
    }

    @Test
    void konstruktor_ShouldStartEmpty_WhenFileIsMissingOrBroken() throws IOException
    {
        // Arrange
        Path kaputt = ordner.resolve("kaputt.txt");
        Files.write(kaputt, new byte[]{(byte) 0xC3, (byte) 0x28, (byte) 0xFF});
        Path ordnerStattDatei = Files.createDirectory(ordner.resolve("ordner.txt"));

        // Act & Assert
        assertTrue(new KonstantenFavoriten(ordner.resolve("fehlt.txt")).namen().isEmpty());
        assertTrue(new KonstantenFavoriten(kaputt).namen().isEmpty());
        assertTrue(new KonstantenFavoriten(ordnerStattDatei).namen().isEmpty());
    }

    @Test
    void konstruktor_ShouldIgnoreBlankLinesAndSurroundingSpaces() throws IOException
    {
        // Arrange
        Path datei = ordner.resolve("favoriten.txt");
        Files.writeString(datei, "\n  Elementarladung  \n\n");

        // Act
        KonstantenFavoriten favoriten = new KonstantenFavoriten(datei);

        // Assert
        assertEquals(Set.of("Elementarladung"), favoriten.namen());
    }
}
