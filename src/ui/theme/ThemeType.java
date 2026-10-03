package ui.theme;

public enum ThemeType
{
    DARK("Dark"),
    LIGHT("Light"),
    NEON("Neon"),
    MATRIX("Matrix"),
    AZUBI_MODERN("Azubi Modern"),
    WIN95("Win95"),
    WIN11("Win11"),
    CUSTOM("Custom");

    private final String label;

    ThemeType(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
