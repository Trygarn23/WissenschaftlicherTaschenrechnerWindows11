package modes.programmierer.formatting;

import modes.programmierer.formatting.ProgrammiererFormatter;
import modes.programmierer.logic.Ieee754Zerlegung;
import modes.programmierer.model.Wortbreite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProgrammiererFormatterTest
{
    private ProgrammiererFormatter formatter;

    @BeforeEach
    void setUp()
    {
        formatter = new ProgrammiererFormatter();
    }

    @Test
    void formatBinary_ShouldPadToWordWidthAndGroupNibbles_WhenWordWidthIsProvided()
    {
        assertEquals("0000 1111", formatter.formatBinary("1111", Wortbreite.BYTE));
        assertEquals("0000 0000 0000 1111", formatter.formatBinary("1111", Wortbreite.WORD));
    }

    @Test
    void formatHex_ShouldPadToWordWidthAndGroupLargeValues_WhenWordWidthIsProvided()
    {
        assertEquals("0F", formatter.formatHex("F", Wortbreite.BYTE));
        assertEquals("FFFF FFFF", formatter.formatHex("FFFFFFFF", Wortbreite.DWORD));
    }

    @Test
    void formatOct_ShouldGroupFromRight_WhenValueIsLong()
    {
        assertEquals("1 234 567", formatter.formatOct("1234567"));
    }

    @Test
    void formatZeichen_ShouldShowPrintableCharacterWithCodepoint()
    {
        assertEquals("Zeichen: 'A' (U+0041)", formatter.formatZeichen(65));
        assertEquals("Zeichen: '€' (U+20AC)", formatter.formatZeichen(0x20AC));
        assertEquals("Zeichen: '😀' (U+1F600)", formatter.formatZeichen(0x1F600));
    }

    @Test
    void formatZeichen_ShouldShowNameForControlCharacters()
    {
        assertEquals("Zeichen: NUL (U+0000)", formatter.formatZeichen(0));
        assertEquals("Zeichen: LF (U+000A)", formatter.formatZeichen(10));
        assertEquals("Zeichen: DEL (U+007F)", formatter.formatZeichen(127));
    }

    @Test
    void formatZeichen_ShouldShowDash_WhenNotRepresentable()
    {
        assertEquals("Zeichen: –", formatter.formatZeichen(0x110000));
        assertEquals("Zeichen: –", formatter.formatZeichen(-1));
        assertEquals("Zeichen: – (U+D800)", formatter.formatZeichen(0xD800));
        assertEquals("Zeichen: – (U+0085)", formatter.formatZeichen(0x85));
    }

    @Test
    void formatIeee754_ShouldDescribeSignExponentAndMantissa()
    {
        // Act
        String text = formatter.formatIeee754(Ieee754Zerlegung.alsFloat(-6.5));

        // Assert
        assertTrue(text.startsWith("float (32 Bit), gespeichert: -6.5"));
        assertTrue(text.contains("Vorzeichen  1  → −"));
        assertTrue(text.contains("Exponent    10000001  → 129 − 127 = 2"));
        assertTrue(text.contains("Mantisse    10100000000000000000000  → 1,"));
    }

    @Test
    void formatIeee754_ShouldNameSpecialCases()
    {
        assertTrue(formatter.formatIeee754(Ieee754Zerlegung.alsDouble(Double.NaN)).contains("NaN"));
        assertTrue(formatter.formatIeee754(Ieee754Zerlegung.alsDouble(Double.POSITIVE_INFINITY)).contains("Unendlich"));
        assertTrue(formatter.formatIeee754(Ieee754Zerlegung.alsDouble(Double.MIN_VALUE)).contains("subnormal, fest 1 − 1023 = -1022"));
        assertTrue(formatter.formatIeee754(Ieee754Zerlegung.alsDouble(-0.0)).contains("→ Null"));
    }
}
