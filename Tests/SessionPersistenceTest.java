import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ui.session.RechnerSession;
import ui.session.SessionPersistence;
import ui.theme.ThemeType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SessionPersistenceTest
{
    @TempDir
    Path tempDir;

    @Test
    void sessionPersistence_ShouldSaveAndLoadSession()
    {
        // Arrange
        Path sessionFile = tempDir.resolve("session.properties");
        SessionPersistence persistence = new SessionPersistence(sessionFile);
        RechnerSession session = new RechnerSession(
                RechnerSession.VERSION,
                RechnerModus.WISSENSCHAFTLICH,
                "2+3",
                "2+3 = 5",
                List.of("2+3 = 5", "6*7 = 42"),
                WinkelModus.RAD,
                12.5,
                ThemeType.CUSTOM,
                ZahlenFormatModus.WISSENSCHAFTLICH,
                7
        );

        // Act
        persistence.speichere(session);
        RechnerSession loaded = persistence.lade();

        // Assert
        assertEquals(RechnerSession.VERSION, loaded.getVersion());
        assertEquals(RechnerModus.WISSENSCHAFTLICH, loaded.getAktiverModus());
        assertEquals("2+3", loaded.getAusdruck());
        assertEquals("2+3 = 5", loaded.getVerlauf());
        assertEquals(List.of("2+3 = 5", "6*7 = 42"), loaded.getHistoryEintraege());
        assertEquals(WinkelModus.RAD, loaded.getWinkelModus());
        assertEquals(12.5, loaded.getSpeicherWert(), 1e-10);
        assertEquals(ThemeType.CUSTOM, loaded.getThemeType());
        assertEquals(ZahlenFormatModus.WISSENSCHAFTLICH, loaded.getZahlenFormatModus());
        assertEquals(7, loaded.getNachkommastellen());
    }

    @Test
    void sessionPersistence_ShouldUseDefaults_WhenVersionIsUnknown() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("unknown-version.properties");
        Files.writeString(sessionFile,
                "version=999\n"
                        + "aktiverModus=GRAPH\n"
                        + "ausdruck=should-not-load\n");
        SessionPersistence persistence = new SessionPersistence(sessionFile);

        // Act
        RechnerSession loaded = persistence.lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals("", loaded.getAusdruck());
        assertEquals(List.of(), loaded.getHistoryEintraege());
    }

    @Test
    void sessionPersistence_ShouldUseDefaults_WhenFileIsMissing()
    {
        // Arrange
        SessionPersistence persistence = new SessionPersistence(tempDir.resolve("missing-session.properties"));

        // Act
        RechnerSession loaded = persistence.lade();

        // Assert
        assertEquals(RechnerSession.VERSION, loaded.getVersion());
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals("", loaded.getAusdruck());
        assertEquals("", loaded.getVerlauf());
        assertEquals(List.of(), loaded.getHistoryEintraege());
        assertEquals(WinkelModus.DEG, loaded.getWinkelModus());
        assertEquals(ThemeType.DARK, loaded.getThemeType());
        assertEquals(ZahlenFormatModus.AUTO, loaded.getZahlenFormatModus());
    }

    @Test
    void sessionPersistence_ShouldFallbackPerField_WhenValuesAreBroken() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("broken-values.properties");
        Files.writeString(sessionFile,
                "version=1\n"
                        + "aktiverModus=NOPE\n"
                        + "winkelModus=RAD\n"
                        + "speicherWert=not-a-number\n"
                        + "theme=LIGHT\n"
                        + "zahlenFormat=kaputt\n"
                        + "nachkommastellen=999\n"
                        + "history.count=2\n"
                        + "history.0=2+3 = 5\n"
                        + "history.1=\n");
        SessionPersistence persistence = new SessionPersistence(sessionFile);

        // Act
        RechnerSession loaded = persistence.lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals(WinkelModus.RAD, loaded.getWinkelModus());
        assertEquals(0.0, loaded.getSpeicherWert(), 1e-10);
        assertEquals(ThemeType.LIGHT, loaded.getThemeType());
        assertEquals(ZahlenFormatModus.AUTO, loaded.getZahlenFormatModus());
        assertEquals(15, loaded.getNachkommastellen());
        assertEquals(List.of("2+3 = 5"), loaded.getHistoryEintraege());
    }
}
