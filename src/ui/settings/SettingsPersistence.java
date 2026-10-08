package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.persistence.AppDateien;
import common.persistence.DateiPersistenz;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import static common.persistence.DateiPersistenz.leseEnum;
import static common.persistence.DateiPersistenz.leseInt;

public class SettingsPersistence
{
    private final Path datei;

    public SettingsPersistence()
    {
        this(AppDateien.settings());
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
        // Version 1 (ohne Eintrag) kannte die neuen Felder noch nicht – sie bekommen einfach ihre Standardwerte.
        // Eine neuere Version von einer späteren App wird trotzdem gelesen, soweit die Felder bekannt sind.
        settings.setGeleseneDateiVersion(leseInt(properties, "dateiVersion", 1));
        settings.setThemeType(leseEnum(properties, "theme", ThemeType.class, settings.getThemeType()));
        settings.setStartModus(leseEnum(properties, "startModus", RechnerModus.class, settings.getStartModus()));
        settings.setWinkelModus(leseEnum(properties, "winkelModus", WinkelModus.class, settings.getWinkelModus()));
        settings.setHistoryEnabled(leseBoolean(properties, "historyEnabled", settings.isHistoryEnabled()));
        settings.setNachkommastellen(leseInt(properties, "nachkommastellen", settings.getNachkommastellen()));
        settings.setZahlenFormatModus(leseEnum(properties, "zahlenFormat", ZahlenFormatModus.class, settings.getZahlenFormatModus()));
        settings.setFensterBreite(leseInt(properties, "fensterBreite", settings.getFensterBreite()));
        settings.setFensterHoehe(leseInt(properties, "fensterHoehe", settings.getFensterHoehe()));
        settings.setWenigerBewegung(leseBoolean(properties, "wenigerBewegung", settings.isWenigerBewegung()));
        settings.setPruefungsModus(leseBoolean(properties, "pruefungsModus", settings.isPruefungsModus()));
        settings.setThemeVomSystem(leseBoolean(properties, "themeVomSystem", settings.isThemeVomSystem()));
        return settings;
    }

    private static boolean leseBoolean(Properties properties, String key, boolean standard)
    {
        String wert = properties.getProperty(key);
        return wert == null ? standard : Boolean.parseBoolean(wert.trim());
    }

    public void speichere(AppSettings settings)
    {
        Properties properties = new Properties();
        properties.setProperty("dateiVersion", Integer.toString(AppSettings.DATEI_VERSION));
        properties.setProperty("theme", settings.getThemeType().name());
        properties.setProperty("startModus", settings.getStartModus().name());
        properties.setProperty("winkelModus", settings.getWinkelModus().name());
        properties.setProperty("historyEnabled", Boolean.toString(settings.isHistoryEnabled()));
        properties.setProperty("nachkommastellen", Integer.toString(settings.getNachkommastellen()));
        properties.setProperty("zahlenFormat", settings.getZahlenFormatModus().name());
        properties.setProperty("fensterBreite", Integer.toString(settings.getFensterBreite()));
        properties.setProperty("fensterHoehe", Integer.toString(settings.getFensterHoehe()));
        properties.setProperty("wenigerBewegung", Boolean.toString(settings.isWenigerBewegung()));
        properties.setProperty("pruefungsModus", Boolean.toString(settings.isPruefungsModus()));
        properties.setProperty("themeVomSystem", Boolean.toString(settings.isThemeVomSystem()));

        DateiPersistenz.speichereProperties(datei, properties, "Wissenschaftlicher Taschenrechner Einstellungen");
    }
}
