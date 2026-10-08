package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PruefungsModusTest
{
    @Test
    void istModusErlaubt_ShouldOnlyAllowStandardAndScientific_WhenActive()
    {
        for (RechnerModus modus : RechnerModus.values())
        {
            boolean erwartet = modus == RechnerModus.STANDARD || modus == RechnerModus.WISSENSCHAFTLICH;
            assertEquals(erwartet, PruefungsModus.istModusErlaubt(true, modus), modus.name());
            assertTrue(PruefungsModus.istModusErlaubt(false, modus), modus.name());
        }
    }

    @Test
    void erlaubterModus_ShouldFallBackToScientific_WhenModeIsBlocked()
    {
        assertEquals(RechnerModus.WISSENSCHAFTLICH, PruefungsModus.erlaubterModus(true, RechnerModus.GRAPH));
        assertEquals(RechnerModus.STANDARD, PruefungsModus.erlaubterModus(true, RechnerModus.STANDARD));
        assertEquals(RechnerModus.GRAPH, PruefungsModus.erlaubterModus(false, RechnerModus.GRAPH));
        assertFalse(PruefungsModus.sindWerkzeugeErlaubt(true));
    }
}
