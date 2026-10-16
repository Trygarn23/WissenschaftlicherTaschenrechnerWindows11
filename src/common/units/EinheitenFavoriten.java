package common.units;

import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Gemerkte Umrechnungen, gespeichert als {@code KATEGORIE;von;nach} (Einheitennamen) pro Zeile. */
public final class EinheitenFavoriten
{
    public record Umrechnung(EinheitKategorie kategorie, String von, String nach)
    {
        @Override
        public String toString()
        {
            return kategorie.getLabel() + ": " + von + " → " + nach;
        }
    }

    private final Path datei;
    private final List<Umrechnung> umrechnungen = new ArrayList<>();

    public EinheitenFavoriten(Path datei)
    {
        this.datei = datei;
        for (String zeile : DateiPersistenz.ladeZeilen(datei))
        {
            String[] teile = zeile.split(";", -1);
            if (teile.length != 3 || teile[1].isBlank() || teile[2].isBlank())
            {
                continue;
            }
            try
            {
                Umrechnung umrechnung = new Umrechnung(EinheitKategorie.valueOf(teile[0].trim()), teile[1].trim(), teile[2].trim());
                if (!umrechnungen.contains(umrechnung))
                {
                    umrechnungen.add(umrechnung);
                }
            }
            catch (IllegalArgumentException ignored)
            {
                // Unbekannte Kategorie: Zeile überspringen.
            }
        }
    }

    public List<Umrechnung> alle()
    {
        return List.copyOf(umrechnungen);
    }

    public boolean istFavorit(Umrechnung umrechnung)
    {
        return umrechnungen.contains(umrechnung);
    }

    public void umschalten(Umrechnung umrechnung)
    {
        if (!umrechnungen.remove(umrechnung))
        {
            umrechnungen.add(umrechnung);
        }
        DateiPersistenz.speichereZeilen(datei, umrechnungen.stream()
                .map(u -> u.kategorie().name() + ";" + u.von() + ";" + u.nach())
                .toList());
    }
}
