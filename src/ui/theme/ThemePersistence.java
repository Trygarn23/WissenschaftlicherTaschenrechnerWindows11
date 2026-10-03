package ui.theme;

import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ThemePersistence
{
    private static final Path THEME_DATEI =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_theme.txt");

    public ThemeType ladeTheme(ThemeType fallback)
    {
        String text = String.join("", DateiPersistenz.ladeZeilen(THEME_DATEI)).trim();
        if (text.isBlank())
        {
            return fallback;
        }

        try
        {
            return ThemeType.valueOf(text);
        }
        catch (IllegalArgumentException ignored)
        {
            return fallback;
        }
    }

    public void speichereTheme(ThemeType themeType)
    {
        DateiPersistenz.speichereZeilen(THEME_DATEI, List.of(themeType.name()));
    }
}
