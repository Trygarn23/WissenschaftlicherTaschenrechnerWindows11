package ui.befehle;

import java.util.List;

/** Ein Eintrag der Befehlssuche, z. B. „Wissenschaftlich“ (Modus) oder „Pi“ (Konstante). */
public record Befehl(String titel, String kategorie, List<String> suchbegriffe, Runnable aktion)
{
    public Befehl
    {
        suchbegriffe = List.copyOf(suchbegriffe);
    }

    public Befehl(String titel, String kategorie, Runnable aktion)
    {
        this(titel, kategorie, List.of(), aktion);
    }
}
