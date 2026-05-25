package ui.shell;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.session.RechnerSession;
import ui.theme.ThemeType;

import java.util.List;

public record ShellSessionData(
        RechnerModus aktiverModus,
        String ausdruck,
        String verlauf,
        List<String> historyEintraege,
        WinkelModus winkelModus,
        double speicherWert,
        ThemeType themeType,
        ZahlenFormatModus zahlenFormatModus,
        int nachkommastellen)
{
    static ShellSessionData fromSession(RechnerSession session)
    {
        return new ShellSessionData(
                session.getAktiverModus(),
                session.getAusdruck(),
                session.getVerlauf(),
                session.getHistoryEintraege(),
                session.getWinkelModus(),
                session.getSpeicherWert(),
                session.getThemeType(),
                session.getZahlenFormatModus(),
                session.getNachkommastellen()
        );
    }

    RechnerSession toSession()
    {
        return new RechnerSession(
                RechnerSession.VERSION,
                aktiverModus,
                ausdruck,
                verlauf,
                historyEintraege,
                winkelModus,
                speicherWert,
                themeType,
                zahlenFormatModus,
                nachkommastellen
        );
    }
}
