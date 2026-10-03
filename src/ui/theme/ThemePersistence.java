package ui.theme;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ThemePersistence
{
    private static final Path THEME_DATEI =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_theme.txt");

    public ThemeType ladeTheme(ThemeType fallback)
    {
        try
        {
            if (!Files.exists(THEME_DATEI))
            {
                return fallback;
            }

            String text = Files.readString(THEME_DATEI, StandardCharsets.UTF_8).trim();
            if (text.isBlank())
            {
                return fallback;
            }

            return ThemeType.valueOf(text);
        }
        catch (Exception ignored)
        {
            return fallback;
        }
    }

    public void speichereTheme(ThemeType themeType)
    {
        try
        {
            Files.writeString(
                    THEME_DATEI,
                    themeType.name(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        catch (IOException ignored)
        {
        }
    }
}
