package common.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppDateienTest
{
    @TempDir
    Path home;

    @Test
    void datei_ShouldMoveOldFileIntoAppFolderOnce() throws Exception
    {
        // Arrange
        Path alt = home.resolve(".wissenschaftlicher_taschenrechner_settings.properties");
        Files.writeString(alt, "theme=NEON");

        // Act
        Path datei = AppDateien.datei(home, "settings.properties");

        // Assert
        assertEquals(home.resolve(".wissenschaftlicher_taschenrechner").resolve("settings.properties"), datei);
        assertEquals("theme=NEON", Files.readString(datei));
        assertFalse(Files.exists(alt));
    }

    @Test
    void datei_ShouldKeepNewFile_WhenBothExist() throws Exception
    {
        // Arrange
        Path neu = home.resolve(".wissenschaftlicher_taschenrechner").resolve("history.txt");
        Files.createDirectories(neu.getParent());
        Files.writeString(neu, "neu");
        Files.writeString(home.resolve(".wissenschaftlicher_taschenrechner_history.txt"), "alt");

        // Act
        Path datei = AppDateien.datei(home, "history.txt");

        // Assert
        assertEquals("neu", Files.readString(datei));
    }

    @Test
    void datei_ShouldReturnNewPath_WhenNothingExistsYet()
    {
        // Act & Assert
        assertEquals(home.resolve(".wissenschaftlicher_taschenrechner").resolve("session.properties"),
                AppDateien.datei(home, "session.properties"));
    }
}
