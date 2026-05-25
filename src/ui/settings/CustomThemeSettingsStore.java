package ui.settings;

import ui.theme.custom.CustomThemeColors;

interface CustomThemeSettingsStore
{
    CustomThemeColors lade();

    void speichere(CustomThemeColors colors);
}
