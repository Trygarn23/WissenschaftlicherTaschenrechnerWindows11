package ui.shell;

import common.history.DateiVerlaufRepository;
import common.history.VerlaufEintrag;
import common.history.VerlaufService;
import common.state.RechnerModus;
import ui.session.SessionPersistence;
import ui.settings.AppSettings;
import ui.settings.SettingsPersistence;

import java.util.List;

public class ShellPersistenceService
{
    private final SettingsPersistence settingsPersistence;
    private final SessionPersistence sessionPersistence;
    private final VerlaufService verlaufService;

    public ShellPersistenceService()
    {
        this(
                new SettingsPersistence(),
                new SessionPersistence(),
                new VerlaufService(new DateiVerlaufRepository())
        );
    }

    ShellPersistenceService(
            SettingsPersistence settingsPersistence,
            SessionPersistence sessionPersistence,
            VerlaufService verlaufService)
    {
        this.settingsPersistence = settingsPersistence;
        this.sessionPersistence = sessionPersistence;
        this.verlaufService = verlaufService;
    }

    public AppSettings ladeSettings()
    {
        return settingsPersistence.lade();
    }

    public void speichereSettings(AppSettings settings)
    {
        settingsPersistence.speichere(settings);
    }

    public void speichereFenstergroesse(AppSettings settings, int breite, int hoehe)
    {
        settings.setFensterBreite(breite);
        settings.setFensterHoehe(hoehe);
        speichereSettings(settings);
    }

    public List<VerlaufEintrag> ladeVerlauf(AppSettings settings)
    {
        if (!settings.isHistoryEnabled())
        {
            return List.of();
        }

        return verlaufService.ladeStrukturierteEintraege(RechnerModus.STANDARD);
    }

    public void speichereVerlauf(AppSettings settings, List<VerlaufEintrag> eintraege)
    {
        if (settings.isHistoryEnabled())
        {
            verlaufService.speichereStrukturierteEintraege(eintraege);
        }
    }

    public VerlaufEintrag erstelleVerlaufEintrag(String text, RechnerModus modus)
    {
        return verlaufService.erstelleEintragAusText(text, modus);
    }

    public void speichereSession(ShellSessionData sessionData)
    {
        sessionPersistence.speichere(sessionData.toSession());
    }

    public ShellSessionData ladeSession()
    {
        return ShellSessionData.fromSession(sessionPersistence.lade());
    }
}
