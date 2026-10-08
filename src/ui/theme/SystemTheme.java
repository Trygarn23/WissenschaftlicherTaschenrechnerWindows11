package ui.theme;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Fragt Windows, ob Apps hell oder dunkel dargestellt werden sollen.
 * Liest nur den Registry-Wert {@code AppsUseLightTheme}; auf anderen Systemen oder bei Fehlern gibt es keine Antwort.
 */
public final class SystemTheme
{
    private static final String SCHLUESSEL = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";

    private SystemTheme()
    {
    }

    /** Light oder Dark passend zum System, sonst {@code fallback}. */
    public static ThemeType passendesTheme(ThemeType fallback)
    {
        return istHell().map(hell -> hell ? ThemeType.LIGHT : ThemeType.DARK).orElse(fallback);
    }

    public static Optional<Boolean> istHell()
    {
        if (!System.getProperty("os.name", "").toLowerCase().contains("windows"))
        {
            return Optional.empty();
        }

        try
        {
            Process process = new ProcessBuilder("reg", "query", SCHLUESSEL, "/v", "AppsUseLightTheme")
                    .redirectErrorStream(true)
                    .start();
            // ponytail: kurzer Timeout statt eigenem Thread – beim Start lieber ohne Systemtheme als hängen.
            if (!process.waitFor(2, TimeUnit.SECONDS))
            {
                process.destroyForcibly();
                return Optional.empty();
            }
            return leseAusgabe(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            return Optional.empty();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    /** Wertet die Ausgabe von {@code reg query} aus, z. B. „AppsUseLightTheme    REG_DWORD    0x1“. */
    static Optional<Boolean> leseAusgabe(String ausgabe)
    {
        for (String zeile : ausgabe.split("\\R"))
        {
            String[] teile = zeile.trim().split("\\s+");
            if (teile.length == 3 && teile[0].equals("AppsUseLightTheme") && teile[1].equals("REG_DWORD"))
            {
                return switch (teile[2].toLowerCase())
                {
                    case "0x1" -> Optional.of(true);
                    case "0x0" -> Optional.of(false);
                    default -> Optional.empty();
                };
            }
        }
        return Optional.empty();
    }
}
