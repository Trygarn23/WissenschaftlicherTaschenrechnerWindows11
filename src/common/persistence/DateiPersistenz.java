package common.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gemeinsame Lese-/Schreiblogik für die kleinen Konfigurationsdateien im Benutzerverzeichnis.
 * Fehler werden geloggt statt verschluckt, die App läuft aber mit Standardwerten weiter.
 */
public final class DateiPersistenz
{
    private static final Logger LOGGER = Logger.getLogger(DateiPersistenz.class.getName());

    private DateiPersistenz()
    {
    }

    public static Optional<Properties> ladeProperties(Path datei)
    {
        if (!Files.exists(datei))
        {
            return Optional.empty();
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(datei, StandardCharsets.UTF_8))
        {
            properties.load(reader);
            return Optional.of(properties);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Datei konnte nicht gelesen werden: " + datei, e);
            return Optional.empty();
        }
    }

    public static void speichereProperties(Path datei, Properties properties, String kommentar)
    {
        try
        {
            erstelleElternordner(datei);
            try (Writer writer = Files.newBufferedWriter(datei, StandardCharsets.UTF_8))
            {
                properties.store(writer, kommentar);
            }
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Datei konnte nicht gespeichert werden: " + datei, e);
        }
    }

    public static List<String> ladeZeilen(Path datei)
    {
        if (!Files.exists(datei))
        {
            return List.of();
        }

        try
        {
            return Files.readAllLines(datei, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Datei konnte nicht gelesen werden: " + datei, e);
            return List.of();
        }
    }

    public static void speichereZeilen(Path datei, List<String> zeilen)
    {
        try
        {
            erstelleElternordner(datei);
            Files.write(datei, zeilen, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Datei konnte nicht gespeichert werden: " + datei, e);
        }
    }

    public static <T extends Enum<T>> T leseEnum(Properties properties, String key, Class<T> enumType, T fallback)
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

    public static int leseInt(Properties properties, String key, int fallback)
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

    public static double leseDouble(Properties properties, String key, double fallback)
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

    private static void erstelleElternordner(Path datei) throws IOException
    {
        Path parent = datei.getParent();
        if (parent != null)
        {
            Files.createDirectories(parent);
        }
    }
}
