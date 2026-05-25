package ui.settings;

import ui.theme.custom.CustomThemeColors;
import ui.theme.custom.CustomThemePersistence;

final class PersistentCustomThemeSettingsStore implements CustomThemeSettingsStore
{
    private final CustomThemePersistence persistence;

    PersistentCustomThemeSettingsStore()
    {
        this(new CustomThemePersistence());
    }

    PersistentCustomThemeSettingsStore(CustomThemePersistence persistence)
    {
        this.persistence = persistence;
    }

    @Override
    public CustomThemeColors lade()
    {
        return persistence.lade();
    }

    @Override
    public void speichere(CustomThemeColors colors)
    {
        persistence.speichere(colors);
    }
}
