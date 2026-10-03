package common.formatting;

import common.formatting.ZahlenEingabe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZahlenEingabeTest
{
    @Test
    void lese_ShouldAcceptCommaAndPointWithSurroundingSpaces()
    {
        // Act & Assert
        assertEquals(3.5, ZahlenEingabe.lese(" 3,5 "));
        assertEquals(-0.25, ZahlenEingabe.lese("-0.25"));
        assertEquals(1.2e-5, ZahlenEingabe.lese("1,2e-5"));
    }

    @Test
    void lese_ShouldExplainInvalidOrMissingInputInGerman()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class, () -> ZahlenEingabe.lese("  "));
        IllegalArgumentException text = assertThrows(IllegalArgumentException.class, () -> ZahlenEingabe.lese("abc"));

        // Assert
        assertEquals("Bitte eine Zahl eingeben.", leer.getMessage());
        assertEquals("„abc“ ist keine gültige Zahl.", text.getMessage());
    }
}
