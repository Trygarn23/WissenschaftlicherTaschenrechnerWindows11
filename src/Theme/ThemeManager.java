package Theme;

import Theme.themes.DarkTheme;
import Theme.themes.LightTheme;

public class ThemeManager
{
    private AppTheme currentTheme;
    private ThemeType currentThemeType;

    public ThemeManager()
    {
        setTheme(ThemeType.DARK);
    }

    public void setTheme(ThemeType type)
    {
        currentThemeType = type;
        currentTheme = createTheme(type);
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
            case LIGHT ->  new LightTheme();
            default -> new DarkTheme();
        };
    }
}