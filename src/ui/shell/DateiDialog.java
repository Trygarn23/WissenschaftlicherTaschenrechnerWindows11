package ui.shell;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

/** Öffnen-/Speichern-Dialoge mit Dateifilter für die Modi; {@code null} bei Abbruch. */
public final class DateiDialog
{
    private DateiDialog()
    {
    }

    public static Path waehleZumOeffnen(Component parent, String titel, String beschreibung, String endung)
    {
        JFileChooser chooser = chooser(titel, beschreibung, endung);
        return chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().toPath() : null;
    }

    /** Fragt nach dem Speicherort und hängt die Endung an, falls sie fehlt. {@code vorschlag} darf {@code null} sein. */
    public static Path waehleZumSpeichern(Component parent, String titel, String beschreibung, String endung, String vorschlag)
    {
        JFileChooser chooser = chooser(titel, beschreibung, endung);
        if (vorschlag != null)
        {
            chooser.setSelectedFile(new File(vorschlag));
        }
        return chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION ? mitEndung(chooser.getSelectedFile().toPath(), endung) : null;
    }

    static Path mitEndung(Path datei, String endung)
    {
        String name = datei.getFileName().toString();
        return name.toLowerCase(Locale.ROOT).endsWith("." + endung) ? datei : datei.resolveSibling(name + "." + endung);
    }

    private static JFileChooser chooser(String titel, String beschreibung, String endung)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(titel);
        chooser.setFileFilter(new FileNameExtensionFilter(beschreibung, endung));
        return chooser;
    }
}
