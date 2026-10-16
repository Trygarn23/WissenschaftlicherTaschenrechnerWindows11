package ui.shell;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateiDialogTest
{
    @Test
    void mitEndung_ShouldAppendMissingExtensionOnly()
    {
        assertEquals(Path.of("ordner", "matrix.csv"), DateiDialog.mitEndung(Path.of("ordner", "matrix"), "csv"));
        assertEquals(Path.of("ordner", "Matrix.CSV"), DateiDialog.mitEndung(Path.of("ordner", "Matrix.CSV"), "csv"));
        assertEquals(Path.of("bild.txt.png"), DateiDialog.mitEndung(Path.of("bild.txt"), "png"));
    }
}
