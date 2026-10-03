package common.history;

import common.persistence.AppDateien;
import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.util.List;

public class DateiVerlaufRepository implements VerlaufRepository
{
    private final Path datei;

    public DateiVerlaufRepository()
    {
        this(AppDateien.verlauf());
    }

    public DateiVerlaufRepository(Path datei)
    {
        this.datei = datei;
    }

    @Override
    public List<String> ladeEintraege()
    {
        return DateiPersistenz.ladeZeilen(datei);
    }

    @Override
    public void speichereEintraege(List<String> eintraege)
    {
        DateiPersistenz.speichereZeilen(datei, eintraege);
    }
}
