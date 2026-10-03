package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.persistence.DateiPersistenz;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static common.persistence.DateiPersistenz.leseEnum;
import static common.persistence.DateiPersistenz.leseInt;

public class SettingsPersistence
{
    private static final Path STANDARD_DATEI =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_settings.properties");

    private final Path datei;

    public SettingsPersistence()
    {
        this(STANDARD_DATEI);
    }

    public SettingsPersistence(Path datei)
    {
        this.datei = datei;
    }

    public AppSettings lade()
    {
        AppSettings settings = new AppSettings();
        Optional<Properties> geladen = DateiPersistenz.ladeProperties(datei);
        if (geladen.isEmpty())
        {
            return settings;
        }

        Properties properties = geladen.get();
        settings.setThemeType(leseEnum(properties, "theme", ThemeType.class, settings.getThemeType()));
        settings.setStartModus(leseEnum(properties, "startModus", RechnerModus.class, settings.getStartModus()));
        settings.setWinkelModus(leseEnum(properties, "winkelModus", WinkelModus.class, settings.getWinkelModus()));
        settings.setHistoryEnabled(Boolean.parseBoolean(properties.getProperty("historyEnabled", Boolean.toString(settings.isHistoryEnabled()))));
        settings.setNachkommastellen(leseInt(properties, "nachkommastellen", settings.getNachkommastellen()));
        settings.setZahlenFormatModus(leseEnum(properties, "zahlenFormat", ZahlenFormatModus.class, settings.getZahlenFormatModus()));
        settings.setFensterBreite(leseInt(properties, "fensterBreite", settings.getFensterBreite()));
        settings.setFensterHoehe(leseInt(properties, "fensterHoehe", settings.getFensterHoehe()));
        return settings;
    }

    public void speichere(AppSettings settings)
    {
        Properties properties = new Properties();
        properties.setProperty("theme", settings.getThemeType().name());
        properties.setProperty("startModus", settings.getStartModus().name());
        properties.setProperty("winkelModus", settings.getWinkelModus().name());
        properties.setProperty("historyEnabled", Boolean.toString(settings.isHistoryEnabled()));
        properties.setProperty("nachkommastellen", Integer.toString(settings.getNachkommastellen()));
        properties.setProperty("zahlenFormat", settings.getZahlenFormatModus().name());
        properties.setProperty("fensterBreite", Integer.toString(settings.getFensterBreite()));
        properties.setProperty("fensterHoehe", Integer.toString(settings.getFensterHoehe()));

        DateiPersistenz.speichereProperties(datei, properties, "Wissenschaftlicher Taschenrechner Einstellungen");
    }
}
