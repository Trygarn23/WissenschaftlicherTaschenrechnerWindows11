package ui.shell;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Merkt sich den aktuellen Ausdruck in einer kleinen Datei. Beim normalen Beenden wird sie gelöscht –
 * liegt sie beim Start noch da, ist die App vorher abgestürzt oder hart beendet worden.
 */
public class LetzteEingabeSicherung
{
    private static final Logger LOGGER = Logger.getLogger(LetzteEingabeSicherung.class.getName());

    private final Path datei;
    private String zuletztGeschrieben = "";

    public LetzteEingabeSicherung(Path datei)
    {
        this.datei = datei;
    }

    /** Eingabe, die nach einem Absturz übrig geblieben ist. Leer, wenn alles normal beendet wurde. */
    public Optional<String> ladeUebrigeEingabe()
    {
        try
        {
            if (!Files.exists(datei))
            {
                return Optional.empty();
            }
            String text = Files.readString(datei, StandardCharsets.UTF_8).strip();
            return text.isEmpty() ? Optional.empty() : Optional.of(text);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Letzte Eingabe konnte nicht gelesen werden: " + datei, e);
            return Optional.empty();
        }
    }

    /** Schreibt nur, wenn sich der Ausdruck wirklich geändert hat, damit nicht jeder Refresh die Platte anfasst. */
    public void merke(String ausdruck)
    {
        String text = ausdruck == null ? "" : ausdruck;
        if (text.equals(zuletztGeschrieben))
        {
            return;
        }

        try
        {
            if (text.isBlank())
            {
                Files.deleteIfExists(datei);
            }
            else
            {
                Files.createDirectories(datei.getParent());
                Files.writeString(datei, text, StandardCharsets.UTF_8);
            }
            zuletztGeschrieben = text;
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Letzte Eingabe konnte nicht gesichert werden: " + datei, e);
        }
    }

    /** Beim normalen Beenden: nichts mehr wiederherzustellen. */
    public void loesche()
    {
        merke("");
    }
}
