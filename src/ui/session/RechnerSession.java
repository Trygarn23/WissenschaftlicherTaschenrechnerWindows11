package ui.session;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RechnerSession
{
    public static final String VERSION = "1";

    private final String version;
    private final RechnerModus aktiverModus;
    private final String ausdruck;
    private final String verlauf;
    private final List<String> historyEintraege;
    private final WinkelModus winkelModus;
    private final double speicherWert;
    private final ThemeType themeType;
    private final ZahlenFormatModus zahlenFormatModus;
    private final int nachkommastellen;

    public RechnerSession(
            String version,
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
        this.version = version == null ? VERSION : version;
        this.aktiverModus = aktiverModus == null ? RechnerModus.STANDARD : aktiverModus;
        this.ausdruck = ausdruck == null ? "" : ausdruck;
        this.verlauf = verlauf == null ? "" : verlauf;
        this.historyEintraege = Collections.unmodifiableList(new ArrayList<>(
                historyEintraege == null ? List.of() : historyEintraege
        ));
        this.winkelModus = winkelModus == null ? WinkelModus.DEG : winkelModus;
        this.speicherWert = Double.isFinite(speicherWert) ? speicherWert : 0.0;
        this.themeType = themeType == null ? ThemeType.DARK : themeType;
        this.zahlenFormatModus = zahlenFormatModus == null ? ZahlenFormatModus.AUTO : zahlenFormatModus;
        this.nachkommastellen = Math.max(2, Math.min(15, nachkommastellen));
    }

    public static RechnerSession standard()
    {
        return new RechnerSession(
                VERSION,
                RechnerModus.STANDARD,
                "",
                "",
                List.of(),
                WinkelModus.DEG,
                0.0,
                ThemeType.DARK,
                ZahlenFormatModus.AUTO,
                11
        );
    }

    public String getVersion()
    {
        return version;
    }

    public RechnerModus getAktiverModus()
    {
        return aktiverModus;
    }

    public String getAusdruck()
    {
        return ausdruck;
    }

    public String getVerlauf()
    {
        return verlauf;
    }

    public List<String> getHistoryEintraege()
    {
        return historyEintraege;
    }

    public WinkelModus getWinkelModus()
    {
        return winkelModus;
    }

    public double getSpeicherWert()
    {
        return speicherWert;
    }

    public ThemeType getThemeType()
    {
        return themeType;
    }

    public ZahlenFormatModus getZahlenFormatModus()
    {
        return zahlenFormatModus;
    }

    public int getNachkommastellen()
    {
        return nachkommastellen;
    }
}
