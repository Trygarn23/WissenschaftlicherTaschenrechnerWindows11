package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

public final class AppSettings
{
    public static final String VERSION = "0.4.0-local";

    private ThemeType themeType = ThemeType.DARK;
    private RechnerModus startModus = RechnerModus.STANDARD;
    private WinkelModus winkelModus = WinkelModus.DEG;
    private boolean historyEnabled = true;
    private int nachkommastellen = 11;
    private ZahlenFormatModus zahlenFormatModus = ZahlenFormatModus.AUTO;
    private int fensterBreite = 1180;
    private int fensterHoehe = 860;

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
}
