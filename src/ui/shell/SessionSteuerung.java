package ui.shell;

import common.logic.RechnerService;
import common.state.RechnerModus;
import ui.history.HistoryPanel;
import ui.settings.AppSettings;
import ui.theme.ThemeType;

import java.util.List;

/**
 * Sitzung speichern/laden: sammelt den Zustand aus Rechner und Verlauf ein bzw. spielt ihn zurück.
 * Theme und Modus setzt die Shell nach dem Laden selbst, weil daran Layout und Prüfungsmodus hängen.
 */
final class SessionSteuerung
{
    private final ShellPersistenceService persistenceService;
    private final RechnerService rechner;
    private final HistoryPanel historyPanel;

    SessionSteuerung(ShellPersistenceService persistenceService, RechnerService rechner, HistoryPanel historyPanel)
    {
        this.persistenceService = persistenceService;
        this.rechner = rechner;
        this.historyPanel = historyPanel;
    }

    void speichere(RechnerModus aktuellerModus, ThemeType themeType)
    {
        persistenceService.speichereSession(schnappschuss(rechner, aktuellerModus, historyPanel.getAllEntries(), themeType));
    }

    /** Lädt die gespeicherte Sitzung in Settings (werden gleich gespeichert), Rechner und Verlauf. */
    ShellSessionData lade(AppSettings settings)
    {
        ShellSessionData session = persistenceService.ladeSession();
        uebernimm(session, settings, rechner);
        persistenceService.speichereSettings(settings);
        historyPanel.setAllEntries(session.historyEintraege());
        return session;
    }

    static ShellSessionData schnappschuss(RechnerService rechner, RechnerModus aktuellerModus,
                                          List<String> historyEintraege, ThemeType themeType)
    {
        return new ShellSessionData(
                aktuellerModus,
                rechner.getAusdruckText(),
                rechner.getVerlauf(),
                historyEintraege,
                rechner.getWinkelModus(),
                rechner.getSpeicherWert(),
                themeType,
                rechner.getZahlenFormatModus(),
                rechner.getNachkommastellen()
        );
    }

    static void uebernimm(ShellSessionData session, AppSettings settings, RechnerService rechner)
    {
        settings.setThemeType(session.themeType());
        settings.setStartModus(session.aktiverModus());
        settings.setWinkelModus(session.winkelModus());
        settings.setZahlenFormatModus(session.zahlenFormatModus());
        settings.setNachkommastellen(session.nachkommastellen());

        rechner.setWinkelModus(session.winkelModus());
        rechner.setZahlenFormatModus(session.zahlenFormatModus());
        rechner.setNachkommastellen(session.nachkommastellen());
        rechner.setAusdruckText(session.ausdruck());
        rechner.setVerlauf(session.verlauf());
        rechner.setSpeicherWert(session.speicherWert());
    }
}
