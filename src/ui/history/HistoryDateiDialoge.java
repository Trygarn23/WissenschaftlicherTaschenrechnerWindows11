package ui.history;

import common.history.VerlaufEintrag;
import common.history.VerlaufJson;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
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
        JFileChooser chooser = chooser("Verlauf exportieren", extension);
        chooser.setSelectedFile(new File("verlauf." + extension));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        Path file = chooser.getSelectedFile().toPath();
        if (!file.getFileName().toString().toLowerCase().endsWith("." + extension))
        {
            file = file.resolveSibling(file.getFileName() + "." + extension);
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
        JFileChooser chooser = chooser("Verlauf importieren", "json");
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
        {
            return Optional.empty();
        }

        try
        {
            return Optional.of(VerlaufJson.lese(Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8)));
        }
        catch (IOException | IllegalArgumentException ex)
        {
            JOptionPane.showMessageDialog(parent, "Datei konnte nicht gelesen werden:\n" + ex.getMessage(),
                    "Import fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }

    private static JFileChooser chooser(String title, String extension)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + "-Datei", extension));
        return chooser;
    }
}
