package ui.shell;

import common.formatting.ZahlenFormatModus;
import common.history.DateiVerlaufRepository;
import common.history.VerlaufEintrag;
import common.history.VerlaufService;
import common.state.RechnerModus;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ui.session.SessionPersistence;
import ui.settings.AppSettings;
import ui.settings.SettingsPersistence;
import ui.theme.ThemeType;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShellPersistenceServiceTest
{
    @TempDir
    Path tempDir;

    @Test
    void settings_ShouldLoadSaveAndUpdateWindowSize()
    {
        ShellPersistenceService service = createService();
        AppSettings settings = new AppSettings();
        settings.setThemeType(ThemeType.MATRIX);
        settings.setStartModus(RechnerModus.STATISTIK);

        service.speichereSettings(settings);
        AppSettings loaded = service.ladeSettings();

        assertEquals(ThemeType.MATRIX, loaded.getThemeType());
        assertEquals(RechnerModus.STATISTIK, loaded.getStartModus());

        service.speichereFenstergroesse(loaded, 1440, 900);
        AppSettings resized = service.ladeSettings();

        assertEquals(1440, resized.getFensterBreite());
        assertEquals(900, resized.getFensterHoehe());
    }

    @Test
    void verlauf_ShouldRespectHistorySetting()
    {
        ShellPersistenceService service = createService();
        AppSettings settings = new AppSettings();
        VerlaufEintrag entry = service.erstelleVerlaufEintrag("1 + 1 = 2", RechnerModus.STANDARD);

        service.speichereVerlauf(settings, List.of(entry));
        assertEquals(1, service.ladeVerlauf(settings).size());

        settings.setHistoryEnabled(false);
        service.speichereVerlauf(settings, List.of(service.erstelleVerlaufEintrag("2 + 2 = 4", RechnerModus.STANDARD)));

        assertTrue(service.ladeVerlauf(settings).isEmpty());

        settings.setHistoryEnabled(true);
        List<VerlaufEintrag> loaded = service.ladeVerlauf(settings);

        assertEquals(1, loaded.size());
        assertEquals("1 + 1", loaded.getFirst().getAusdruck());
        assertEquals("2", loaded.getFirst().getErgebnis());
    }

    @Test
    void session_ShouldRoundtripShellSessionData()
    {
        ShellPersistenceService service = createService();
        ShellSessionData sessionData = new ShellSessionData(
                RechnerModus.PROGRAMMIERER,
                "FF",
                "HEX: FF",
                List.of("15 + 1 = 16"),
                WinkelModus.RAD,
                42.5,
                ThemeType.NEON,
                ZahlenFormatModus.WISSENSCHAFTLICH,
                7
        );

        service.speichereSession(sessionData);
        ShellSessionData loaded = service.ladeSession();

        assertEquals(RechnerModus.PROGRAMMIERER, loaded.aktiverModus());
        assertEquals("FF", loaded.ausdruck());
        assertEquals("HEX: FF", loaded.verlauf());
        assertEquals(List.of("15 + 1 = 16"), loaded.historyEintraege());
        assertEquals(WinkelModus.RAD, loaded.winkelModus());
        assertEquals(42.5, loaded.speicherWert());
        assertEquals(ThemeType.NEON, loaded.themeType());
        assertEquals(ZahlenFormatModus.WISSENSCHAFTLICH, loaded.zahlenFormatModus());
        assertEquals(7, loaded.nachkommastellen());
    }

    private ShellPersistenceService createService()
    {
        return new ShellPersistenceService(
                new SettingsPersistence(tempDir.resolve("settings.properties")),
                new SessionPersistence(tempDir.resolve("session.properties")),
                new VerlaufService(new DateiVerlaufRepository(tempDir.resolve("history.txt")))
        );
    }
}
