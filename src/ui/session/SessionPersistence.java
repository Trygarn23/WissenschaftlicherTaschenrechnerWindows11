package ui.session;

import common.formatting.ZahlenFormatModus;
import common.persistence.AppDateien;
import common.persistence.DateiPersistenz;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.theme.ThemeType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static common.persistence.DateiPersistenz.leseDouble;
import static common.persistence.DateiPersistenz.leseEnum;
import static common.persistence.DateiPersistenz.leseInt;

public class SessionPersistence
{
    private final Path datei;

    public SessionPersistence()
    {
        this(AppDateien.session());
    }

    public SessionPersistence(Path datei)
    {
        this.datei = datei;
    }

    public RechnerSession lade()
    {
        RechnerSession fallback = RechnerSession.standard();
        Optional<Properties> geladen = DateiPersistenz.ladeProperties(datei);
        if (geladen.isEmpty())
        {
            return fallback;
        }

        Properties properties = geladen.get();
        String version = properties.getProperty("version", "");
        if (!RechnerSession.VERSION.equals(version))
        {
            return fallback;
        }

        return new RechnerSession(
                version,
                leseEnum(properties, "aktiverModus", RechnerModus.class, fallback.getAktiverModus()),
                properties.getProperty("ausdruck", fallback.getAusdruck()),
                properties.getProperty("verlauf", fallback.getVerlauf()),
                readHistory(properties),
                leseEnum(properties, "winkelModus", WinkelModus.class, fallback.getWinkelModus()),
                leseDouble(properties, "speicherWert", fallback.getSpeicherWert()),
                leseEnum(properties, "theme", ThemeType.class, fallback.getThemeType()),
                leseEnum(properties, "zahlenFormat", ZahlenFormatModus.class, fallback.getZahlenFormatModus()),
                leseInt(properties, "nachkommastellen", fallback.getNachkommastellen())
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

        DateiPersistenz.speichereProperties(datei, properties, "Wissenschaftlicher Taschenrechner Session");
    }

    private List<String> readHistory(Properties properties)
    {
        int count = leseInt(properties, "history.count", 0);
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
}
