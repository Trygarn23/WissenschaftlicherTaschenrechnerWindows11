package modes.programmierer.logic;

import modes.programmierer.model.Ieee754Darstellung;
import modes.programmierer.model.Ieee754Darstellung.Art;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Ieee754ZerlegungTest
{
    @Test
    void alsFloat_ShouldSplitOne()
    {
        // Act
        Ieee754Darstellung d = Ieee754Zerlegung.alsFloat(1.0);

        // Assert
        assertEquals(0, d.vorzeichenBit());
        assertEquals("01111111", d.exponentBits());
        assertEquals("0".repeat(23), d.mantisseBits());
        assertEquals(127, d.bias());
        assertEquals(0, d.tatsaechlicherExponent());
        assertEquals(Art.NORMAL, d.art());
    }

    @Test
    void alsDouble_ShouldSplitMinusSixPointFive()
    {
        // Act
        Ieee754Darstellung d = Ieee754Zerlegung.alsDouble(-6.5);

        // Assert
        assertEquals(1, d.vorzeichenBit());
        assertEquals("10000000001", d.exponentBits());
        assertEquals("1010" + "0".repeat(48), d.mantisseBits());
        assertEquals(1023, d.bias());
        assertEquals(2, d.tatsaechlicherExponent());
    }

    @Test
    void alsFloat_ShouldShowRoundedValue_WhenNumberIsNotExact()
    {
        // Act
        Ieee754Darstellung d = Ieee754Zerlegung.alsFloat(0.1);

        // Assert
        assertEquals("01111011", d.exponentBits());
        assertEquals("10011001100110011001101", d.mantisseBits());
        assertEquals(-4, d.tatsaechlicherExponent());
        assertNotEquals(0.1, d.gespeicherterWert());
    }

    @Test
    void zerlegung_ShouldDistinguishPlusAndMinusZero()
    {
        // Act
        Ieee754Darstellung plus = Ieee754Zerlegung.alsDouble(0.0);
        Ieee754Darstellung minus = Ieee754Zerlegung.alsFloat(-0.0);

        // Assert
        assertEquals(Art.NULL, plus.art());
        assertEquals(0, plus.vorzeichenBit());
        assertEquals(Art.NULL, minus.art());
        assertEquals(1, minus.vorzeichenBit());
        assertNull(minus.tatsaechlicherExponent());
    }

    @Test
    void zerlegung_ShouldRecognizeInfinityAndNaN()
    {
        // Act
        Ieee754Darstellung unendlich = Ieee754Zerlegung.alsFloat(Double.NEGATIVE_INFINITY);
        Ieee754Darstellung nan = Ieee754Zerlegung.alsDouble(Double.NaN);

        // Assert
        assertEquals(Art.UNENDLICH, unendlich.art());
        assertEquals(1, unendlich.vorzeichenBit());
        assertEquals("11111111", unendlich.exponentBits());
        assertEquals(Art.NAN, nan.art());
        assertEquals("1".repeat(11), nan.exponentBits());
        assertNull(nan.tatsaechlicherExponent());
    }

    @Test
    void zerlegung_ShouldRecognizeSubnormalNumbers()
    {
        // Act
        Ieee754Darstellung floatKlein = Ieee754Zerlegung.alsFloat(Float.MIN_VALUE);
        Ieee754Darstellung doubleKlein = Ieee754Zerlegung.alsDouble(Double.MIN_VALUE);

        // Assert
        assertEquals(Art.SUBNORMAL, floatKlein.art());
        assertEquals("0".repeat(22) + "1", floatKlein.mantisseBits());
        assertEquals(-126, floatKlein.tatsaechlicherExponent());
        assertEquals(Art.SUBNORMAL, doubleKlein.art());
        assertEquals(-1022, doubleKlein.tatsaechlicherExponent());
    }

    @Test
    void alsFloat_ShouldBecomeInfinity_WhenDoubleIsTooLargeForFloat()
    {
        // Act
        Ieee754Darstellung d = Ieee754Zerlegung.alsFloat(1e300);

        // Assert
        assertEquals(Art.UNENDLICH, d.art());
        assertEquals(Double.POSITIVE_INFINITY, d.gespeicherterWert());
    }
}
