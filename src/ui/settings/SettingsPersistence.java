package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
        if (!Files.exists(datei))
        {
            return settings;
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(datei, StandardCharsets.UTF_8))
        {
            properties.load(reader);
        }
        catch (IOException ignored)
        {
            return settings;
        }

        settings.setThemeType(readEnum(properties, "theme", ThemeType.class, settings.getThemeType()));
        settings.setStartModus(readEnum(properties, "startModus", RechnerModus.class, settings.getStartModus()));
        settings.setWinkelModus(readEnum(properties, "winkelModus", WinkelModus.class, settings.getWinkelModus()));
        settings.setHistoryEnabled(Boolean.parseBoolean(properties.getProperty("historyEnabled", Boolean.toString(settings.isHistoryEnabled()))));
        settings.setNachkommastellen(readInt(properties, "nachkommastellen", settings.getNachkommastellen()));
        settings.setZahlenFormatModus(readEnum(properties, "zahlenFormat", ZahlenFormatModus.class, settings.getZahlenFormatModus()));
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

        try
        {
            Path parent = datei.getParent();
            if (parent != null)
            {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(datei, StandardCharsets.UTF_8))
            {
                properties.store(writer, "Wissenschaftlicher Taschenrechner Einstellungen");
            }
        }
        catch (IOException ignored)
        {
        }
    }

    private <T extends Enum<T>> T readEnum(Properties properties, String key, Class<T> enumType, T fallback)
    {
        try
        {
            return Enum.valueOf(enumType, properties.getProperty(key, fallback.name()));
        }
        catch (IllegalArgumentException ignored)
        {
            return fallback;
        }
    }

    private int readInt(Properties properties, String key, int fallback)
    {
        try
        {
            return Integer.parseInt(properties.getProperty(key, Integer.toString(fallback)));
        }
        catch (NumberFormatException ignored)
        {
            return fallback;
        }
    }
}
