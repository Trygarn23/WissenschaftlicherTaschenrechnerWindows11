package common.history;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

/**
 * Alle Verlaufseinträge plus ein einmaliges Rückgängig fürs Löschen.
 * Kennt kein Swing, das Verlaufspanel zeigt nur an, was hier drinsteht.
 */
public final class VerlaufListe
{
    private final List<VerlaufEintrag> eintraege = new ArrayList<>();

    /** Stand vor dem letzten Löschen. Gilt nur, bis sich der Verlauf wieder anders ändert. */
    private List<VerlaufEintrag> vorDemLoeschen;

    public List<VerlaufEintrag> alle()
    {
        return List.copyOf(eintraege);
    }

    public boolean istLeer()
    {
        return eintraege.isEmpty();
    }

    public void ersetzeAlle(List<VerlaufEintrag> neue)
    {
        vorDemLoeschen = null;
        eintraege.clear();
        for (VerlaufEintrag eintrag : neue)
        {
            if (istGueltig(eintrag))
            {
                eintraege.add(eintrag);
            }
        }
    }

    /** @return {@code false}, wenn der Eintrag leer war und deshalb nicht übernommen wurde */
    public boolean fuegeHinzu(VerlaufEintrag eintrag)
    {
        if (!istGueltig(eintrag))
        {
            return false;
        }

        vorDemLoeschen = null;
        eintraege.add(eintrag);
        return true;
    }

    /** Was schon genauso im Verlauf steht, kommt nicht doppelt rein; danach nach Zeit sortiert. */
    public void importiere(List<VerlaufEintrag> importierte)
    {
        LinkedHashSet<VerlaufEintrag> zusammen = new LinkedHashSet<>(eintraege);
        zusammen.addAll(importierte);
        List<VerlaufEintrag> sortiert = new ArrayList<>(zusammen);
        sortiert.sort(Comparator.comparing(VerlaufEintrag::getZeitpunkt));
        ersetzeAlle(sortiert);
    }

    public void entferne(List<VerlaufEintrag> weg)
    {
        List<VerlaufEintrag> vorher = alle();
        weg.forEach(eintraege::remove);
        vorDemLoeschen = vorher;
    }

    public boolean kannRueckgaengig()
    {
        return vorDemLoeschen != null;
    }

    /** @return {@code true}, wenn tatsächlich etwas zurückgeholt wurde */
    public boolean macheLoeschenRueckgaengig()
    {
        if (vorDemLoeschen == null)
        {
            return false;
        }

        List<VerlaufEintrag> zurueck = vorDemLoeschen;
        vorDemLoeschen = null;
        eintraege.clear();
        eintraege.addAll(zurueck);
        return true;
    }

    /** @return der geänderte Eintrag, leer wenn der Eintrag gar nicht im Verlauf steht */
    public Optional<VerlaufEintrag> schalteFavorit(VerlaufEintrag eintrag)
    {
        int index = eintraege.indexOf(eintrag);
        if (index < 0)
        {
            return Optional.empty();
        }

        VerlaufEintrag geaendert = eintrag.toggleFavorit();
        eintraege.set(index, geaendert);
        return Optional.of(geaendert);
    }

    private static boolean istGueltig(VerlaufEintrag eintrag)
    {
        return eintrag != null && !eintrag.toLegacyText().isBlank();
    }
}
