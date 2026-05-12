package ui.theme;


import ui.theme.themes.*;
import ui.theme.custom.CustomTheme;
import ui.theme.custom.CustomThemePersistence;

public class ThemeManager
{
    private final ThemePersistence themePersistence = new ThemePersistence();
    private final CustomThemePersistence customThemePersistence;

    private AppTheme currentTheme;
    private ThemeType currentThemeType;

    public ThemeManager()
    {
        this(new CustomThemePersistence());
    }

    public ThemeManager(CustomThemePersistence customThemePersistence)
    {
        this.customThemePersistence = customThemePersistence == null ? new CustomThemePersistence() : customThemePersistence;
        setTheme(themePersistence.ladeTheme(ThemeType.DARK));
    }

    public void setTheme(ThemeType type)
    {
        currentThemeType = type;
        currentTheme = createTheme(type);
        themePersistence.speichereTheme(type);
    }

    public AppTheme getCurrentTheme()
    {
        return currentTheme;
    }

    public ThemeType getCurrentThemeType()
    {
        return currentThemeType;
    }

    private AppTheme createTheme(ThemeType type)
    {
        return switch (type)
        {
            case DARK -> new DarkTheme();
            case LIGHT -> new LightTheme();
            case NEON -> new NeonTheme();
            case MATRIX -> new MatrixTheme();
            case WIN95 -> new Win95Theme();
            case WIN11 -> new Win11Theme();
            case CUSTOM -> new CustomTheme(customThemePersistence.lade());
        };
    }
}
