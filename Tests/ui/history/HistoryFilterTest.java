package ui.history;

import common.history.VerlaufEintrag;
import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryFilterTest
{
    private static final LocalDateTime ZEITPUNKT = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Test
    void leererSuchtextGibtAlleEintraegeZurueck()
    {
        List<VerlaufEintrag> entries = entries();

        assertEquals(entries, HistoryFilter.filter(entries, "", "Suche..."));
        assertEquals(entries, HistoryFilter.filter(entries, "Suche...", "Suche..."));
    }

    @Test
    void sucheNutztAusdruckErgebnisUndModus()
    {
        List<VerlaufEintrag> entries = entries();

        assertEquals(1, HistoryFilter.filter(entries, "2 + 2", "Suche...").size());
        assertEquals(1, HistoryFilter.filter(entries, "4", "Suche...").size());
        assertEquals(1, HistoryFilter.filter(entries, "Wissenschaftlich", "Suche...").size());
    }

    private List<VerlaufEintrag> entries()
    {
        return List.of(
                new VerlaufEintrag("2 + 2", "4", RechnerModus.STANDARD, ZEITPUNKT, false),
                new VerlaufEintrag("sin(1)", "0,017", RechnerModus.WISSENSCHAFTLICH, ZEITPUNKT, false)
        );
    }
}
