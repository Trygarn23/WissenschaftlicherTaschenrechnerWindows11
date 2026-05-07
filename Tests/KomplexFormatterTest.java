import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexeZahl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KomplexFormatterTest
{
    @Test
    void formatiere_ShouldFormatCartesianAndPolarRepresentations()
    {
        // Arrange
        KomplexFormatter formatter = new KomplexFormatter();
        KomplexeZahl zahl = new KomplexeZahl(3, -4);

        // Act & Assert
        assertEquals("3 - 4i", formatter.formatiere(zahl, KomplexDarstellung.KARTESISCH));
        assertEquals("5 ∠ -53,1301023542°", formatter.formatiere(zahl, KomplexDarstellung.POLAR_DEG));
    }

    @Test
    void formatiere_ShouldHandlePureRealPureImaginaryAndZero()
    {
        // Arrange
        KomplexFormatter formatter = new KomplexFormatter();

        // Act & Assert
        assertEquals("5 + 0i", formatter.formatiereKartesisch(new KomplexeZahl(5, 0)));
        assertEquals("0 + 7i", formatter.formatiereKartesisch(new KomplexeZahl(0, 7)));
        assertEquals("0 + 0i", formatter.formatiereKartesisch(new KomplexeZahl(0, 0)));
    }
}
