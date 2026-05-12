package ui.session;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SessionPersistence
{
    private static final Path STANDARD_DATEI =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_session.properties");

    private final Path datei;

    public SessionPersistence()
    {
        this(STANDARD_DATEI);
    }

    public SessionPersistence(Path datei)
    {
        this.datei = datei;
    }

    public RechnerSession lade()
    {
        RechnerSession fallback = RechnerSession.standard();
        if (!Files.exists(datei))
        {
            return fallback;
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(datei, StandardCharsets.UTF_8))
        {
            properties.load(reader);
        }
        catch (IOException ignored)
        {
            return fallback;
        }

        String version = properties.getProperty("version", "");
        if (!RechnerSession.VERSION.equals(version))
        {
            return fallback;
        }

        return new RechnerSession(
                version,
                readEnum(properties, "aktiverModus", RechnerModus.class, fallback.getAktiverModus()),
                properties.getProperty("ausdruck", fallback.getAusdruck()),
                properties.getProperty("verlauf", fallback.getVerlauf()),
                readHistory(properties),
                readEnum(properties, "winkelModus", WinkelModus.class, fallback.getWinkelModus()),
                readDouble(properties, "speicherWert", fallback.getSpeicherWert()),
                readEnum(properties, "theme", ThemeType.class, fallback.getThemeType()),
                readEnum(properties, "zahlenFormat", ZahlenFormatModus.class, fallback.getZahlenFormatModus()),
                readInt(properties, "nachkommastellen", fallback.getNachkommastellen())
        );
    }

    public void speichere(RechnerSession session)
    {
        RechnerSession value = session == null ? RechnerSession.standard() : session;
        Properties properties = new Properties();
        properties.setProperty("version", RechnerSession.VERSION);
        properties.setProperty("aktiverModus", value.getAktiverModus().name());
        properties.setProperty("ausdruck", value.getAusdruck());
        properties.setProperty("verlauf", value.getVerlauf());
        properties.setProperty("winkelModus", value.getWinkelModus().name());
        properties.setProperty("speicherWert", Double.toString(value.getSpeicherWert()));
        properties.setProperty("theme", value.getThemeType().name());
        properties.setProperty("zahlenFormat", value.getZahlenFormatModus().name());
        properties.setProperty("nachkommastellen", Integer.toString(value.getNachkommastellen()));
        properties.setProperty("history.count", Integer.toString(value.getHistoryEintraege().size()));

        for (int i = 0; i < value.getHistoryEintraege().size(); i++)
        {
            properties.setProperty("history." + i, value.getHistoryEintraege().get(i));
        }

        try
        {
            Path parent = datei.getParent();
            if (parent != null)
            {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(datei, StandardCharsets.UTF_8))
            {
                properties.store(writer, "Wissenschaftlicher Taschenrechner Session");
            }
        }
        catch (IOException ignored)
        {
        }
    }

    private List<String> readHistory(Properties properties)
    {
        int count = readInt(properties, "history.count", 0);
        if (count <= 0)
        {
            return List.of();
        }

        List<String> entries = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            String entry = properties.getProperty("history." + i, "");
            if (!entry.isBlank())
            {
                entries.add(entry);
            }
        }
        return entries;
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

    private double readDouble(Properties properties, String key, double fallback)
    {
        try
        {
            double value = Double.parseDouble(properties.getProperty(key, Double.toString(fallback)));
            return Double.isFinite(value) ? value : fallback;
        }
        catch (NumberFormatException ignored)
        {
            return fallback;
        }
    }
}
