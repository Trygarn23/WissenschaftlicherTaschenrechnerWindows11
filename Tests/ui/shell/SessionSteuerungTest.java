package ui.shell;

import common.formatting.ZahlenFormatModus;
import common.logic.RechnerService;
import common.state.RechnerModus;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import ui.settings.AppSettings;
import ui.theme.ThemeType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionSteuerungTest
{
    @Test
    void schnappschussUndUebernimm_ShouldRestoreCalculatorAndSettings()
    {
        RechnerService quelle = new RechnerService();
        quelle.setWinkelModus(WinkelModus.RAD);
        quelle.setZahlenFormatModus(ZahlenFormatModus.WISSENSCHAFTLICH);
        quelle.setNachkommastellen(4);
        quelle.setAusdruckText("2+3");
        quelle.setVerlauf("1+1 = 2");
        quelle.setSpeicherWert(7.5);

        ShellSessionData session = SessionSteuerung.schnappschuss(quelle, RechnerModus.GRAPH, List.of("1+1 = 2"), ThemeType.MATRIX);

        RechnerService ziel = new RechnerService();
        AppSettings settings = new AppSettings();
        SessionSteuerung.uebernimm(session, settings, ziel);

        assertEquals("2+3", ziel.getAusdruckText());
        assertEquals("1+1 = 2", ziel.getVerlauf());
        assertEquals(7.5, ziel.getSpeicherWert());
        assertEquals(WinkelModus.RAD, ziel.getWinkelModus());
        assertEquals(ZahlenFormatModus.WISSENSCHAFTLICH, ziel.getZahlenFormatModus());
        assertEquals(4, ziel.getNachkommastellen());

        assertEquals(ThemeType.MATRIX, settings.getThemeType());
        assertEquals(RechnerModus.GRAPH, settings.getStartModus());
        assertEquals(WinkelModus.RAD, settings.getWinkelModus());
        assertEquals(ZahlenFormatModus.WISSENSCHAFTLICH, settings.getZahlenFormatModus());
        assertEquals(4, settings.getNachkommastellen());
        assertEquals(List.of("1+1 = 2"), session.historyEintraege());
    }
}
