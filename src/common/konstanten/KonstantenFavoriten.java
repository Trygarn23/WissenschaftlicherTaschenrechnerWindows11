package common.konstanten;

import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/** Favorisierte Konstanten, gespeichert als Textdatei mit einem Namen pro Zeile. */
public final class KonstantenFavoriten
{
    private final Path datei;
    private final Set<String> namen = new LinkedHashSet<>();

    public KonstantenFavoriten(Path datei)
    {
        this.datei = datei;
        for (String zeile : DateiPersistenz.ladeZeilen(datei))
        {
            if (!zeile.isBlank())
            {
                namen.add(zeile.trim());
            }
        }
    }

    public boolean istFavorit(String name)
    {
        return namen.contains(name);
    }

    public void umschalten(String name)
    {
        if (!namen.remove(name))
        {
            namen.add(name);
        }
        DateiPersistenz.speichereZeilen(datei, namen.stream().toList());
    }

    public Set<String> namen()
    {
        return Set.copyOf(namen);
    }
}
