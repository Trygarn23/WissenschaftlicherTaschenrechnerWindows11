package common.history;

import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DateiVerlaufRepository implements VerlaufRepository
{
    private static final Path STANDARD_DATEI =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_history.txt");

    private final Path datei;

    public DateiVerlaufRepository()
    {
        this(STANDARD_DATEI);
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
