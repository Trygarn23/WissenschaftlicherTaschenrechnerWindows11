package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

public final class AppSettings
{
    public static final String VERSION = "0.5.0-local";
    /** Aufbau der Einstellungsdatei. Hochzählen, wenn sich Bedeutung oder Namen von Feldern ändern. */
    public static final int DATEI_VERSION = 2;

    private ThemeType themeType = ThemeType.DARK;
    private RechnerModus startModus = RechnerModus.STANDARD;
    private WinkelModus winkelModus = WinkelModus.DEG;
    private boolean historyEnabled = true;
    private int nachkommastellen = 11;
    private ZahlenFormatModus zahlenFormatModus = ZahlenFormatModus.AUTO;
    private int fensterBreite = 1180;
    private int fensterHoehe = 860;
    private boolean wenigerBewegung = false;
    private boolean pruefungsModus = false;
    private boolean themeVomSystem = false;
    /** Nur zur Info, aus welcher Dateiversion gelesen wurde (1 = alte Datei ohne Versionseintrag). */
    private int geleseneDateiVersion = DATEI_VERSION;

    public AppSettings copy()
    {
        AppSettings copy = new AppSettings();
        copy.themeType = themeType;
        copy.startModus = startModus;
        copy.winkelModus = winkelModus;
        copy.historyEnabled = historyEnabled;
        copy.nachkommastellen = nachkommastellen;
        copy.zahlenFormatModus = zahlenFormatModus;
        copy.fensterBreite = fensterBreite;
        copy.fensterHoehe = fensterHoehe;
        copy.wenigerBewegung = wenigerBewegung;
        copy.pruefungsModus = pruefungsModus;
        copy.themeVomSystem = themeVomSystem;
        copy.geleseneDateiVersion = geleseneDateiVersion;
        return copy;
    }

    public ThemeType getThemeType()
    {
        return themeType;
    }

    public void setThemeType(ThemeType themeType)
    {
        this.themeType = themeType == null ? ThemeType.DARK : themeType;
    }

    public RechnerModus getStartModus()
    {
        return startModus;
    }

    public void setStartModus(RechnerModus startModus)
    {
        this.startModus = startModus == null ? RechnerModus.STANDARD : startModus;
    }

    public WinkelModus getWinkelModus()
    {
        return winkelModus;
    }

    public void setWinkelModus(WinkelModus winkelModus)
    {
        this.winkelModus = winkelModus == null ? WinkelModus.DEG : winkelModus;
    }

    public boolean isHistoryEnabled()
    {
        return historyEnabled;
    }

    public void setHistoryEnabled(boolean historyEnabled)
    {
        this.historyEnabled = historyEnabled;
    }

    public int getNachkommastellen()
    {
        return nachkommastellen;
    }

    public void setNachkommastellen(int nachkommastellen)
    {
        this.nachkommastellen = Math.max(2, Math.min(15, nachkommastellen));
    }

    public ZahlenFormatModus getZahlenFormatModus()
    {
        return zahlenFormatModus;
    }

    public void setZahlenFormatModus(ZahlenFormatModus zahlenFormatModus)
    {
        this.zahlenFormatModus = zahlenFormatModus == null ? ZahlenFormatModus.AUTO : zahlenFormatModus;
    }

    public int getFensterBreite()
    {
        return fensterBreite;
    }

    public void setFensterBreite(int fensterBreite)
    {
        this.fensterBreite = Math.max(980, fensterBreite);
    }

    public int getFensterHoehe()
    {
        return fensterHoehe;
    }

    public void setFensterHoehe(int fensterHoehe)
    {
        this.fensterHoehe = Math.max(700, fensterHoehe);
    }

    public int getGeleseneDateiVersion()
    {
        return geleseneDateiVersion;
    }

    void setGeleseneDateiVersion(int version)
    {
        this.geleseneDateiVersion = version;
    }

    public boolean isWenigerBewegung()
    {
        return wenigerBewegung;
    }

    public void setWenigerBewegung(boolean wenigerBewegung)
    {
        this.wenigerBewegung = wenigerBewegung;
    }

    public boolean isPruefungsModus()
    {
        return pruefungsModus;
    }

    public void setPruefungsModus(boolean pruefungsModus)
    {
        this.pruefungsModus = pruefungsModus;
    }

    public boolean isThemeVomSystem()
    {
        return themeVomSystem;
    }

    public void setThemeVomSystem(boolean themeVomSystem)
    {
        this.themeVomSystem = themeVomSystem;
    }

    /** Sicherer Standard für „Alles zurücksetzen“: alles wie beim ersten Start, nur die Fenstergröße bleibt. */
    public AppSettings standardMitFenster()
    {
        AppSettings standard = new AppSettings();
        standard.fensterBreite = fensterBreite;
        standard.fensterHoehe = fensterHoehe;
        return standard;
    }
}
