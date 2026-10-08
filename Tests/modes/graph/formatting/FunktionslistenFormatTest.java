package modes.graph.formatting;

import modes.graph.model.FunktionsDefinition;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunktionslistenFormatTest
{
    @Test
    void schreibe_ShouldWriteOneLinePerFunction()
    {
        // Arrange
        FunktionsDefinition f = new FunktionsDefinition("f", "a*x^2", new Color(24, 153, 219));
        FunktionsDefinition g = new FunktionsDefinition("g", "sin(x)", Color.RED);
        g.setSichtbar(false);

        // Act
        String text = FunktionslistenFormat.schreibe(List.of(f, g));

        // Assert
        assertEquals("f;a*x^2;#1899DB;true\ng;sin(x);#FF0000;false\n", text);
    }

    @Test
    void lese_ShouldRestoreWrittenFunctions()
    {
        // Arrange
        FunktionsDefinition f = new FunktionsDefinition("f", "x^2-4", new Color(76, 190, 120));
        FunktionsDefinition h = new FunktionsDefinition("h", "f(x)+1", new Color(155, 110, 230));
        h.setSichtbar(false);

        // Act
        List<FunktionsDefinition> gelesen = FunktionslistenFormat.lese(FunktionslistenFormat.schreibe(List.of(f, h)));

        // Assert
        assertEquals(2, gelesen.size());
        assertEquals("f", gelesen.get(0).getName());
        assertEquals("x^2-4", gelesen.get(0).getAusdruck());
        assertEquals(new Color(76, 190, 120), gelesen.get(0).getFarbe());
        assertTrue(gelesen.get(0).isSichtbar());
        assertEquals("h", gelesen.get(1).getName());
        assertFalse(gelesen.get(1).isSichtbar());
    }

    @Test
    void lese_ShouldSkipEmptyLinesAndAcceptWindowsLineBreaks()
    {
        // Act
        List<FunktionsDefinition> gelesen = FunktionslistenFormat.lese("\r\nf;x;#000000;TRUE\r\n\r\ng;2x;#ffffff;false\r\n");

        // Assert
        assertEquals(2, gelesen.size());
        assertEquals(Color.WHITE, gelesen.get(1).getFarbe());
    }

    @Test
    void lese_ShouldRejectBrokenLinesWithLineNumber()
    {
        // Act & Assert
        assertTrue(assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("f;x;#000000;true\ng;x;#000000"))
                .getMessage().startsWith("Zeile 2"));
        assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("f;x;rot;true"));
        assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("f;x;#000000;vielleicht"));
        assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("1f;x;#000000;true"));
        assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("x;x;#000000;true"));
    }

    @Test
    void lese_ShouldRejectDuplicateNamesAndEmptyFile()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("f;x;#000000;true\nF;2x;#000000;true"));
        assertEquals("Die Datei enthält keine Funktionen",
                assertThrows(IllegalArgumentException.class, () -> FunktionslistenFormat.lese("  \n\n")).getMessage());
    }
}
