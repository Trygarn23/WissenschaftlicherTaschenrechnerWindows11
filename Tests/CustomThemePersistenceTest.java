import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ui.theme.custom.CustomThemeColors;
import ui.theme.custom.CustomThemePersistence;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CustomThemePersistenceTest
{
    @TempDir
    Path tempDir;

    @Test
    void customThemePersistence_ShouldSaveAndLoadColors()
    {
        // Arrange
        Path themeFile = tempDir.resolve("custom-theme.properties");
        CustomThemePersistence persistence = new CustomThemePersistence(themeFile);
        CustomThemeColors colors = new CustomThemeColors(
                new Color(1, 2, 3),
                new Color(4, 5, 6),
                new Color(7, 8, 9),
                new Color(10, 11, 12),
                new Color(13, 14, 15),
                new Color(16, 17, 18),
                new Color(19, 20, 21)
        );

        // Act
        persistence.speichere(colors);
        CustomThemeColors loaded = persistence.lade();

        // Assert
        assertEquals(colors, loaded);
    }

    @Test
    void customThemePersistence_ShouldUseDefaults_WhenFileIsMissing()
    {
        // Arrange
        CustomThemePersistence persistence = new CustomThemePersistence(tempDir.resolve("missing.properties"));

        // Act
        CustomThemeColors loaded = persistence.lade();

        // Assert
        assertEquals(CustomThemeColors.DEFAULT, loaded);
    }

    @Test
    void customThemePersistence_ShouldFallbackPerColor_WhenValueIsInvalid() throws Exception
    {
        // Arrange
        Path themeFile = tempDir.resolve("invalid.properties");
        Files.writeString(themeFile,
                "panelBackground=#010203\n"
                        + "displayBackground=nope\n"
                        + "displayForeground=#070809\n");
        CustomThemePersistence persistence = new CustomThemePersistence(themeFile);

        // Act
        CustomThemeColors loaded = persistence.lade();

        // Assert
        assertEquals(new Color(1, 2, 3), loaded.panelBackground());
        assertEquals(CustomThemeColors.DEFAULT.displayBackground(), loaded.displayBackground());
        assertEquals(new Color(7, 8, 9), loaded.displayForeground());
    }
}
