package ui.session;

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

    @Test
    void sessionPersistence_ShouldUseDefaults_WhenFileIsEmpty() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("empty.properties");
        Files.writeString(sessionFile, "");

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals(List.of(), loaded.getHistoryEintraege());
    }

    @Test
    void sessionPersistence_ShouldNotCrash_WhenFileIsBinaryGarbage() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("garbage.properties");
        Files.write(sessionFile, new byte[]{(byte) 0xFF, (byte) 0xFE, 0x00, (byte) 0x80, (byte) 0xC3, 0x28, 0x01, 0x02});

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals("", loaded.getAusdruck());
    }

    @Test
    void sessionPersistence_ShouldNotCrash_WhenFileHasBrokenUnicodeEscape() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("text-garbage.properties");
        Files.writeString(sessionFile, "version=1\nausdruck=abc \\u12");

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
    }

    @Test
    void sessionPersistence_ShouldKeepReadableFields_WhenFileIsTruncated() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("truncated.properties");
        Files.writeString(sessionFile,
                "version=1\n"
                        + "aktiverModus=GRAPH\n"
                        + "ausdruck=2+\n"
                        + "history.count=5\n"
                        + "history.0=2+3 = 5\n"
                        + "history.1=6*");

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.GRAPH, loaded.getAktiverModus());
        assertEquals("2+", loaded.getAusdruck());
        assertEquals(List.of("2+3 = 5", "6*"), loaded.getHistoryEintraege());
        assertEquals(WinkelModus.DEG, loaded.getWinkelModus());
    }

    @Test
    void sessionPersistence_ShouldUseDefaults_WhenVersionIsMissing() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("no-version.properties");
        Files.writeString(sessionFile, "aktiverModus=GRAPH\nausdruck=alt\n");

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals("", loaded.getAusdruck());
    }

    @Test
    void sessionPersistence_ShouldUseDefaultsForMissingFields_WhenOnlyVersionIsPresent() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("only-version.properties");
        Files.writeString(sessionFile, "version=1\n");

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals(ThemeType.DARK, loaded.getThemeType());
        assertEquals(WinkelModus.DEG, loaded.getWinkelModus());
        assertEquals(List.of(), loaded.getHistoryEintraege());
    }

    @Test
    void sessionPersistence_ShouldUseDefaults_WhenModeIsUnknownOrCountIsBroken() throws Exception
    {
        // Arrange
        Path sessionFile = tempDir.resolve("unknown-mode.properties");
        Files.writeString(sessionFile,
                "version=1\naktiverModus=ZUKUNFTSMODUS\nhistory.count=-3\nhistory.0=x\nspeicherWert=NaN\n");

        // Act
        RechnerSession loaded = new SessionPersistence(sessionFile).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
        assertEquals(List.of(), loaded.getHistoryEintraege());
        assertEquals(0.0, loaded.getSpeicherWert(), 1e-10);
    }

    @Test
    void sessionPersistence_ShouldUseDefaults_WhenPathIsADirectory() throws Exception
    {
        // Arrange
        Path ordner = Files.createDirectory(tempDir.resolve("ist-ordner"));

        // Act
        RechnerSession loaded = new SessionPersistence(ordner).lade();

        // Assert
        assertEquals(RechnerModus.STANDARD, loaded.getAktiverModus());
    }
}
