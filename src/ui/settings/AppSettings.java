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

    public AppSettings copy()
    {
        AppSettings copy = new AppSettings();
        copy.themeType = themeType;
        copy.startModus = startModus;
        copy.winkelModus = winkelModus;
        copy.historyEnabled = historyEnabled;
        copy.nachkommastellen = nachkommastellen;
        copy.zahlenFormatModus = zahlenFormatModus;
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
}
