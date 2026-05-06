import modes.programmierer.formatting.ProgrammiererFormatter;
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
}
