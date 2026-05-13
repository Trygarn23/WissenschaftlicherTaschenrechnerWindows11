package common.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AusdruckNormalisierungTest
{
    @Test
    void zwischenablage_ShouldNormalizeWhitespaceOperatorsMinusAndGermanThousands()
    {
        assertEquals("1234,56+7", AusdruckNormalisierung.zwischenablage(" 1.234,56 + 7 "));
        assertEquals("2*3/4-5", AusdruckNormalisierung.zwischenablage("2 \u00d7 3 \u00f7 4 \u2212 5"));
    }

    @Test
    void verlaufErgebnis_ShouldNormalizeFormattedHistoryResult()
    {
        assertEquals("-1234,5", AusdruckNormalisierung.verlaufErgebnis("\u2212 1.234,5"));
    }

    @Test
    void normalisierung_ShouldKeepNullStable()
    {
        assertNull(AusdruckNormalisierung.zwischenablage(null));
        assertNull(AusdruckNormalisierung.verlaufErgebnis(null));
    }
}
