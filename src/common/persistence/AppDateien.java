package common.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Alle Dateien der App liegen in einem Ordner {@code ~/.wissenschaftlicher_taschenrechner/}.
 * Alte Dateien direkt im Benutzerordner ({@code ~/.wissenschaftlicher_taschenrechner_<name>}) werden beim
 * ersten Zugriff einmal in den neuen Ordner verschoben.
 */
public final class AppDateien
{
    private static final Logger LOGGER = Logger.getLogger(AppDateien.class.getName());
    private static final String ORDNER_NAME = ".wissenschaftlicher_taschenrechner";
    private static final String ALTES_PRAEFIX = ".wissenschaftlicher_taschenrechner_";

    private AppDateien()
    {
    }

    public static Path settings()
    {
        return datei(benutzerordner(), "settings.properties");
    }

    public static Path session()
    {
        return datei(benutzerordner(), "session.properties");
    }

    public static Path verlauf()
    {
        return datei(benutzerordner(), "history.txt");
    }

    public static Path customTheme()
    {
        return datei(benutzerordner(), "custom_theme.properties");
    }

    public static Path konstantenFavoriten()
    {
        return datei(benutzerordner(), "konstanten_favoriten.txt");
    }

    public static Path eigeneKonstanten()
    {
        return datei(benutzerordner(), "eigene_konstanten.txt");
    }

    public static Path eigeneFunktionen()
    {
        return datei(benutzerordner(), "eigene_funktionen.txt");
    }

    /** Letzte Eingabe, falls die App abstürzt oder hart beendet wird. */
    public static Path wiederherstellung()
    {
        return datei(benutzerordner(), "letzte_eingabe.txt");
    }

    static Path datei(Path benutzerordner, String name)
    {
        Path neu = benutzerordner.resolve(ORDNER_NAME).resolve(name);
        Path alt = benutzerordner.resolve(ALTES_PRAEFIX + name);
        if (Files.exists(neu) || !Files.exists(alt))
        {
            return neu;
        }

        try
        {
            Files.createDirectories(neu.getParent());
            Files.move(alt, neu);
            return neu;
        }
        catch (IOException e)
        {
            // Lieber mit der alten Datei weiterarbeiten als Daten zu verlieren.
            LOGGER.log(Level.WARNING, "Datei konnte nicht verschoben werden: " + alt, e);
            return alt;
        }
    }

    private static Path benutzerordner()
    {
        return Paths.get(System.getProperty("user.home"));
    }
}
