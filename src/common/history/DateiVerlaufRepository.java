package common.history;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        try
        {
            if (!Files.exists(datei))
            {
                return List.of();
            }
            return Files.readAllLines(datei, StandardCharsets.UTF_8);
        }
        catch (IOException ignored)
        {
            return List.of();
        }
    }

    @Override
    public void speichereEintraege(List<String> eintraege)
    {
        try
        {
            Files.write(
                    datei,
                    eintraege,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        catch (IOException ignored)
        {
        }
    }
}
