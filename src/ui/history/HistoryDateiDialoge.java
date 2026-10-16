package ui.history;

import common.history.VerlaufEintrag;
import common.history.VerlaufJson;
import ui.shell.DateiDialog;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/** Datei-Dialoge für Export und Import. Das Umwandeln in Text macht {@code common.history}, hier geht es nur um Dateien. */
final class HistoryDateiDialoge
{
    private HistoryDateiDialoge()
    {
    }

    static void speichere(Component parent, String extension, String content)
    {
        Path file = DateiDialog.waehleZumSpeichern(parent, "Verlauf exportieren", beschreibung(extension), extension,
                "verlauf." + extension);
        if (file == null)
        {
            return;
        }

        try
        {
            Files.writeString(file, content, StandardCharsets.UTF_8);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(parent, "Datei konnte nicht gespeichert werden:\n" + ex.getMessage(),
                    "Export fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Liest eine JSON-Datei, die vorher über den Export geschrieben wurde. Leer, wenn abgebrochen oder fehlerhaft. */
    static Optional<List<VerlaufEintrag>> ladeJson(Component parent)
    {
        Path file = DateiDialog.waehleZumOeffnen(parent, "Verlauf importieren", beschreibung("json"), "json");
        if (file == null)
        {
            return Optional.empty();
        }

        try
        {
            return Optional.of(VerlaufJson.lese(Files.readString(file, StandardCharsets.UTF_8)));
        }
        catch (IOException | IllegalArgumentException ex)
        {
            JOptionPane.showMessageDialog(parent, "Datei konnte nicht gelesen werden:\n" + ex.getMessage(),
                    "Import fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }

    private static String beschreibung(String extension)
    {
        return extension.toUpperCase() + "-Datei";
    }
}
