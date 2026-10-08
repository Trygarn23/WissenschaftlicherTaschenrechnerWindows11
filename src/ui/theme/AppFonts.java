package ui.theme;

import java.awt.Font;

/** Zentrale Stelle für die UI-Schrift, damit sie nicht in jedem Panel neu ausgeschrieben wird. */
public final class AppFonts
{
    public static final String SCHRIFTART = "Segoe UI";

    private AppFonts()
    {
    }

    public static Font normal(int groesse)
    {
        return new Font(SCHRIFTART, Font.PLAIN, groesse);
    }

    public static Font fett(int groesse)
    {
        return new Font(SCHRIFTART, Font.BOLD, groesse);
    }

    /** Logische Symbole fehlen in Segoe UI; vor Verwendung Glyphen prüfen. */
    public static Font symbole(int groesse)
    {
        for (String name : new String[]{"Segoe UI Symbol", "Dialog"})
        {
            Font font = new Font(name, Font.PLAIN, groesse);
            if (font.canDisplayUpTo("∧∨¬⊕→↔") == -1) return font;
        }
        return new Font("Dialog", Font.PLAIN, groesse);
    }

    /** Für Textbereiche, in denen Spalten untereinander stehen sollen. */
    public static Font festeBreite(int groesse)
    {
        return new Font("Consolas", Font.PLAIN, groesse);
    }
}
