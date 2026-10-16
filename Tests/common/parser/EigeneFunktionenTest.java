package common.parser;

import common.state.WinkelModus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EigeneFunktionenTest
{
    @TempDir
    Path ordner;

    @AfterEach
    void aufraeumen()
    {
        FunktionsRegistry.entferneAlleBenutzerfunktionen();
    }

    @Test
    void fuegeHinzu_ShouldPersistAndRegisterAgain_WhenLoadedAtStart()
    {
        // Arrange
        Path datei = ordner.resolve("unterordner/funktionen.txt");
        new EigeneFunktionen(datei).fuegeHinzu("Quad", "x^2+1");
        FunktionsRegistry.entferneAlleBenutzerfunktionen();

        // Act
        EigeneFunktionen geladen = new EigeneFunktionen(datei);

        // Assert
        assertEquals(Map.of("quad", "x^2+1"), geladen.alle());
        assertEquals(10.0, AusdruckParser.auswerten("quad(3)", 0, WinkelModus.RAD), 1e-10);
    }

    @Test
    void fuegeHinzu_ShouldThrowAndNotSave_WhenFunctionIsInvalid()
    {
        // Arrange
        Path datei = ordner.resolve("funktionen.txt");
        EigeneFunktionen funktionen = new EigeneFunktionen(datei);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> funktionen.fuegeHinzu("sin", "x"));
        assertThrows(IllegalArgumentException.class, () -> funktionen.fuegeHinzu("f", ""));
        assertTrue(funktionen.alle().isEmpty());
        assertFalse(Files.exists(datei));
    }

    @Test
    void entferne_ShouldUnregisterAndSave()
    {
        // Arrange
        Path datei = ordner.resolve("funktionen.txt");
        EigeneFunktionen funktionen = new EigeneFunktionen(datei);
        funktionen.fuegeHinzu("doppelt", "2x");

        // Act
        funktionen.entferne("Doppelt");

        // Assert
        assertTrue(new EigeneFunktionen(datei).alle().isEmpty());
        assertFalse(FunktionsRegistry.benutzerfunktionen().containsKey("doppelt"));
    }

    @Test
    void konstruktor_ShouldSkipBrokenLines() throws IOException
    {
        // Arrange
        Path datei = ordner.resolve("funktionen.txt");
        Files.writeString(datei, "kaputt\nsin;x\n;x\nplus;x+10\n");

        // Act
        EigeneFunktionen funktionen = new EigeneFunktionen(datei);

        // Assert
        assertEquals(Map.of("plus", "x+10"), funktionen.alle());
    }
}
