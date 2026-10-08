package modes.wissenschaftlich.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZahlentheorieTest
{
    @Test
    void primfaktoren_ShouldWriteExponentsAsSuperscript()
    {
        // Act
        String ergebnis = Zahlentheorie.primfaktoren(360);

        // Assert
        assertEquals("360 = 2³ · 3² · 5", ergebnis);
    }

    @Test
    void primfaktoren_ShouldHandlePrimesAndLargeValues()
    {
        // Act & Assert
        assertEquals("2 = 2", Zahlentheorie.primfaktoren(2));
        assertEquals("97 = 97", Zahlentheorie.primfaktoren(97));
        assertEquals("1024 = 2¹⁰", Zahlentheorie.primfaktoren(1024));
        assertEquals("1000000000000000 = 2¹⁵ · 5¹⁵", Zahlentheorie.primfaktoren(1_000_000_000_000_000L));
        assertEquals("999999999999989 = 999999999999989", Zahlentheorie.primfaktoren(999_999_999_999_989L));
    }

    @Test
    void primfaktoren_ShouldRejectValuesOutsideRange()
    {
        // Act
        IllegalArgumentException eins = assertThrows(IllegalArgumentException.class, () -> Zahlentheorie.primfaktoren(1));

        // Assert
        assertEquals("Primfaktorzerlegung geht nur für ganze Zahlen von 2 bis 10¹⁵.", eins.getMessage());
        assertThrows(IllegalArgumentException.class, () -> Zahlentheorie.primfaktoren(-12));
        assertThrows(IllegalArgumentException.class, () -> Zahlentheorie.primfaktoren(1_000_000_000_000_001L));
    }
}
