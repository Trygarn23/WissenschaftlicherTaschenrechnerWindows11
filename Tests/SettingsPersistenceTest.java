import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ui.settings.AppSettings;
import ui.settings.SettingsPersistence;
import ui.theme.ThemeType;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsPersistenceTest
{
    @TempDir
    Path tempDir;

    @Test
    void settingsPersistence_ShouldSaveAndLoadSettings()
    {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.properties");
        SettingsPersistence persistence = new SettingsPersistence(settingsFile);
        AppSettings settings = new AppSettings();
        settings.setThemeType(ThemeType.NEON);
        settings.setStartModus(RechnerModus.PROGRAMMIERER);
        settings.setWinkelModus(WinkelModus.RAD);
        settings.setHistoryEnabled(false);
        settings.setNachkommastellen(7);
        settings.setZahlenFormatModus(ZahlenFormatModus.WISSENSCHAFTLICH);
        settings.setFensterBreite(1400);
        settings.setFensterHoehe(900);

        // Act
        persistence.speichere(settings);
        AppSettings loaded = persistence.lade();

        // Assert
        assertEquals(ThemeType.NEON, loaded.getThemeType());
        assertEquals(RechnerModus.PROGRAMMIERER, loaded.getStartModus());
        assertEquals(WinkelModus.RAD, loaded.getWinkelModus());
        assertFalse(loaded.isHistoryEnabled());
        assertEquals(7, loaded.getNachkommastellen());
        assertEquals(ZahlenFormatModus.WISSENSCHAFTLICH, loaded.getZahlenFormatModus());
        assertEquals(1400, loaded.getFensterBreite());
        assertEquals(900, loaded.getFensterHoehe());
    }
}
