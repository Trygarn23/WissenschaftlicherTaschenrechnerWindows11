package ui.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LetzteEingabeSicherungTest
{
    @TempDir
    Path tempDir;

    @Test
    void merke_ShouldLeaveInputForNextStart_WhenAppIsNotClosedNormally()
    {
        // Arrange
        Path datei = tempDir.resolve("unterordner").resolve("letzte_eingabe.txt");
        LetzteEingabeSicherung sicherung = new LetzteEingabeSicherung(datei);

        // Act
        sicherung.merke("12×(3+4");

        // Assert: ein „neuer Start“ findet die Eingabe
        assertEquals(Optional.of("12×(3+4"), new LetzteEingabeSicherung(datei).ladeUebrigeEingabe());
    }

    @Test
    void loesche_ShouldRemoveFile_WhenClosedNormally()
    {
        // Arrange
        Path datei = tempDir.resolve("letzte_eingabe.txt");
        LetzteEingabeSicherung sicherung = new LetzteEingabeSicherung(datei);
        sicherung.merke("1+1");

        // Act
        sicherung.loesche();

        // Assert
        assertFalse(Files.exists(datei));
        assertEquals(Optional.empty(), sicherung.ladeUebrigeEingabe());
    }

    @Test
    void ladeUebrigeEingabe_ShouldIgnoreEmptyFile() throws Exception
    {
        // Arrange
        Path datei = tempDir.resolve("letzte_eingabe.txt");
        Files.writeString(datei, "  \n");

        // Act & Assert
        assertEquals(Optional.empty(), new LetzteEingabeSicherung(datei).ladeUebrigeEingabe());
    }
}
